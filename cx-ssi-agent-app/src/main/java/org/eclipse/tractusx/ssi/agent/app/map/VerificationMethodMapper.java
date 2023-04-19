package org.eclipse.tractusx.ssi.agent.app.map;

import org.eclipse.tractusx.ssi.agent.model.DidDocumentVerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationKey2020;

public class VerificationMethodMapper {

  public static DidDocumentVerificationMethod map(
      Ed25519VerificationKey2020 ed25519VerificationKey2020) {
    var verificationMethod = new DidDocumentVerificationMethod();
    verificationMethod.setId(ed25519VerificationKey2020.getId().toString());
    verificationMethod.setType(Ed25519VerificationKey2020.TYPE);
    verificationMethod.setController(ed25519VerificationKey2020.getController().toString());
    verificationMethod.setPublicKeyBase58(ed25519VerificationKey2020.getMultibase().getEncoded());
    return verificationMethod;
  }
}
