package org.eclipse.tractusx.ssi.lib.exception.proof;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SignatureVerificationExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new SignatureVerificationException(""));
    assertDoesNotThrow(
        () -> new SignatureVerificationException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new SignatureVerificationException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new SignatureVerificationException("", new IllegalArgumentException(), false, false));
  }
}
