package org.eclipse.tractusx.ssi.lib.exception.did;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class DidResolverExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new DidResolverException(""));
    assertDoesNotThrow(() -> new DidResolverException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new DidResolverException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new DidResolverException("", new IllegalArgumentException(), false, false));
  }
}
