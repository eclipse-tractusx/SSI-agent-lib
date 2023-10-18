package org.eclipse.tractusx.ssi.lib.crypt.rsa;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class RSAKeyGenerator {

  private final int keySize;

  public RSAKeyGenerator(final int keySize) {
    this.keySize = keySize;
  }

  public KeyPair generateKey() {
    KeyPairGenerator kpg = null;
    try {
      kpg = KeyPairGenerator.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException(e);
    }
    kpg.initialize(keySize);
    java.security.KeyPair keyPair = kpg.generateKeyPair();

    return new KeyPair(
        new RSAPubKey(keyPair.getPublic().getEncoded()),
        new RSAPrivKey(keyPair.getPrivate().getEncoded()));
  }
}
