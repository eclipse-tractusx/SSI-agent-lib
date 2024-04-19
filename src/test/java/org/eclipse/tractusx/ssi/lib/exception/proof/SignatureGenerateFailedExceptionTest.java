package org.eclipse.tractusx.ssi.lib.exception.proof;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SignatureGenerateFailedExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new SignatureGenerateFailedException(""));
    assertDoesNotThrow(
        () -> new SignatureGenerateFailedException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new SignatureGenerateFailedException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () ->
            new SignatureGenerateFailedException("", new IllegalArgumentException(), false, false));
  }
}
