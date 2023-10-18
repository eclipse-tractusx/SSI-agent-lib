package org.eclipse.tractusx.ssi.lib.crypt.rsa;

import static org.junit.jupiter.api.Assertions.*;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import org.junit.jupiter.api.Test;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
class RSAKeyTest {

  @Test
  void shouldReturnPEMString() throws NoSuchAlgorithmException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(2048);
    java.security.KeyPair keyPair = kpg.generateKeyPair();

    RSAPrivKey privKey = new RSAPrivKey(keyPair.getPrivate().getEncoded());
    String string = assertDoesNotThrow(privKey::asStringForStoring);
    assertTrue(string.startsWith("-----BEGIN RSA PRIVATE KEY-----"));
    assertEquals(2048, privKey.getKeyLength());

    RSAPubKey pubKey = new RSAPubKey(keyPair.getPublic().getEncoded());
    string = assertDoesNotThrow(pubKey::asStringForStoring);
    assertTrue(string.startsWith("-----BEGIN PUBLIC KEY-----"));
    assertEquals(2048, pubKey.getKeyLength());
  }
}
