package org.eclipse.tractusx.ssi.lib.model.proof;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSProofConfiguration;
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

  @Test
  void testProofConfiguration() {

    Date now = new Date();
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(JWSSignature2020.TIME_FORMAT).withZone(ZoneOffset.UTC);
    Map<String, Object> standardEntries =
        Map.of(
            Proof.TYPE,
            JWSSignature2020.JWS_VERIFICATION_KEY_2020,
            JWSSignature2020.JWS,
            "Something",
            JWSSignature2020.VERIFICATION_METHOD,
            URI.create("http://example.com#key-1"),
            JWSSignature2020.CREATED,
            formatter.format(now.toInstant()),
            JWSSignature2020.PROOF_PURPOSE,
            "Proof Purpose");

    HashMap<String, Object> entries = new HashMap<>(standardEntries);
    Proof proof = new JWSProofConfiguration(entries);
    Assertions.assertTrue(proof.isConfiguration());
  }
}
