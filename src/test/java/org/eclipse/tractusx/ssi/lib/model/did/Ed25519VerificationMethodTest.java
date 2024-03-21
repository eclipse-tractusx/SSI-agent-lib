package org.eclipse.tractusx.ssi.lib.model.did;

import static org.junit.jupiter.api.Assertions.*;

import com.nimbusds.jose.jwk.OctetKeyPair;
import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.UUID;
import lombok.SneakyThrows;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPublicKeyFormatException;
import org.junit.jupiter.api.Test;

class Ed25519VerificationMethodTest {

  @Test
  void shouldThrowWhenIllegalType() {
    Map<String, Object> type =
        Map.of(
            "type",
            "Ed25519VerificationKey2069",
            "id",
            UUID.randomUUID().toString(),
            "controller",
            UUID.randomUUID().toString());
    assertThrows(IllegalArgumentException.class, () -> new Ed25519VerificationMethod(type));
  }

  @Test
  void shouldThrowWhenPublicKeyBase58IsNull() {
    Map<String, Object> type =
        Map.of(
            "type",
            Ed25519VerificationMethod.DEFAULT_TYPE,
            "id",
            UUID.randomUUID().toString(),
            "controller",
            UUID.randomUUID().toString());
    assertThrows(IllegalArgumentException.class, () -> new Ed25519VerificationMethod(type));
  }

  @Test
  @SneakyThrows
  void shouldRetrieveOctetKeyPair() {
    Map<String, Object> map =
        Map.of(
            "type",
            Ed25519VerificationMethod.DEFAULT_TYPE,
            Ed25519VerificationMethod.PUBLIC_KEY_BASE_58,
            "zdbDmZLTWuEYYZNHFLKLoRkEX4sZykkSLNQLXvMUyMB1",
            "id",
            UUID.randomUUID().toString(),
            "controller",
            UUID.randomUUID().toString());
    Ed25519VerificationMethod type = new Ed25519VerificationMethod(map);

    OctetKeyPair octetKeyPair = assertDoesNotThrow(type::getOctetKeyPair);
    assertNotNull(octetKeyPair);
  }

  private static byte[] readPublicKey(String publicKey) throws InvalidPublicKeyFormatException {

    PemReader pemReader = new PemReader(new StringReader(publicKey));
    try {
      return pemReader.readPemObject().getContent();
    } catch (IOException e) {
      throw new InvalidPublicKeyFormatException(e.getMessage());
    }
  }
}
