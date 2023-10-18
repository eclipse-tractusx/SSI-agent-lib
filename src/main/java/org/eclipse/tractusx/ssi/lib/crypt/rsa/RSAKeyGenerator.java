package org.eclipse.tractusx.ssi.lib.crypt.rsa;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.lib.crypt.IKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class RSAKeyGenerator implements IKeyGenerator {

  private final int keySize;

  public RSAKeyGenerator(final int keySize) {
    this.keySize = keySize;
  }

  public KeyPair generateKey() throws KeyGenerationException {
    KeyPairGenerator kpg = null;
    try {
      kpg = KeyPairGenerator.getInstance("RSA");
    } catch (NoSuchAlgorithmException e) {
      throw new KeyGenerationException(e);
    }
    kpg.initialize(keySize);
    java.security.KeyPair keyPair = kpg.generateKeyPair();

    return new KeyPair(
        new RSAPubKey(keyPair.getPublic().getEncoded()),
        new RSAPrivKey(keyPair.getPrivate().getEncoded()));
  }
}
