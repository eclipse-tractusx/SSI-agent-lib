package org.eclipse.tractusx.ssi.lib.exception.proof;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import org.junit.jupiter.api.Test;

class JwtAudienceCheckExceptionTest {

  @Test
  void shouldCreate() {
    assertDoesNotThrow(() -> new JwtAudienceCheckException(""));
    assertDoesNotThrow(() -> new JwtAudienceCheckException("", new IllegalArgumentException()));
    assertDoesNotThrow(() -> new JwtAudienceCheckException(new IllegalArgumentException()));
    assertDoesNotThrow(
        () -> new JwtAudienceCheckException("", new IllegalArgumentException(), false, false));
    assertDoesNotThrow(() -> new JwtAudienceCheckException("", Collections.emptyList()));
  }
}
