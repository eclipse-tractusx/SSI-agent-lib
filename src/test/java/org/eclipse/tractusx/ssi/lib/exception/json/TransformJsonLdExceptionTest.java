package org.eclipse.tractusx.ssi.lib.exception.json;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class TransformJsonLdExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new TransformJsonLdException(""));
    assertDoesNotThrow(() -> new TransformJsonLdException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new TransformJsonLdException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new TransformJsonLdException("", new IllegalArgumentException(), false, false));
  }
}
