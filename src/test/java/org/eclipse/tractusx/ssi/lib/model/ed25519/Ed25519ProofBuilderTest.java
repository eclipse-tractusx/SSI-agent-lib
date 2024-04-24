package org.eclipse.tractusx.ssi.lib.model.ed25519;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.did.web.util.Ed25519PublicKeyParser;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.proof.ed25519.Ed25519ProofBuilder;
import org.eclipse.tractusx.ssi.lib.model.proof.ed25519.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class Ed25519ProofBuilderTest {

  @Test
  @SneakyThrows
  void testEd25519ProofBuilder() {

    Date now = new Date();
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(JWSSignature2020.TIME_FORMAT).withZone(ZoneOffset.UTC);
    URI verificationMethod = URI.create("http://example.com#key-1");

    String keyString =
        "-----BEGIN PUBLIC KEY-----\n"
            + "MCowBQYDK2VwAyEABqAmUe/amV/nAVUt01XyrLpmQLOyLqF6LnAkH4QdyqI=\n"
            + "-----END PUBLIC KEY-----";

    MultibaseString multibaseString =
        assertDoesNotThrow(() -> Ed25519PublicKeyParser.parsePublicKey(keyString));

    Ed25519Signature2020 ed25519Signature2020 =
        new Ed25519ProofBuilder()
            .proofPurpose(JWSSignature2020.ASSERTION_METHOD)
            .verificationMethod(verificationMethod)
            .proofValue(multibaseString.toString())
            .created(now.toInstant())
            .build();

    Assertions.assertEquals(
        JWSSignature2020.ASSERTION_METHOD, ed25519Signature2020.getProofPurpose());
    Assertions.assertEquals(verificationMethod, ed25519Signature2020.getVerificationMethod());
    Assertions.assertEquals(multibaseString, ed25519Signature2020.getProofValue());
    Assertions.assertEquals(
        formatter.format(now.toInstant()), formatter.format(ed25519Signature2020.getCreated()));
  }
}
