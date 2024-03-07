package org.eclipse.tractusx.ssi.lib.exception.key;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class KeyGenerationExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new KeyGenerationException(""));
    assertDoesNotThrow(() -> new KeyGenerationException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new KeyGenerationException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new KeyGenerationException("", new IllegalArgumentException(), false, false));
  }
}
