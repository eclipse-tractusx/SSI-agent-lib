package org.eclipse.tractusx.ssi.agent.app.map;

import org.eclipse.tractusx.ssi.agent.model.DidDocumentVerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.lib.model.did.VerificationMethod;

public class VerificationMethodMapper {

  public static DidDocumentVerificationMethod map(VerificationMethod v) {

    if (!Ed25519VerificationKey2020.isInstance(v)) {
      throw new UnsupportedOperationException(
          "Unsupported verification method type: " + v.getType());
    }

    final Ed25519VerificationKey2020 ed25519VerificationKey2020 = new Ed25519VerificationKey2020(v);
    final DidDocumentVerificationMethod verificationMethod = new DidDocumentVerificationMethod();
    verificationMethod.setId(ed25519VerificationKey2020.getId().toString());
    verificationMethod.setType(Ed25519VerificationKey2020.DEFAULT_TYPE);
    verificationMethod.setController(ed25519VerificationKey2020.getController().toString());
    verificationMethod.setPublicKeyMultibase(
        ed25519VerificationKey2020.getPublicKeyBase58().getEncoded());
    return verificationMethod;
  }
}
