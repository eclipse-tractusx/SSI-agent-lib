package org.eclipse.tractusx.ssi.lib.crypt.rsa;

import com.nimbusds.jose.jwk.RSAKey;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class RSAPublicKeyWrapper implements IPublicKey {

  private final RSAPublicKey publicKey;

  /**
   * @param encoded DER encoded bytes
   */
  public RSAPublicKeyWrapper(byte[] encoded) {
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      publicKey = (RSAPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public int getKeyLength() {
    return publicKey.getModulus().bitLength();
  }

  @Override
  public String asStringForStoring() {
    try {
      StringWriter stringWriter = new StringWriter();
      JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
      pemWriter.writeObject(publicKey);
      pemWriter.close();
      return stringWriter.toString();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public String asStringForExchange(final EncodeType encodeType) {
    return new RSAKey.Builder(publicKey).build().toJSONString();
  }

  @Override
  public byte[] asByte() {
    return publicKey.getEncoded();
  }

  @Override
  public RSAKey toJwk() {
    return new RSAKey.Builder(publicKey).build();
  }

  public RSAPublicKey getPublicKey() {
    return publicKey;
  }
}
