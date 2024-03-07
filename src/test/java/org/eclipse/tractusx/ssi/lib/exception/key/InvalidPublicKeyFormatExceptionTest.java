package org.eclipse.tractusx.ssi.lib.exception.key;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidPublicKeyFormatExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new InvalidPublicKeyFormatException(""));
    assertDoesNotThrow(
        () -> new InvalidPublicKeyFormatException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new InvalidPublicKeyFormatException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () ->
            new InvalidPublicKeyFormatException("", new IllegalArgumentException(), false, false));
    assertDoesNotThrow(() -> new InvalidPublicKeyFormatException(42, 42));
  }
}
