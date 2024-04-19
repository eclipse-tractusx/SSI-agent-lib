package org.eclipse.tractusx.ssi.lib.crypt.rsa;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;
import org.junit.jupiter.api.Test;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
class RSAPublicKeyWrapperTest {

  @Test
  void shouldResultInValidRSAWrapper()
      throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA", new BouncyCastleProvider());
    kpg.initialize(2048);
    KeyPair keyPair = kpg.generateKeyPair();

    RSAPublicKeyWrapper wrapper =
        assertDoesNotThrow(() -> new RSAPublicKeyWrapper(keyPair.getPublic().getEncoded()));

    assertTrue(wrapper.getKeyLength() > 0);
    assertFalse(wrapper.asStringForStoring().isBlank());
    assertNotNull(wrapper.asStringForExchange(EncodeType.BASE_58));
    assertTrue(wrapper.asByte().length > 0);
    assertNotNull(wrapper.getPublicKey());
    assertNotNull(wrapper.toJwk());
  }

  @Test
  void shouldThrowWhenWrongEncodedKey() {
    assertThrows(IllegalStateException.class, () -> new RSAPublicKeyWrapper(new byte[5]));
  }
}
