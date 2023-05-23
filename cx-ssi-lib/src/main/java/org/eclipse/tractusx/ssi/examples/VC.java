package org.eclipse.tractusx.ssi.examples;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.model.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofGenerator;

public class VC {
  public static VerifiableCredential createVCWithoutProof() {

    // VC Bulider
    final VerifiableCredentialBuilder verifiableCredentialBuilder =
        new VerifiableCredentialBuilder();

    // VC Subject
    final VerifiableCredentialSubject verifiableCredentialSubject =
        new VerifiableCredentialSubject(Map.of("test", "test"));

    // Using Builder
    final VerifiableCredential credentialWithoutProof =
        verifiableCredentialBuilder
            .id(URI.create("did:test:id"))
            .type(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .issuer(URI.create("did:test:isser"))
            .expirationDate(Instant.now().plusSeconds(3600))
            .issuanceDate(Instant.now())
            .credentialSubject(verifiableCredentialSubject)
            .build();

    return credentialWithoutProof;
  }

  public static VerifiableCredential createVCWithProof(
      VerifiableCredential credential, byte[] privateKey, Did issuer) {

    // VC Builder
    final VerifiableCredentialBuilder builder =
        new VerifiableCredentialBuilder()
            .context(credential.getContext())
            .id(credential.getId())
            .issuer(issuer.toUri())
            .issuanceDate(Instant.now())
            .credentialSubject(credential.getCredentialSubject())
            .expirationDate(credential.getExpirationDate())
            .type(credential.getTypes());

    // Ed25519 Proof Builder
    final LinkedDataProofGenerator generator = LinkedDataProofGenerator.create();
    final Ed25519Signature2020 proof =
        generator.createEd25519Signature2020(
            builder.build(), URI.create(issuer + "#key-1"), privateKey);

    // Adding Proof to VC
    builder.proof(proof);

    return builder.build();
  }
}
