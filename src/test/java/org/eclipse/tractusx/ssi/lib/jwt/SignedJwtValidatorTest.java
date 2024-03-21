package org.eclipse.tractusx.ssi.lib.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.text.ParseException;
import java.util.Map;
import java.util.UUID;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.exception.proof.JwtAudienceCheckException;
import org.eclipse.tractusx.ssi.lib.exception.proof.JwtExpiredException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureParseException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.JwtConfig;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.testcontainers.shaded.com.fasterxml.jackson.core.JsonProcessingException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

class SignedJwtValidatorTest {

  @Test
  void shouldValidate() throws JsonProcessingException {
    JwtConfig conf = JwtConfig.builder().expirationTime(60).build();
    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    String audience = "my-audience";
    SignedJWT signedJWT = create(testIdentity, audience, conf);

    SignedJwtValidator validator = new SignedJwtValidator();
    assertDoesNotThrow(() -> validator.validateDate(signedJWT));
    assertDoesNotThrow(() -> validator.validateAudiences(signedJWT, audience));
  }

  @Test
  void shouldThrowWhenAudienceDoesNotMatch() {
    JwtConfig conf = JwtConfig.builder().expirationTime(60).build();
    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    String audience = "my-audience";
    SignedJWT signedJWT = create(testIdentity, "fake-audience", conf);

    SignedJwtValidator validator = new SignedJwtValidator();
    assertThrows(
        JwtAudienceCheckException.class, () -> validator.validateAudiences(signedJWT, audience));
  }

  @Test
  @SneakyThrows
  void shouldThrowSignatureParseExceptionWhenJwtClaimsNotParseable() {
    JwtConfig conf = JwtConfig.builder().expirationTime(60).build();
    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    String audience = "my-audience";
    SignedJWT signedJWT = create(testIdentity, audience, conf);

    SignedJWT spy = Mockito.spy(signedJWT);
    when(spy.getJWTClaimsSet()).thenThrow(new ParseException("yada", 55));

    SignedJwtValidator validator = new SignedJwtValidator();
    assertThrows(SignatureParseException.class, () -> validator.validateAudiences(spy, audience));

    assertThrows(SignatureParseException.class, () -> validator.validateDate(spy));
  }

  @Test
  void shouldThrowWhenExpired() {
    JwtConfig conf = JwtConfig.builder().expirationTime(-5).build();
    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    SignedJWT signedJWT = create(testIdentity, "my-audience", conf);

    SignedJwtValidator validator = new SignedJwtValidator();
    assertThrows(JwtExpiredException.class, () -> validator.validateDate(signedJWT));
  }

  @SneakyThrows
  private SignedJWT create(TestIdentity testIdentity, String audience, JwtConfig conf) {

    SignedJwtFactory fac = new SignedJwtFactory();
    return fac.create(
        URI.create("id"),
        new Did(new DidMethod("web"), new DidMethodIdentifier("issuer"), null),
        audience,
        new SerializedVerifiablePresentation(
            new ObjectMapper().writeValueAsString(Map.of("hallo", "du"))),
        testIdentity.getPrivateKey(),
        testIdentity.getDid().toUri().toString() + "#" + UUID.randomUUID(),
        conf);
  }
}
