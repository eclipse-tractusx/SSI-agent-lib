package org.eclipse.tractusx.ssi.lib.proof;

import java.net.URI;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.Ed25519Signature2020Builder;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.transform.TransformedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataSigner;

@RequiredArgsConstructor
public class LinkedDataProofGenerator {

  public static LinkedDataProofGenerator create() {
    return new LinkedDataProofGenerator(
        new LinkedDataHasher(), new LinkedDataTransformer(), new LinkedDataSigner());
  }

  private final LinkedDataHasher hasher;
  private final LinkedDataTransformer transformer;
  private final LinkedDataSigner signer;

  public Ed25519Signature2020 createEd25519Signature2020(
      VerifiableCredential verifiableCredential, URI verificationMethodId, byte[] signingKey) {
    final TransformedLinkedData transformedData = transformer.transform(verifiableCredential);
    final HashedLinkedData hashedData = hasher.hash(transformedData);
    final byte[] signature = signer.sign(hashedData, signingKey);
    final MultibaseString multibaseString = MultibaseFactory.create(signature);

    return new Ed25519Signature2020Builder()
        .proofPurpose(Ed25519Signature2020.PROOF_PURPOSE)
        .proofValue(multibaseString.getEncoded())
        .verificationMethod(verificationMethodId)
        .created(Instant.now())
        .build();
  }
}
