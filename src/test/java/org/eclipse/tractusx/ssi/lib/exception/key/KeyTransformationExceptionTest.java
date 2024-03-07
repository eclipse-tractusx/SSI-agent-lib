package org.eclipse.tractusx.ssi.lib.exception.key;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class KeyTransformationExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new KeyTransformationException(""));
    assertDoesNotThrow(() -> new KeyTransformationException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new KeyTransformationException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new KeyTransformationException("", new IllegalArgumentException(), false, false));
  }
}
