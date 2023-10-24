package org.eclipse.tractusx.ssi.lib.crypt.rsa;

import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class RSAPrivateKeyWrapper implements IPrivateKey {

  private final RSAPrivateKey privateKey;

  /**
   * @param encoded DER encoded bytes
   */
  public RSAPrivateKeyWrapper(final byte[] encoded) {
    try {
      KeyFactory kf = KeyFactory.getInstance("RSA");
      privateKey = (RSAPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public int getKeyLength() {
    return privateKey.getModulus().bitLength();
  }

  @Override
  public String asStringForStoring() {
    try {
      StringWriter stringWriter = new StringWriter();
      JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter);
      pemWriter.writeObject(privateKey);
      pemWriter.close();
      return stringWriter.toString();
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public String asStringForExchange(final EncodeType encodeType) {
    return null;
  }

  @Override
  public byte[] asByte() {
    return privateKey.getEncoded();
  }

  public RSAPrivateKey getPrivateKey() {
    return privateKey;
  }
}
