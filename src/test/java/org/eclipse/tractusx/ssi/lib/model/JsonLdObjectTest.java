package org.eclipse.tractusx.ssi.lib.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.junit.jupiter.api.Test;

class JsonLdObjectTest {

  @Test
  void withListContext() {
    Map<String, Object> map =
        Map.of(
            "@context",
            List.of(42),
            "id",
            "did:web:localhost",
            "type",
            "type",
            "issuer",
            "did:web:localhost",
            "issuanceDate",
            Instant.now().toString(),
            "credentialSubject",
            Map.of("hallo", "möp"));
    assertThrows(IllegalArgumentException.class, () -> new VerifiableCredential(map));
  }

  @Test
  void withContext() {
    Map<String, Object> map =
        Map.of(
            "@context",
            42,
            "id",
            "did:web:localhost",
            "type",
            "type",
            "issuer",
            "did:web:localhost",
            "issuanceDate",
            Instant.now().toString(),
            "credentialSubject",
            Map.of("hallo", "möp"));
    assertThrows(IllegalArgumentException.class, () -> new VerifiableCredential(map));
  }
}
