package org.eclipse.tractusx.ssi.lib.crypt.rsa;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;
import org.junit.jupiter.api.Test;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
class RSAKeyGeneratorTest {
  @Test
  void shouldGenerateKeyForGivenCurve() {
    RSAKeyGenerator generator = new RSAKeyGenerator(2048);
    KeyPair keyPair = assertDoesNotThrow(generator::generateKey);
    assertTrue(keyPair.getPublicKey() instanceof RSAPublicKeyWrapper);
    assertTrue(keyPair.getPrivateKey() instanceof RSAPrivateKeyWrapper);
  }

  @Test
  void shouldThrow() {
    RSAKeyGenerator generator = new RSAKeyGenerator(-5);
    assertThrows(KeyGenerationException.class, generator::generateKey);
  }
}
