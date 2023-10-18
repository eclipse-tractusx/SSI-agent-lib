package org.eclipse.tractusx.ssi.lib.crypt.ec;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.tractusx.ssi.lib.crypt.IKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class ECKeyGenerator implements IKeyGenerator {

  private final String curve;

  public ECKeyGenerator(final String curve) {
    this.curve = curve;
  }

  @Override
  public KeyPair generateKey() throws KeyGenerationException {

    KeyPairGenerator kpg = null;
    try {
      kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
      ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec(curve);
      kpg.initialize(ecGenParameterSpec, new SecureRandom());
    } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
      throw new KeyGenerationException(e);
    }

    java.security.KeyPair keyPair = kpg.generateKeyPair();

    return new KeyPair(
        new ECPubKey(keyPair.getPublic().getEncoded(), curve),
        new ECPrivKey(keyPair.getPrivate().getEncoded()));
  }
}
