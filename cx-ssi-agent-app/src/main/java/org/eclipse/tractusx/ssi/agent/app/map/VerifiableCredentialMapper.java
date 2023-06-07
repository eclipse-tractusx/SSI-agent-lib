package org.eclipse.tractusx.ssi.agent.app.map;

import java.util.*;
import java.util.stream.Collectors;
import org.eclipse.tractusx.ssi.agent.model.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;

public class VerifiableCredentialMapper {

  public static VerifiableCredential map(
      org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential
          verifiableCredential) {
    Objects.requireNonNull(verifiableCredential, "VerifiableCredential must not be null");

    List<Map> verifiableCredentialSubject =
        new ArrayList<>(verifiableCredential.getCredentialSubject());

    final VerifiableCredential credential = new VerifiableCredential();
    credential.atContext(verifiableCredential.getContext());
    credential.id(verifiableCredential.getId());
    credential.type(verifiableCredential.getTypes());
    credential.credentialSubject(verifiableCredentialSubject);
    credential.issuer(verifiableCredential.getIssuer());
    credential.issuanceDate(
        verifiableCredential.getIssuanceDate().atOffset(java.time.ZoneOffset.UTC));
    credential.expirationDate(
        verifiableCredential.getExpirationDate().atOffset(java.time.ZoneOffset.UTC));
    credential.proof(verifiableCredential.getProof());

    return credential;
  }

  public static org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential map(
      VerifiableCredential verifiableCredential) {
    Objects.requireNonNull(verifiableCredential, "VerifiableCredential must not be null");

    final List<VerifiableCredentialSubject> verifiableCredentialSubject =
        verifiableCredential.getCredentialSubject().stream()
            .map(VerifiableCredentialSubject::new)
            .collect(Collectors.toList());
    final Proof proof =
        Optional.ofNullable(verifiableCredential.getProof()).map(Proof::new).orElse(null);

    return new VerifiableCredentialBuilder()
        .context(verifiableCredential.getAtContext())
        .id(verifiableCredential.getId())
        .type(verifiableCredential.getType())
        .issuer(verifiableCredential.getIssuer())
        .issuanceDate(verifiableCredential.getIssuanceDate().toInstant())
        .expirationDate(verifiableCredential.getExpirationDate().toInstant())
        .credentialSubject(verifiableCredentialSubject)
        .proof(proof)
        .build();
  }
}
