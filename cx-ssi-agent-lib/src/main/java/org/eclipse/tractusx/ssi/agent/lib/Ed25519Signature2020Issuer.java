package org.eclipse.tractusx.ssi.agent.lib;

import java.net.URI;
import java.time.Instant;
import org.eclipse.tractusx.ssi.lib.model.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofGenerator;

public class Ed25519Signature2020Issuer {

  public VerifiableCredential createEd25519Signature(
      VerifiableCredential credential, byte[] privateKey, Did issuer) {

    final VerifiableCredentialBuilder builder =
        new VerifiableCredentialBuilder()
            .context(credential.getContext())
            .id(credential.getId())
            .issuer(issuer.toUri())
            .issuanceDate(Instant.now())
            .credentialSubject(credential.getCredentialSubject())
            .expirationDate(credential.getExpirationDate())
            .type(credential.getTypes());

    final LinkedDataProofGenerator generator = LinkedDataProofGenerator.create();
    final Ed25519Signature2020 proof =
        generator.createEd25519Signature2020(
            builder.build(), URI.create(issuer + "#key-1"), privateKey);

    return builder.proof(proof).build();
  }
}
