package org.eclipse.tractusx.ssi.lib.crypt.ec;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
class ECKeyGeneratorTest {

  @ParameterizedTest
  @ValueSource(strings = {"secp256r1", "secp384r1", "secp256k1"})
  void shouldGenerateKeyForGivenCurve(String curve) {
    ECKeyGenerator generator = new ECKeyGenerator(curve);
    KeyPair keyPair = assertDoesNotThrow(generator::generateKey);
    assertTrue(keyPair.getPublicKey() instanceof ECPublicKeyWrapper);
    assertTrue(keyPair.getPrivateKey() instanceof ECPrivateKeyWrapper);
  }

  @Test
  void shouldThrow() {
    ECKeyGenerator generator = new ECKeyGenerator("this-is-not-a-ec-curve");
    assertThrows(KeyGenerationException.class, generator::generateKey);
  }
}
