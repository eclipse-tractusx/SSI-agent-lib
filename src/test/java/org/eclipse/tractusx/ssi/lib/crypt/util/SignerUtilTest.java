package org.eclipse.tractusx.ssi.lib.crypt.util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SignerUtilTest {

  @Test
  void ecShouldThrow() {
    assertThrows(IllegalStateException.class, () -> SignerUtil.getECPrivateKey(new byte[12]));
  }

  @Test
  void rsaShouldThrow() {
    assertThrows(IllegalStateException.class, () -> SignerUtil.getRSAPrivateKey(new byte[12]));
  }
}
