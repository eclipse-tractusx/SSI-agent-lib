package org.eclipse.tractusx.ssi.lib.model.did;

import java.util.Map;
import java.util.Objects;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.util.SerializeUtil;

@ToString
public class Ed25519VerificationKey2020 extends VerificationMethod {
  public static final String DEFAULT_TYPE = "Ed25519VerificationKey2020";

  public static final String PUBLIC_KEY_BASE_58 = "publicKeyMultibase";

  public static boolean isInstance(Map<String, Object> json) {
    return DEFAULT_TYPE.equals(json.get(TYPE));
  }

  public Ed25519VerificationKey2020(Map<String, Object> json) {
    super(json);

    if (!DEFAULT_TYPE.equals(this.getType())) {
      throw new IllegalArgumentException(
          String.format("Invalid type %s. Expected %s", this.getType(), DEFAULT_TYPE));
    }

    try {
      // validate getters
      Objects.requireNonNull(this.getPublicKeyBase58(), "publicKeyBase58 is null");
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid Ed25519VerificationKey2020: %s", SerializeUtil.toJson(json)), e);
    }
  }

  public MultibaseString getPublicKeyBase58() {
    return MultibaseFactory.create((String) this.get(PUBLIC_KEY_BASE_58));
  }
}
