package org.eclipse.tractusx.ssi.lib.crypt.x21559;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.eclipse.tractusx.ssi.lib.crypt.IKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePublicKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;

public class x21559Generator implements IKeyGenerator {

  @Override
  public KeyPair generateKey() throws KeyGenerationException {

    SecureRandom secureRandom = new SecureRandom();

    Ed25519KeyPairGenerator keyPairGenerator = new Ed25519KeyPairGenerator();
    keyPairGenerator.init(new Ed25519KeyGenerationParameters(secureRandom));
    AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();

    Ed25519PrivateKeyParameters privateKey = (Ed25519PrivateKeyParameters) keyPair.getPrivate();
    Ed25519PublicKeyParameters publicKey = (Ed25519PublicKeyParameters) keyPair.getPublic();

    x21559PrivateKey x21559PrivateKey;
    try {
      x21559PrivateKey = new x21559PrivateKey(privateKey.getEncoded());
    } catch (InvalidePrivateKeyFormat e) {
      throw new KeyGenerationException(e.getCause());
    }
    x21559PublicKey x21559PublicKey;
    try {
      x21559PublicKey = new x21559PublicKey(publicKey.getEncoded());
    } catch (InvalidePublicKeyFormat e) {
      throw new KeyGenerationException(e.getCause());
    }

    KeyPair pair = new KeyPair(x21559PublicKey, x21559PrivateKey);

    return pair;
  }
}
