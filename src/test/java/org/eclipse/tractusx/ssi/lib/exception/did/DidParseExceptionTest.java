package org.eclipse.tractusx.ssi.lib.exception.did;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DidParseExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new DidParseException(""));
    assertDoesNotThrow(() -> new DidParseException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new DidParseException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new DidParseException("", new IllegalArgumentException(), false, false));
  }
}
