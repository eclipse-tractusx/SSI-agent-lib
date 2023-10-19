package org.eclipse.tractusx.ssi.lib.crypt.ec;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import java.io.IOException;
import java.io.StringWriter;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class ECPublicKeyWrapper implements IPublicKey {
  private final ECPublicKey publicKey;

  /**
   * @param encoded DER encoded bytes
   */
  public ECPublicKeyWrapper(byte[] encoded) {
    try {
      KeyFactory kf = KeyFactory.getInstance("EC");
      publicKey = (ECPublicKey) kf.generatePublic(new X509EncodedKeySpec(encoded));
    } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public int getKeyLength() {
    return SubjectPublicKeyInfo.getInstance(publicKey.getEncoded())
        .getPublicKeyData()
        .getOctets()
        .length;
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
    return new ECKey.Builder(Curve.forECParameterSpec(publicKey.getParams()), publicKey)
        .build()
        .toJSONString();
  }

  @Override
  public ECKey toJwk() {
    return new ECKey.Builder(Curve.forECParameterSpec(publicKey.getParams()), publicKey).build();
  }

  @Override
  public byte[] asByte() {
    return publicKey.getEncoded();
  }

  public ECPublicKey getPublicKey() {
    return publicKey;
  }
}
