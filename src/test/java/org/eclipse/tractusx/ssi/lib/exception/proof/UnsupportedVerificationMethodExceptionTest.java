package org.eclipse.tractusx.ssi.lib.exception.proof;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.eclipse.tractusx.ssi.lib.model.did.VerificationMethod;
import org.junit.jupiter.api.Test;

class UnsupportedVerificationMethodExceptionTest {
  @Test
  void shouldCreate() {
    VerificationMethod vm =
        new VerificationMethod(
            Map.of(
                "id", "id",
                "type", "fake-type",
                "controller", "my-controller"));
    assertDoesNotThrow(() -> new UnsupportedVerificationMethodException(vm, ""));
  }
}
