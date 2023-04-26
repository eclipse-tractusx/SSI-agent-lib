package org.eclipse.tractusx.ssi.lib.did.web;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.did.web.util.Constants;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.DidWebException;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolver;

@RequiredArgsConstructor
public class DidWebDocumentResolver implements DidDocumentResolver {

  private final HttpClient client;
  private final DidWebParser parser;
  private final boolean enforceHttps;

  @Override
  public DidMethod getSupportedMethod() {
    return Constants.DID_WEB_METHOD;
  }

  @Override
  public DidDocument resolve(Did did) {
    if (!did.getMethod().equals(Constants.DID_WEB_METHOD))
      throw new SsiException(
          "Handler can only handle the following methods:" + Constants.DID_WEB_METHOD);

    final URI uri = parser.parse(did, enforceHttps);

    final HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

    try {
      final HttpResponse<String> response =
          client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() < 200 || response.statusCode() > 299) {
        throw new DidWebException(
            String.format(
                "Unexpected response when resolving did document [Code=%s, Payload=%s]",
                response.statusCode(), response.body()));
      }
      if (response.body() == null) {
        throw new DidWebException("Empty response body");
      }

      final byte[] body = response.body().getBytes(StandardCharsets.UTF_8);

      // TODO Fix this
      final ObjectMapper mapper = new ObjectMapper();
      final JsonNode didNode = mapper.readTree(body);

      final String id = didNode.get("id").asText();
      final JsonNode verificationMethodNode = didNode.get("verificationMethod");

      final List<Ed25519VerificationKey2020> keys = new ArrayList<>();
      if (verificationMethodNode.isArray()) {
        verificationMethodNode
            .elements()
            .forEachRemaining(
                jsonNode -> {
                  var key = parseKeyNode(did, jsonNode);
                  keys.add(key);
                });
      } else {
        Ed25519VerificationKey2020 key = parseKeyNode(did, verificationMethodNode);
        keys.add(key);
      }

      return DidDocument.builder().id(URI.create(id)).verificationMethods(keys).build();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private Ed25519VerificationKey2020 parseKeyNode(Did did, JsonNode verificationMethodNode) {
    final String verificationMethodType = verificationMethodNode.get("type").asText();
    final String verificationMethodId = verificationMethodNode.get("id").asText();
    final String verificationMethodController = verificationMethodNode.get("controller").asText();
    final String verificationMethodKey = verificationMethodNode.get("publicKeyMultibase").asText();

    if (!Objects.equals(verificationMethodType, Ed25519VerificationKey2020.TYPE)) {
      // TODO LOG
      //      monitor.warning(
      //          String.format(
      //              "Skipped unsupported verification key type in DID '%s'. Supported Types:
      // [%s]",
      //              did, Ed25519VerificationKey2020.TYPE));
    }

    return Ed25519VerificationKey2020.builder()
        .id(URI.create(verificationMethodId))
        .controller(URI.create(verificationMethodController))
        .multibase(MultibaseFactory.create(verificationMethodKey))
        .build();
  }
}
