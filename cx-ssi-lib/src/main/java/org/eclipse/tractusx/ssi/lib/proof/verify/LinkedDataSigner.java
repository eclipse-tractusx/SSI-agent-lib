package org.eclipse.tractusx.ssi.lib.proof.verify;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

public class LinkedDataSigner {

  @SneakyThrows
  public byte[] sign(HashedLinkedData hashedLinkedData, byte[] signingKey) {

    final byte[] message = hashedLinkedData.getValue();

    final KeyFactory kf = KeyFactory.getInstance("Ed25519");
    final PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(signingKey));

    final Signature sig = Signature.getInstance("Ed25519");
    sig.initSign(privateKey);
    sig.update(message);

    return sig.sign();
  }
}
