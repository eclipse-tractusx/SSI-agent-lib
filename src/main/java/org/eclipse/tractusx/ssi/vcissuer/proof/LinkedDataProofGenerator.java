package org.eclipse.tractusx.ssi.vcissuer.proof;

import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.vcissuer.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.vcissuer.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.vcissuer.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.vcissuer.proof.verify.LinkedDataSigner;
import org.eclipse.tractusx.ssi.vcissuer.spi.Ed25519Proof;
import org.eclipse.tractusx.ssi.vcissuer.spi.MultibaseString;
import org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential.VerifiableCredential;

import java.net.URI;
import java.time.Instant;

@RequiredArgsConstructor
public class LinkedDataProofGenerator {

    public static LinkedDataProofGenerator create() {
        return new LinkedDataProofGenerator(
                new LinkedDataHasher(),
                new LinkedDataTransformer(),
                new LinkedDataSigner()
        );
    }

    private final LinkedDataHasher hasher;
    private final LinkedDataTransformer transformer;
    private final LinkedDataSigner signer;

    public Ed25519Proof createProof(
            VerifiableCredential verifiableCredential, URI verificationMethodId, byte[] signingKey) {
        var transformedData = transformer.transform(verifiableCredential);
        var hashedData = hasher.hash(transformedData);
        var signature = signer.sign(hashedData, signingKey);
        MultibaseString multibaseString = MultibaseFactory.create(signature);

        return Ed25519Proof.builder()
                .created(Instant.now())
                .verificationMethod(verificationMethodId)
                .proofValue(multibaseString)
                .build();
    }
}
