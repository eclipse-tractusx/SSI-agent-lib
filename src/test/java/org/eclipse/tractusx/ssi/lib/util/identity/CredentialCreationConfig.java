package org.eclipse.tractusx.ssi.lib.util.identity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.model.ProofPurpose;
import org.eclipse.tractusx.ssi.lib.model.did.VerificationMethod;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;

@RequiredArgsConstructor
@Getter
public class CredentialCreationConfig {
  final ProofPurpose proofPurpose;
  final VerificationMethod verificationMethod;
  final IPrivateKey privateKey;
  final SignatureType signatureType;
}
