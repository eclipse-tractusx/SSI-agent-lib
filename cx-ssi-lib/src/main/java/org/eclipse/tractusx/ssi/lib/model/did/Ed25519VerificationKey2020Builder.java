package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;

@NoArgsConstructor
public class Ed25519VerificationKey2020Builder {
  private URI id;
  private URI controller;
  private String publicKeyMultiBase;

  public Ed25519VerificationKey2020Builder id(URI id) {
    this.id = id;
    return this;
  }

  public Ed25519VerificationKey2020Builder controller(URI controller) {
    this.controller = controller;
    return this;
  }

  public Ed25519VerificationKey2020Builder publicKeyMultiBase(MultibaseString multibaseString) {
    this.publicKeyMultiBase = multibaseString.getEncoded();
    return this;
  }

  public Ed25519VerificationKey2020 build() {
    return new Ed25519VerificationKey2020(
        Map.of(
            Ed25519VerificationKey2020.ID, id,
            Ed25519VerificationKey2020.TYPE, Ed25519VerificationKey2020.DEFAULT_TYPE,
            Ed25519VerificationKey2020.CONTROLLER, controller,
            Ed25519VerificationKey2020.PUBLIC_KEY_BASE_58, publicKeyMultiBase));
  }
}
