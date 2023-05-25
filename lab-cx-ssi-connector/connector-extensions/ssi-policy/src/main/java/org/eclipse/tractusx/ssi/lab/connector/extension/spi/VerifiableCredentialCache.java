package org.eclipse.tractusx.ssi.lab.connector.extension.spi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import lombok.*;
import org.eclipse.edc.protocol.ids.spi.types.MessageProtocol;
import org.eclipse.edc.spi.message.MessageContext;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.exceptions.VerifiableCredentialNotFoundException;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.message.VerifiableCredentialRequest;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiableCredential;

// TODO also cache if other connector could not provide verifiable credential

@RequiredArgsConstructor
public class VerifiableCredentialCache {

  private final RemoteMessageDispatcherRegistry remoteMessageDispatcherRegistry;
  private final int cacheExpirySeconds;
  private final Monitor monitor;
  private final Map<URI, CacheEntry> cacheByIdsUrl = new ConcurrentHashMap<>();
  private final ConcurrentHashMap<String, Object> locks = new ConcurrentHashMap<>();

  public List<VerifiableCredential> get(URI idsUrl, List<String> verifiableCredentialTypes)
      throws VerifiableCredentialNotFoundException {
    if (!cacheByIdsUrl.containsKey(idsUrl)) cacheByIdsUrl.put(idsUrl, new CacheEntry());

    updateMissingOrExpiredCredentials(idsUrl, verifiableCredentialTypes);

    // prevent asking twice if not exists
    final List<VerifiableCredential> cachedCredentials =
        cacheByIdsUrl.get(idsUrl).getVerifiableCredential().stream()
            .map(CachedVerifiableCredential::getVerifiableCredential)
            .collect(Collectors.toList());

    final List<VerifiableCredential> verifiableCredentials = new ArrayList<>();
    final List<String> notFoundCredentialTypes = new ArrayList<>();

    for (final String type : verifiableCredentialTypes) {
      final List<VerifiableCredential> credentials =
          cachedCredentials.stream()
              .filter(vc -> vc.getTypes().contains(type))
              .collect(Collectors.toList());

      if (credentials.isEmpty()) {
        notFoundCredentialTypes.add(type);
      } else {
        verifiableCredentials.addAll(credentials);
      }
    }

    if (!notFoundCredentialTypes.isEmpty()) {
      throw new VerifiableCredentialNotFoundException(notFoundCredentialTypes);
    }
    return verifiableCredentials;
  }

  private void updateMissingOrExpiredCredentials(
      URI idsUrl, List<String> verifiableCredentialTypes) {

    synchronized (locks.computeIfAbsent(idsUrl.toString(), k -> new Object())) {
      removeExpiredCredentials(idsUrl);

      final List<String> cachedCredentialTypes =
          cacheByIdsUrl.get(idsUrl).getVerifiableCredential().stream()
              .map(CachedVerifiableCredential::getVerifiableCredential)
              .map(VerifiableCredential::getTypes)
              .flatMap(List::stream)
              .collect(Collectors.toList());

      final List<String> credentialTypesToRequest = new ArrayList<>(verifiableCredentialTypes);
      credentialTypesToRequest.removeAll(cachedCredentialTypes);

      monitor.info(
          String.format(
              "Requesting credentials from %s for types [%s]",
              idsUrl, String.join(", ", credentialTypesToRequest)));
      final RemoteMessage message =
          new VerifiableCredentialRequest(
              idsUrl.toString(), MessageProtocol.IDS_MULTIPART, credentialTypesToRequest);
      final CompletableFuture<List> future =
          remoteMessageDispatcherRegistry.send(List.class, message, new MyMessageContext());

      // TODO handle bad responses
      final List<VerifiableCredential> verifiableCredentials;

      try {
        final SignedJWT jwtPresentation = (SignedJWT) future.get(); // TODO add normal jwt check
        final Map<String, Object> payloadVp =
            (Map<String, Object>) jwtPresentation.getPayload().toJSONObject().get("vp");
        if (!payloadVp.containsKey("verifiableCredential")) {
          monitor.warning("Received verifiable credentials without vp.verifiableCredential");
          verifiableCredentials = new ArrayList<>();
        } else {

          final Map<String, Object> verifiablePresentation =
              jwtPresentation.getJWTClaimsSet().getJSONObjectClaim("vp");

          com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap vc =
              (com.nimbusds.jose.shaded.gson.internal.LinkedTreeMap)
                  verifiablePresentation.get("verifiableCredential");

          var vpClaimValueSerialized =
              new ObjectMapper().writeValueAsString(verifiablePresentation);
          monitor.info("Received verifiable presentation: " + vpClaimValueSerialized);

          Map<String, Object> vp = new ObjectMapper().readValue(vpClaimValueSerialized, Map.class);

          var vcsSerialized = new ObjectMapper().writeValueAsString(vp.get("verifiableCredential"));
          monitor.info("Received verifiable credentials: " + vcsSerialized);
          // TODO this code covers the case where only one VC is returned, cover the case for an
          // array, too
          Map<String, Object> vcs = new ObjectMapper().readValue(vcsSerialized, Map.class);

          verifiableCredentials = List.of(new VerifiableCredential(vcs));
        }
      } catch (InterruptedException e) {
        monitor.warning("Interrupted while waiting for verifiable credentials. " + e);
        throw new RuntimeException(e);
      } catch (ExecutionException e) {
        monitor.warning("Error while waiting for verifiable credentials. " + e);
        throw new RuntimeException(e);
      } catch (ParseException e) {
        monitor.warning("Error while parsing verifiable credentials. " + e);
        throw new RuntimeException(e);
      } catch (JsonProcessingException e) {
        throw new RuntimeException(e);
      }

      monitor.info(
          String.format(
              "Received verifiable credentials from %s. Types [%s]",
              idsUrl,
              verifiableCredentials.stream()
                  .flatMap(c -> c.getTypes().stream())
                  .map(Object::toString)
                  .collect(Collectors.joining(", "))));

      cacheByIdsUrl
          .get(idsUrl)
          .getVerifiableCredential()
          .addAll(
              verifiableCredentials.stream()
                  .map(CachedVerifiableCredential::new)
                  .collect(Collectors.toList()));
    }
  }

  private void removeExpiredCredentials(URI idsUrl) {
    final CacheEntry cacheEntry = cacheByIdsUrl.get(idsUrl);
    if (cacheEntry != null) {
      cacheEntry.getVerifiableCredential().removeIf(this::isExpired);
    }
  }

  private boolean isExpired(CachedVerifiableCredential vc) {
    return vc.getFetchedAt().plusSeconds(cacheExpirySeconds).isBefore(Instant.now());
  }

  @Data
  private static class CacheEntry {
    private final List<CachedVerifiableCredential> verifiableCredential = new ArrayList<>();
    private Instant fetchedAt = Instant.now();
  }

  @Data
  private static class CachedVerifiableCredential {
    private final VerifiableCredential verifiableCredential;
    private Instant fetchedAt = Instant.now();
  }

  @Value
  private static class MyMessageContext implements MessageContext {
    String processId = "0";
  }
}
