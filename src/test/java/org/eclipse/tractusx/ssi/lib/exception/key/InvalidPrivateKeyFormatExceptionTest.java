package org.eclipse.tractusx.ssi.lib.exception.key;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class InvalidPrivateKeyFormatExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new InvalidPrivateKeyFormatException(""));
    assertDoesNotThrow(
        () -> new InvalidPrivateKeyFormatException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new InvalidPrivateKeyFormatException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () ->
            new InvalidPrivateKeyFormatException("", new IllegalArgumentException(), false, false));
    assertDoesNotThrow(() -> new InvalidPrivateKeyFormatException(42, 42));
  }
}
