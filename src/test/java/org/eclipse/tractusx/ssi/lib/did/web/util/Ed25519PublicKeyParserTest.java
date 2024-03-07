package org.eclipse.tractusx.ssi.lib.did.web.util;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.junit.jupiter.api.Test;

class Ed25519PublicKeyParserTest {

  @Test
  void shouldParseString() {
    String keyString =
        "-----BEGIN PUBLIC KEY-----\n"
            + "MCowBQYDK2VwAyEABqAmUe/amV/nAVUt01XyrLpmQLOyLqF6LnAkH4QdyqI=\n"
            + "-----END PUBLIC KEY-----";

    MultibaseString multibaseString =
        assertDoesNotThrow(() -> Ed25519PublicKeyParser.parsePublicKey(keyString));
    assertNotNull(multibaseString);
  }
}
