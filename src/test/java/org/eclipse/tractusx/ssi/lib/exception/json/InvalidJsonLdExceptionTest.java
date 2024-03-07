package org.eclipse.tractusx.ssi.lib.exception.json;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InvalidJsonLdExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new InvalidJsonLdException(""));
    assertDoesNotThrow(() -> new InvalidJsonLdException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new InvalidJsonLdException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new InvalidJsonLdException("", new IllegalArgumentException(), false, false));
  }
}
