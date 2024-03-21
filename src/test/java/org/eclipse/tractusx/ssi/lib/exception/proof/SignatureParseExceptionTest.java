package org.eclipse.tractusx.ssi.lib.exception.proof;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SignatureParseExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new SignatureParseException(""));
    assertDoesNotThrow(() -> new SignatureParseException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new SignatureParseException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new SignatureParseException("", new IllegalArgumentException(), false, false));
  }
}
