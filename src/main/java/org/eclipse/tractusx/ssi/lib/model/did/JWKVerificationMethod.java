package org.eclipse.tractusx.ssi.lib.model.did;

import com.nimbusds.jose.jwk.JWK;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class JWKVerificationMethod extends VerificationMethod {

  public static final String DEFAULT_TYPE = "JsonWebKey2020";

  public static final String PUBLIC_KEY_JWK = "publicKeyJwk";

  private final JWK jwk;

  public JWKVerificationMethod(final Map<String, Object> json) {
    super(json);

    if (!DEFAULT_TYPE.equals(this.getType())) {
      throw new IllegalArgumentException(
          String.format("Invalid type %s. Expected %s", this.getType(), DEFAULT_TYPE));
    }

    Object object = this.get(PUBLIC_KEY_JWK);
    try {
      jwk = JWK.parse(convertToMap(object));
    } catch (ParseException e) {
      throw new IllegalStateException(e);
    }
  }

  public JWK getJwk() {
    return jwk;
  }

  private Map<String, Object> convertToMap(Object o) {
    if (o instanceof Map) {
      Map<String, Object> result = new HashMap<>();
      Map<?, ?> rawMap = (Map<?, ?>) o;
      rawMap.forEach((k, v) -> result.put((String) k, v));
      return result;
    }
    throw new IllegalArgumentException("object is not a map");
  }

  public static boolean isInstance(Map<String, Object> json) {
    return DEFAULT_TYPE.equals(json.get(TYPE));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JWKVerificationMethod that = (JWKVerificationMethod) o;
    return Objects.equals(jwk, that.jwk)
        && this.getId().equals(that.getId())
        && this.getType().equals(that.getType())
        && this.getController().equals(that.getController());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), jwk);
  }
}
