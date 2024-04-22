package org.eclipse.tractusx.ssi.lib.model.proof;

import java.net.URI;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSProofBuilder;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JWSProofBuilderTest {

  @BeforeEach
  public void setup() {}

  @Test
  void testProofPurpose() {

    Date now = new Date();
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(JWSSignature2020.TIME_FORMAT).withZone(ZoneOffset.UTC);
    URI uri = URI.create("http://example.com#key-1");
    String proofValue = "proofValue";
    JWSSignature2020 build =
        new JWSProofBuilder()
            .proofValue(proofValue)
            .proofPurpose(JWSSignature2020.ASSERTION_METHOD)
            .verificationMethod(uri)
            .created(now.toInstant())
            .build();

    Assertions.assertEquals(JWSSignature2020.ASSERTION_METHOD, build.getProofPurpose());
    Assertions.assertEquals(uri, build.getVerificationMethod());
    Assertions.assertEquals(
        formatter.format(now.toInstant()), formatter.format(build.getCreated()));
    Assertions.assertEquals(proofValue, build.getJws());
  }
}
