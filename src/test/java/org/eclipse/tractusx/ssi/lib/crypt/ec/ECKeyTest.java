package org.eclipse.tractusx.ssi.lib.crypt.ec;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import org.junit.jupiter.api.Test;
import org.testcontainers.shaded.org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class ECKeyTest {
  @Test
  void shouldReturnPEMString() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC", new BouncyCastleProvider());
    ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("secp256r1");
    kpg.initialize(ecGenParameterSpec, new SecureRandom());
    java.security.KeyPair keyPair = kpg.generateKeyPair();

    ECPrivKey ECpKey = new ECPrivKey(keyPair.getPrivate().getEncoded());
    String string = assertDoesNotThrow(ECpKey::asStringForStoring);
    assertTrue(string.startsWith("-----BEGIN EC PRIVATE KEY-----"));
    assertEquals(32, ECpKey.getKeyLength());

    ECPubKey pubKey = new ECPubKey(keyPair.getPublic().getEncoded());
    string = assertDoesNotThrow(pubKey::asStringForStoring);
    assertTrue(string.startsWith("-----BEGIN PUBLIC KEY-----"));
    assertEquals(65, pubKey.getKeyLength());
  }
}
