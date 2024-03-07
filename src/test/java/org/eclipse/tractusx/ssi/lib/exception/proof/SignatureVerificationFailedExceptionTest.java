package org.eclipse.tractusx.ssi.lib.exception.proof;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SignatureVerificationFailedExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new SignatureVerificationFailedException(""));
    assertDoesNotThrow(
        () -> new SignatureVerificationFailedException("", new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new SignatureVerificationFailedException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () ->
            new SignatureVerificationFailedException(
                "", new IllegalArgumentException(), false, false));
  }
}
