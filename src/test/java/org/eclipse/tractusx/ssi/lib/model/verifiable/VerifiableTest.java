package org.eclipse.tractusx.ssi.lib.model.verifiable;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.junit.jupiter.api.Test;

class VerifiableTest {

  @Test
  void testEqual() {
    final VerifiableCredentialSubject verifiableCredentialSubject =
        new VerifiableCredentialSubject(Map.of("test", "test"));

    final VerifiableCredentialBuilder verifiableCredentialBuilder =
        new VerifiableCredentialBuilder();
    // Using Builder
    VerifiableCredential one =
        verifiableCredentialBuilder
            .id(URI.create("did:test:id"))
            .type(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .issuer(URI.create("did:test:isser"))
            .expirationDate(Instant.now().plusSeconds(3600))
            .issuanceDate(Instant.now())
            .credentialSubject(verifiableCredentialSubject)
            .build();

    VerifiableCredential two =
        verifiableCredentialBuilder
            .id(URI.create("did:test:id"))
            .type(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .issuer(URI.create("did:test:isser"))
            .expirationDate(Instant.now().plusSeconds(3600))
            .issuanceDate(Instant.now())
            .credentialSubject(verifiableCredentialSubject)
            .build();

    assertEquals(one, two);
  }
}
