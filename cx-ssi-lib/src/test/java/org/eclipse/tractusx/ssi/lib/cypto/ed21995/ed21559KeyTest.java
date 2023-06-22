package org.eclipse.tractusx.ssi.lib.cypto.ed21995;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import org.eclipse.tractusx.ssi.lib.crypt.IKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559Generator;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PublicKey;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePublicKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;
import org.junit.jupiter.api.Test;

public class ed21559KeyTest {

  @Test
  public void testED21559KeyGeneration() throws KeyGenerationException {
    IKeyGenerator keyGenerator = new x21559Generator();
    KeyPair keyPair = keyGenerator.generateKey();
    assertNotNull(keyPair.getPrivateKey());
    assertNotNull(keyPair.getPublicKey());
  }

  @Test
  public void testED21559KeySerliztion() throws KeyGenerationException, IOException {
    IKeyGenerator keyGenerator = new x21559Generator();
    KeyPair keyPair = keyGenerator.generateKey();

    printTestData(keyPair.getPrivateKey().asStringForStoring());
    assertNotNull(keyPair.getPrivateKey().asStringForStoring());

    printTestData(keyPair.getPrivateKey().asStringForExchange(EncodeType.Base64));
    assertNotNull(keyPair.getPrivateKey().asStringForExchange(EncodeType.Base64));

    printTestData(keyPair.getPublicKey().asStringForStoring());
    assertNotNull(keyPair.getPublicKey().asStringForStoring());

    printTestData(keyPair.getPublicKey().asStringForExchange(EncodeType.Base64));
    assertNotNull(keyPair.getPublicKey().asStringForExchange(EncodeType.Base64));
  }

  @Test
  public void testED21559KeyDeserliztion()
      throws KeyGenerationException, IOException, InvalidePrivateKeyFormat,
          InvalidePublicKeyFormat {
    IKeyGenerator keyGenerator = new x21559Generator();
    KeyPair keyPair = keyGenerator.generateKey();

    var originalPrivateKey = keyPair.getPrivateKey().asByte();
    var originalPublicKey = keyPair.getPublicKey().asByte();

    String privateKeyString = keyPair.getPrivateKey().asStringForStoring();
    var privateKey = new x21559PrivateKey(privateKeyString, true);

    String publicKeyString = keyPair.getPublicKey().asStringForStoring();
    var publicKey = new x21559PublicKey(publicKeyString, true);

    printTestData(keyPair.getPrivateKey().asStringForExchange(EncodeType.Base64));
    printTestData(privateKey.asStringForExchange(EncodeType.Base64));

    assertTrue(Arrays.equals(originalPrivateKey, privateKey.asByte()));

    printTestData(keyPair.getPublicKey().asStringForExchange(EncodeType.Base58));
    printTestData(publicKey.asStringForExchange(EncodeType.Base58));
    assertTrue(Arrays.equals(originalPublicKey, publicKey.asByte()));
  }

  private static void printTestData(String str) {
    System.out.println(str);
  }
}
