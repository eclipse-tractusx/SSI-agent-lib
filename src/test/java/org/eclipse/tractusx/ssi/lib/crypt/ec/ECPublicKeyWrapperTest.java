package org.eclipse.tractusx.ssi.lib.crypt.ec;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;
import org.junit.jupiter.api.Test;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
class ECPublicKeyWrapperTest {
  @Test
  void shouldResultInValidEcWrapper()
      throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
    ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256r1");
    kpg.initialize(ecGenParameterSpec, new SecureRandom());
    KeyPair keyPair = kpg.generateKeyPair();

    ECPublicKeyWrapper wrapper =
        assertDoesNotThrow(() -> new ECPublicKeyWrapper(keyPair.getPublic().getEncoded()));

    assertTrue(wrapper.getKeyLength() > 0);
    assertFalse(wrapper.asStringForStoring().isBlank());
    assertNotNull(wrapper.asStringForExchange(EncodeType.BASE58));
    assertTrue(wrapper.asByte().length > 0);
    assertNotNull(wrapper.getPublicKey());
    assertNotNull(wrapper.toJwk());
  }

  @Test
  void shouldThrowWhenWrongEncodedKey() {
    assertThrows(IllegalStateException.class, () -> new ECPublicKeyWrapper(new byte[5]));
  }
}
