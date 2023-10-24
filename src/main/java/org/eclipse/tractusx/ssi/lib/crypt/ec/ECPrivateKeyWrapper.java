package org.eclipse.tractusx.ssi.lib.crypt.ec;

import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class ECPrivateKeyWrapper implements IPrivateKey {

  private final ECPrivateKey privateKey;

  /**
   * @param encoded DER encoded bytes
   */
  public ECPrivateKeyWrapper(final byte[] encoded) {
    try {
      KeyFactory kf = KeyFactory.getInstance("EC");
      privateKey = (ECPrivateKey) kf.generatePrivate(new PKCS8EncodedKeySpec(encoded));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public int getKeyLength() {
    return privateKey.getS().toByteArray().length;
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

  public ECPrivateKey getPrivateKey() {
    return privateKey;
  }
}
