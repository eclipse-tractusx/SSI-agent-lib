package org.eclipse.tractusx.ssi.lib.did.web.util;

import java.io.StringReader;
import lombok.SneakyThrows;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;

public class Ed25519PublicKeyParser {

  /**
   * Parses public key in format -----BEGIN PUBLIC KEY-----
   * MCowBQYDK2VwAyEABqAmUe/amV/nAVUt01XyrLpmQLOyLqF6LnAkH4QdyqI= -----END PUBLIC KEY-----
   *
   * @return public key as multibase string
   */
  public static MultibaseString parsePublicKey(String publicKey) {
    final byte[] publicKey64 = readPublicKey(publicKey);
    return MultibaseFactory.create(publicKey64);
  }

  @SneakyThrows
  private static byte[] readPublicKey(String publicKey) {

    PemReader pemReader = new PemReader(new StringReader(publicKey));
    return pemReader.readPemObject().getContent();
  }
}
