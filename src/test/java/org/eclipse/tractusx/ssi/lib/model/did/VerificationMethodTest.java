package org.eclipse.tractusx.ssi.lib.model.did;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import java.util.Map;
import org.junit.jupiter.api.Test;

class VerificationMethodTest {
  @Test
  void shouldThrowWhenRequiredAttributeNull() {
    IllegalArgumentException illegalArgumentException =
        assertThrows(
            IllegalArgumentException.class,
            () -> new VerificationMethod(Map.of("@context", URI.create("did:localhost"))));
  }
}
