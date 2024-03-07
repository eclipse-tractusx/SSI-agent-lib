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
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Ed25519VerificationMethod(
                Map.of(
                    "type",
                    "Ed25519VerificationKey2069",
                    "id",
                    UUID.randomUUID().toString(),
                    "controller",
                    UUID.randomUUID().toString())));
  }

  @Test
  void shouldThrowWhenPublicKeyBase58IsNull() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new Ed25519VerificationMethod(
                Map.of(
                    "type",
                    Ed25519VerificationMethod.DEFAULT_TYPE,
                    "id",
                    UUID.randomUUID().toString(),
                    "controller",
                    UUID.randomUUID().toString())));
  }

  @Test
  @SneakyThrows
  void shouldRetrieveOctetKeyPair() {
    Ed25519VerificationMethod type =
        new Ed25519VerificationMethod(
            Map.of(
                "type",
                Ed25519VerificationMethod.DEFAULT_TYPE,
                Ed25519VerificationMethod.PUBLIC_KEY_BASE_58,
                "zdbDmZLTWuEYYZNHFLKLoRkEX4sZykkSLNQLXvMUyMB1",
                "id",
                UUID.randomUUID().toString(),
                "controller",
                UUID.randomUUID().toString()));

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
