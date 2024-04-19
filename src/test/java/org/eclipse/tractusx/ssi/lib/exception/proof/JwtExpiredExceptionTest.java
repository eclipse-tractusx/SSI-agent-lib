package org.eclipse.tractusx.ssi.lib.exception.proof;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.util.Date;
import org.junit.jupiter.api.Test;

class JwtExpiredExceptionTest {
  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new JwtExpiredException(""));
    assertDoesNotThrow(() -> new JwtExpiredException(new Date()));
    assertDoesNotThrow(() -> new JwtExpiredException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new JwtExpiredException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new JwtExpiredException("", new IllegalArgumentException(), false, false));
  }
}
