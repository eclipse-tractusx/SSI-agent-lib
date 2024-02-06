package org.eclipse.tractusx.ssi.lib.util.identity;

import lombok.Builder;
import lombok.Getter;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.model.ProofPurpose;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.VerificationMethod;

@Builder
@Getter
public class TestIdentityConfig {

  private final Did did;

  private final DidDocument didDocument;

  private final IPublicKey publicKey;

  private final IPrivateKey privateKey;

  private IPublicKey authenticationPublicKey;

  private IPrivateKey authenticationPrivateKey;

  private VerificationMethod authenticationVerificationMethod;

  private IPublicKey assertionMethodPublicKey;

  private IPrivateKey assertionMethodPrivateKey;

  private VerificationMethod assertionMethodVerificationMethod;

  public TestIdentity toTestIdentity(ProofPurpose proofPurpose) {
    switch (proofPurpose) {
      case AUTHENTICATION:
        return new TestIdentity(
            did, didDocument, authenticationPublicKey, authenticationPrivateKey);
      case ASSERTION_METHOD:
        return new TestIdentity(
            did, didDocument, assertionMethodPublicKey, assertionMethodPrivateKey);
      default:
        return new TestIdentity(did, didDocument, publicKey, privateKey);
    }
  }
}
