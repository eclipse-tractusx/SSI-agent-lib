package org.eclipse.tractusx.ssi.vcissuer.jsonLd;

import info.weboftrust.ldsignatures.LdProof;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.PackagePrivate;
import org.eclipse.tractusx.ssi.vcissuer.exception.SsiException;
import org.eclipse.tractusx.ssi.vcissuer.spi.Ed25519Proof;
import org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential.VerifiableCredentialType;

import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

@PackagePrivate
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DanubTechMapper {
    @NonNull
    @SneakyThrows
    public static com.danubetech.verifiablecredentials.VerifiableCredential map(
            VerifiableCredential credential) {

        if (!credential.getTypes().stream()
                .anyMatch(x -> x.equals(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))) {
            throw new SsiException("Type: VerifiableCredential missing");
        }
        // Verifiable Credential Type is automatically added in Builder
        final List<String> types =
                credential.getTypes().stream()
                        .filter(t -> !t.equals(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
                        .collect(Collectors.toList());

        final  com.danubetech.verifiablecredentials.CredentialSubject subject = map(credential.getCredentialSubject());

        return com.danubetech.verifiablecredentials.VerifiableCredential.builder()
                .defaultContexts(true)
                .forceContextsArray(true)
                .forceTypesArray(true)
                .id(credential.getId())
                .types(types)
                .issuer(credential.getIssuer())
                .issuanceDate(Date.from(credential.getIssuanceDate()))
                .expirationDate(Date.from(credential.getExpirationDate()))
                .credentialSubject(subject)
                .ldProof(map(credential.getProof()))
                .build();
    }

    public static com.danubetech.verifiablecredentials.CredentialSubject map(VerifiableCredentialSubject subject) {
        return com.danubetech.verifiablecredentials.CredentialSubject.builder().properties(subject).build();
    }

    private static LdProof map(Ed25519Proof proof) {
        if (proof == null) return null;

        if (!proof.getType().equals(Ed25519Proof.TYPE)) {
            throw new RuntimeException(
                    String.format(
                            "Proof not supported: %s. Supported Proofs: %s",
                            proof.getType(), Ed25519Proof.TYPE));
        }

        return LdProof.builder()
                .created(Date.from(proof.getCreated()))
                .type(proof.getType())
                .proofPurpose(proof.getProofPurpose())
                .proofValue(proof.getProofValue().getEncoded())
                .verificationMethod(proof.getVerificationMethod())
                .build();
    }
}
