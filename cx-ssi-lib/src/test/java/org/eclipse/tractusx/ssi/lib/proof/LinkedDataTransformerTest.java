package org.eclipse.tractusx.ssi.lib.proof;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.Proof;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LinkedDataTransformerTest {

  private final LinkedDataTransformer linkedDataTransformer = new LinkedDataTransformer();

  @Test
  @SneakyThrows
  public void testLinkedDataTransformer() {
    final VerifiableCredentialBuilder verifiableCredentialBuilder =
        new VerifiableCredentialBuilder();

    final VerifiableCredentialSubject verifiableCredentialSubject =
        new VerifiableCredentialSubject(Map.of("test", "test"));
    final VerifiableCredential credentialWithoutProof =
        verifiableCredentialBuilder
            .id(URI.create("did:test:id"))
            .type(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .issuer(URI.create("did:test:isser"))
            .expirationDate(Instant.now().plusSeconds(3600))
            .issuanceDate(Instant.now())
            .credentialSubject(verifiableCredentialSubject)
            .build();

    var transformedWithoutProof = linkedDataTransformer.transform(credentialWithoutProof);

    final VerifiableCredential verifiableCredentialWithProof =
        verifiableCredentialBuilder.proof(new Proof(Map.of(Proof.TYPE, "foo"))).build();

    var transformedWithProof = linkedDataTransformer.transform(verifiableCredentialWithProof);

    Assertions.assertEquals(transformedWithProof.getValue(), transformedWithoutProof.getValue());
  }
}
