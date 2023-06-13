package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.controller;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.agent.lib.WebAgent;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationMethod;

@Produces({MediaType.APPLICATION_JSON})
@Path("/did.json")
@RequiredArgsConstructor
public class DidWebDocumentController {

  public static final String DOCUMENT =
      "{"
          + "\"@context\": [ "
          + "   \"https://www.w3.org/ns/did/v1\""
          + "],"
          + "\"id\": \"<did>\","
          + "\"verificationMethod\": [{"
          + "   \"id\": \"<did>#key-1\","
          + "   \"type\": \"Ed25519VerificationKey2020\","
          + "   \"controller\": \"<did>\","
          + "   \"publicKeyMultibase\": \"<key>\""
          + "}]"
          + "}";

  private final WebAgent webAgent;

  @SneakyThrows
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public String getDocument() {

    final DidDocument didDocument = webAgent.getDidDocument();

    return DOCUMENT
        .replace("<did>", didDocument.getId().toString())
        .replace(
            "<key>",
            didDocument.getVerificationMethods().stream()
                .filter(Ed25519VerificationMethod::isInstance)
                .findFirst()
                .map(Ed25519VerificationMethod::new)
                .map(v -> v.getPublicKeyBase58().getEncoded())
                .orElseThrow());
  }
}
