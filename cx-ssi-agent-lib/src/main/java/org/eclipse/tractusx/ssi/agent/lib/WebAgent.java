package org.eclipse.tractusx.ssi.agent.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.net.http.HttpClient;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialAlreadyStoredException;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialNotFoundException;
import org.eclipse.tractusx.ssi.agent.lib.jwt.JwtFactory;
import org.eclipse.tractusx.ssi.agent.lib.jwt.JwtReader;
import org.eclipse.tractusx.ssi.agent.lib.wallet.SsiMemoryStorageWallet;
import org.eclipse.tractusx.ssi.agent.lib.wallet.VerifiableCredentialWallet;
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519Key;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519KeySet;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebDocumentResolver;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.InvalidJsonLdException;
import org.eclipse.tractusx.ssi.lib.exception.JwtException;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.did.*;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidDocumentResolverRegistryImpl;

public class WebAgent {

  // TODO Test Signing Key on initialize
  private final Ed25519KeySet signingKeySet;
  private final String hostName;
  private final JwtReader jwtReader;
  private final JwtFactory jwtFactory;

  public WebAgent(String hostName, Ed25519KeySet signingKeySet) {
    this(hostName, signingKeySet, HttpClient.newHttpClient(), true);
  }

  public WebAgent(
      String hostName, Ed25519KeySet signingKeySet, boolean enforceHttpsToResolveDidWeb) {
    this(hostName, signingKeySet, HttpClient.newHttpClient(), enforceHttpsToResolveDidWeb);
  }

  public WebAgent(
      String hostName,
      Ed25519KeySet signingKeySet,
      HttpClient httpClient,
      boolean enforceHttpsToResolveDidWeb) {
    this.hostName = hostName;
    this.signingKeySet = signingKeySet;
    var didDocumentResolverRegistry = new DidDocumentResolverRegistryImpl();
    didDocumentResolverRegistry.register(
        new DidWebDocumentResolver(httpClient, new DidWebParser(), enforceHttpsToResolveDidWeb));
    jwtReader = JwtReader.newInstance(didDocumentResolverRegistry);

    final URI issuerDid = getDidDocument().getId();
    jwtFactory =
        new JwtFactory(DidParser.parse(issuerDid), new Ed25519Key(signingKeySet.getPrivateKey()));

    SsiLibrary.initialize();
  }

  @Builder.Default private final VerifiableCredentialWallet wallet = new SsiMemoryStorageWallet();

  public Did getDid() {
    return DidWebFactory.fromHostname(hostName);
  }

  public DidDocument getDidDocument() {
    final Did did = DidWebFactory.fromHostname(hostName);

    final List<VerificationMethod> verificationMethods = new ArrayList<>();
    final byte[] publicKey = signingKeySet.getPublicKey();
    final MultibaseString publicKeyBase = MultibaseFactory.create(publicKey);
    final Ed25519VerificationKey2020Builder builder = new Ed25519VerificationKey2020Builder();
    final Ed25519VerificationKey2020 key =
        builder
            .id(URI.create(did.toUri() + "#key-" + 1))
            .controller(did.toUri())
            .publicKeyMultiBase(publicKeyBase)
            .build();
    verificationMethods.add(key);

    final DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
    didDocumentBuilder.id(did.toUri());
    didDocumentBuilder.verificationMethods(verificationMethods);

    return didDocumentBuilder.build();
  }

  public void storeCredential(VerifiableCredential verifiableCredential)
      throws CredentialAlreadyStoredException {
    wallet.storeCredential(verifiableCredential);
  }

  public VerifiableCredential getCredentialById(URI id) throws CredentialNotFoundException {
    return wallet.getCredentialById(id);
  }

  public VerifiableCredential getCredentialByType(String type) throws CredentialNotFoundException {
    return wallet.getCredentialByType(type);
  }

  public List<VerifiableCredential> getAllCredentials() {
    return wallet.getAllCredentials();
  }

  public SignedJWT issuePresentationAsJwt(List<VerifiableCredential> credentials, String audience) {
    return jwtFactory.createJwt(credentials, audience);
  }

  public VerifiablePresentation readCredentials(
      SignedJWT verifiablePresentation, String expectedAudience) {
    try {
      return jwtReader.read(
          verifiablePresentation, expectedAudience, false); // TODO make configurable
    } catch (ParseException e) {
      throw new RuntimeException(e);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    } catch (DidDocumentResolverNotRegisteredException e) {
      throw new RuntimeException(e);
    } catch (JwtException e) {
      throw new RuntimeException(e);
    } catch (InvalidJsonLdException e) {
      throw new RuntimeException(e);
    }
  }
}
