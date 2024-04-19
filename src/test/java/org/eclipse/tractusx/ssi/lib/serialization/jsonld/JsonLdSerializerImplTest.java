package org.eclipse.tractusx.ssi.lib.serialization.jsonld;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationType;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.eclipse.tractusx.ssi.lib.util.vc.TestVerifiableFactory;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

class JsonLdSerializerImplTest {

  @Test
  @SneakyThrows
  void shouldSerialize() {
    TestIdentity credentialIssuer = TestIdentityFactory.newIdentityWithEDVerificationMethod();

    final VerifiableCredential credential =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, null);

    final VerifiablePresentation verifiablePresentation =
        new VerifiablePresentationBuilder()
            .id(URI.create("did:localhost"))
            .type(List.of(VerifiablePresentationType.VERIFIABLE_PRESENTATION))
            .verifiableCredentials(List.of(credential))
            .build();

    SerializedVerifiablePresentation serializedVerifiablePresentation =
        new SerializedVerifiablePresentation(
            new ObjectMapper().writeValueAsString(verifiablePresentation));

    JsonLdSerializerImpl serializer = new JsonLdSerializerImpl();
    // with validation
    assertDoesNotThrow(() -> serializer.deserializePresentation(serializedVerifiablePresentation));

    // without validation
    assertDoesNotThrow(
        () -> serializer.deserializePresentation(serializedVerifiablePresentation, false));
  }
}
