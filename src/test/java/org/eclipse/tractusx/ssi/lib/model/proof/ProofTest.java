package org.eclipse.tractusx.ssi.lib.model.proof;

import java.util.Map;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ProofTest {

  @Test
  void testProofWithoutTypeShouldFail() {

    Map<String, Object> map =
        Map.of(
            JWSSignature2020.PROOF_PURPOSE,
            "test",
            JWSSignature2020.VERIFICATION_METHOD,
            "methos",
            JWSSignature2020.CREATED,
            "01-01-2024");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          new Proof(map);
        });
  }
}
