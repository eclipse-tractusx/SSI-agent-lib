package org.eclipse.tractusx.ssi.lib.exception.proof;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SignatureValidationExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new SignatureValidationException(""));
    assertDoesNotThrow(() -> new SignatureValidationException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new SignatureValidationException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new SignatureValidationException("", new IllegalArgumentException(), false, false));
  }
}
