package org.eclipse.tractusx.ssi.lib.crypt.rsa;

import java.security.InvalidParameterException;
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
      kpg.initialize(keySize);
    } catch (NoSuchAlgorithmException | InvalidParameterException e) {
      throw new KeyGenerationException(e);
    }

    java.security.KeyPair keyPair = kpg.generateKeyPair();

    return new KeyPair(
        new RSAPublicKeyWrapper(keyPair.getPublic().getEncoded()),
        new RSAPrivateKeyWrapper(keyPair.getPrivate().getEncoded()));
  }
}
