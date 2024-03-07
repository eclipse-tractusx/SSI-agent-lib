package org.eclipse.tractusx.ssi.lib.exception.proof;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class NoVerificationKeyFoundExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new NoVerificationKeyFoundException(""));
    assertDoesNotThrow(
        () -> new NoVerificationKeyFoundException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new NoVerificationKeyFoundException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () ->
            new NoVerificationKeyFoundException("", new IllegalArgumentException(), false, false));
  }
}
