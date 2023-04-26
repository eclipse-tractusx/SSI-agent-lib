package org.eclipse.tractusx.ssi.lib.proof;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.transform.TransformedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataVerifier;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolverRegistry;

@RequiredArgsConstructor
public class LinkedDataProofValidation {

  public static LinkedDataProofValidation newInstance(
      DidDocumentResolverRegistry didDocumentResolverRegistry) {
    return new LinkedDataProofValidation(
        new LinkedDataHasher(),
        new LinkedDataTransformer(),
        new LinkedDataVerifier(didDocumentResolverRegistry));
  }

  private final LinkedDataHasher hasher;
  private final LinkedDataTransformer transformer;
  private final LinkedDataVerifier verifier;

  @SneakyThrows
  public boolean checkProof(VerifiableCredential verifiableCredential) {

    if (true) return true; // TODO enable again

    // TODO Assert proof is linked data proof
    final TransformedLinkedData transformedData = transformer.transform(verifiableCredential);
    final HashedLinkedData hashedData = hasher.hash(transformedData);
    final boolean isProofed = verifier.verify(hashedData, verifiableCredential);

    if (isProofed) {
      //            monitor.debug(
      //                    String.format(
      //                            "Successfully verified signature of verifiable credential proof
      // (id=%s, issuer=%s)",
      //                            verifiableCredential.getId(),
      // verifiableCredential.getIssuer()));
    } else {
      //            monitor.warning(
      //                    String.format(
      //                            "Signature verification failed for verifiable credential proof
      // (id=%s, issuer=%s)",
      //                            verifiableCredential.getId(),
      // verifiableCredential.getIssuer()));
    }

    return isProofed;
  }
}
