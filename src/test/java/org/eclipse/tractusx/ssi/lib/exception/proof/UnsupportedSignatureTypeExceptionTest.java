package org.eclipse.tractusx.ssi.lib.exception.proof;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UnsupportedSignatureTypeExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new UnsupportedSignatureTypeException(""));
    assertDoesNotThrow(
        () -> new UnsupportedSignatureTypeException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new UnsupportedSignatureTypeException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () ->
            new UnsupportedSignatureTypeException(
                "", new IllegalArgumentException(), false, false));
  }
}
