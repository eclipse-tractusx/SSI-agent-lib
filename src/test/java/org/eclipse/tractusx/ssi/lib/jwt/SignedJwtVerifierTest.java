package org.eclipse.tractusx.ssi.lib.jwt;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.produce.JWSSignerFactory;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.ec.ECPrivateKeyWrapper;
import org.eclipse.tractusx.ssi.lib.crypt.ec.ECPublicKeyWrapper;
import org.eclipse.tractusx.ssi.lib.crypt.octet.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.crypt.rsa.RSAPrivateKeyWrapper;
import org.eclipse.tractusx.ssi.lib.crypt.rsa.RSAPublicKeyWrapper;
import org.eclipse.tractusx.ssi.lib.exception.did.DidParseException;
import org.eclipse.tractusx.ssi.lib.exception.did.DidResolverException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureParseException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureVerificationException;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.VerificationMethod;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
class SignedJwtVerifierTest {

  @ParameterizedTest
  @MethodSource("testConfigs")
  @SneakyThrows
  void verifySignature(TestConfig testConfig) {
    final TestDidResolver didResolver = new TestDidResolver();
    didResolver.register(testConfig.testIdentity);

    SignedJWT signedJWT = signJWT(testConfig.jwsObject, testConfig.jwk);

    SignedJwtVerifier v = new SignedJwtVerifier(didResolver);
    assertTrue(v.verify(signedJWT));
  }

  @Test
  @SneakyThrows
  void shouldThrowWhenVerificationMethodNotSupported() {
    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256r1", Curve.P_256);

    String keyId = extractKeyId(testIdentity);

    VerificationMethod original = testIdentity.getDidDocument().getVerificationMethods().get(0);

    VerificationMethod vm =
        new VerificationMethod(
            Map.of(
                "id", original.getId(),
                "type", "fake-type",
                "controller", "my-controller"));

    testIdentity.getDidDocument().put(DidDocument.VERIFICATION_METHOD, List.of(vm));

    final TestDidResolver didResolver = new TestDidResolver();
    didResolver.register(testIdentity);

    JWSObject jwsObject = getJWSObject(testIdentity, keyId, JWSAlgorithm.ES256);

    JWK jwk = getEcJwk(testIdentity, keyId);

    SignedJWT signedJWT = signJWT(jwsObject, jwk);

    SignedJwtVerifier v = new SignedJwtVerifier(didResolver);
    assertFalse(
        () -> {
          try {
            return v.verify(signedJWT);
          } catch (DidParseException
              | DidResolverException
              | SignatureVerificationException
              | SignatureParseException e) {
            throw new RuntimeException(e);
          }
        });
  }

  @Test
  @SneakyThrows
  void shouldThrowWhenVerificationMethodNotFound() {
    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256r1", Curve.P_256);
    final TestDidResolver didResolver = new TestDidResolver();
    didResolver.register(testIdentity);

    JWSObject jwsObject = getJWSObject(testIdentity, "fake", JWSAlgorithm.ES256);

    JWK jwk = getEcJwk(testIdentity, "fake");

    SignedJWT signedJWT = signJWT(jwsObject, jwk);

    SignedJwtVerifier v = new SignedJwtVerifier(didResolver);
    assertThrows(IllegalArgumentException.class, () -> v.verify(signedJWT));
  }

  @Test
  @SneakyThrows
  void shouldThrowSignatureParseExceptionWhenClaimsSetNotParseable() {
    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256r1", Curve.P_256);
    final TestDidResolver didResolver = new TestDidResolver();
    didResolver.register(testIdentity);

    JWSObject jwsObject = getJWSObject(testIdentity, "fake", JWSAlgorithm.ES256);

    JWK jwk = getEcJwk(testIdentity, "fake");

    SignedJWT signedJWT = signJWT(jwsObject, jwk);
    SignedJWT spy = Mockito.spy(signedJWT);
    doThrow(new ParseException("yada", 42)).when(spy).getJWTClaimsSet();

    SignedJwtVerifier v = new SignedJwtVerifier(didResolver);
    assertThrows(SignatureParseException.class, () -> v.verify(spy));
  }

  @Test
  @SneakyThrows
  void shouldThrowSignatureVerificationException() {
    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256r1", Curve.P_256);
    final TestDidResolver didResolver = new TestDidResolver();
    didResolver.register(testIdentity);
    String keyId = extractKeyId(testIdentity);

    JWSObject jwsObject = getJWSObject(testIdentity, keyId, JWSAlgorithm.ES256);

    JWK jwk = getEcJwk(testIdentity, keyId);

    SignedJWT signedJWT = signJWT(jwsObject, jwk);
    SignedJWT spy = Mockito.spy(signedJWT);

    // this way prevents calling verify directly in test
    doThrow(new JOSEException()).when(spy).verify(any(JWSVerifier.class));

    SignedJwtVerifier v = new SignedJwtVerifier(didResolver);
    assertThrows(SignatureVerificationException.class, () -> v.verify(spy));
  }

  @RequiredArgsConstructor
  private static class TestConfig {

    final TestIdentity testIdentity;

    final JWSObject jwsObject;

    final JWK jwk;
  }

  @SneakyThrows
  private static Stream<Arguments> testConfigs() {

    TestIdentity ecTestIdentity =
        TestIdentityFactory.newIdentityWithECKeys("secp256r1", Curve.P_256);
    TestIdentity edVMTestIdentity = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    TestIdentity rsaTestIdentity = TestIdentityFactory.newIdentityWithRSAKeys();
    TestIdentity edTestIdenbtity = TestIdentityFactory.newIdentityWithEDKeys();

    return Stream.of(
        Arguments.of(generateTestConfigFromTestIdentity(ecTestIdentity, JWSAlgorithm.ES256)),
        Arguments.of(generateTestConfigFromTestIdentity(edVMTestIdentity, JWSAlgorithm.EdDSA)),
        Arguments.of(generateTestConfigFromTestIdentity(rsaTestIdentity, JWSAlgorithm.RS256)),
        Arguments.of(generateTestConfigFromTestIdentity(edTestIdenbtity, JWSAlgorithm.EdDSA)));
  }

  private static JWSObject getJWSObject(
      TestIdentity testIdentity, String keyId, JWSAlgorithm algorithm) {
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .claim("hallo", "world")
            .issuer(testIdentity.getDid().toString())
            .build();

    return new JWSObject(
        new JWSHeader.Builder(algorithm)
            .keyID(testIdentity.getDid().toUri().toString() + "#" + keyId)
            .build(),
        new Payload(claimsSet.toJSONObject()));
  }

  private static String extractKeyId(TestIdentity testIdentity) {
    return testIdentity
        .getDidDocument()
        .getVerificationMethods()
        .get(0)
        .getId()
        .toString()
        .split("#")[1];
  }

  private static JWK getEcJwk(TestIdentity testIdentity, String keyId) {
    return new ECKey.Builder(
            Curve.P_256, ((ECPublicKeyWrapper) testIdentity.getPublicKey()).getPublicKey())
        .privateKey(((ECPrivateKeyWrapper) testIdentity.getPrivateKey()).getPrivateKey())
        .keyID(testIdentity.getDid().toUri().toString() + "#" + keyId)
        .build();
  }

  private static JWK getEdJwk(TestIdentity testIdentity, String keyId) {
    return new OctetKeyPairFactory()
        .fromKeyPairWithKeyID(
            testIdentity.getDid().toUri().toString() + "#" + keyId,
            testIdentity.getPublicKey(),
            testIdentity.getPrivateKey());
  }

  private static JWK getRSAJwk(TestIdentity testIdentity) {
    return new RSAKey.Builder(((RSAPublicKeyWrapper) testIdentity.getPublicKey()).getPublicKey())
        .privateKey(((RSAPrivateKeyWrapper) testIdentity.getPrivateKey()).getPrivateKey())
        .build();
  }

  private static TestConfig generateTestConfigFromTestIdentity(
      TestIdentity testIdentity, JWSAlgorithm algorithm) {
    String keyId = extractKeyId(testIdentity);

    JWSObject jwsObject = getJWSObject(testIdentity, keyId, algorithm);
    JWK jwk = null;
    if (algorithm == JWSAlgorithm.ES256) {
      jwk = getEcJwk(testIdentity, keyId);
    } else if (algorithm == JWSAlgorithm.EdDSA) {
      jwk = getEdJwk(testIdentity, keyId);
    } else if (algorithm == JWSAlgorithm.RS256) {
      jwk = getRSAJwk(testIdentity);
    }

    return new TestConfig(testIdentity, jwsObject, Objects.requireNonNull(jwk));
  }

  private SignedJWT signJWT(JWSObject jws, JWK jwk) throws JOSEException, ParseException {
    JWSSignerFactory fac = new DefaultJWSSignerFactory();
    JWSSigner signer = fac.createJWSSigner(jwk);

    jws.sign(signer);

    return SignedJWT.parse(jws.serialize());
  }
}
