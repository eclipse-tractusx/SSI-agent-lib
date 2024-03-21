package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofGenerator;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofSigner;
import org.eclipse.tractusx.ssi.lib.serialization.jsonld.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.eclipse.tractusx.ssi.lib.util.vc.TestVerifiableFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** The type Serialized jwt presentation factory impl test. */
class SerializedJwtPresentationFactoryImplTest {

  private LinkedDataProofGenerator linkedDataProofGenerator;

  private TestIdentity credentialIssuer;
  private TestDidResolver didResolver;

  private SignedJwtVerifier jwtVerifier;

  @SneakyThrows
  private static Stream<Arguments> testConfigs() {
    JwtConfig conf = JwtConfig.builder().expirationTime(99).build();
    JwtConfig confNoTime = JwtConfig.builder().build();
    return Stream.of(Arguments.of(confNoTime), Arguments.of(conf));
  }

  /** Test jwt serialization. */
  @SneakyThrows
  @ParameterizedTest
  @MethodSource("testConfigs")
  void testJwtSerialization(JwtConfig conf) {
    SsiLibrary.initialize();
    this.didResolver = new TestDidResolver();

    credentialIssuer = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    didResolver.register(credentialIssuer);
    jwtVerifier = new SignedJwtVerifier(didResolver);

    linkedDataProofGenerator =
        new LinkedDataProofGenerator(
            SignatureType.JWS,
            new LinkedDataHasher(),
            new LinkedDataTransformer(),
            new Ed25519ProofSigner());

    // prepare key
    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();

    final VerifiableCredential credential =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, null);

    final Proof proof =
        linkedDataProofGenerator.createProof(
            credential, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiableCredential credentialWithProof =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, proof);

    SerializedJwtPresentationFactory presentationFactory =
        new SerializedJwtPresentationFactoryImpl(
            new SignedJwtFactory(), new JsonLdSerializerImpl(), credentialIssuer.getDid());

    // Build JWT
    SignedJWT presentation;
    if (conf.getExpirationTime() == 0) {
      presentation =
          presentationFactory.createPresentation(
              credentialIssuer.getDid(),
              List.of(credentialWithProof),
              "test-audience",
              credentialIssuer.getPrivateKey(),
              "key-1");
    } else {
      presentation =
          presentationFactory.createPresentation(
              credentialIssuer.getDid(),
              List.of(credentialWithProof),
              "test-audience",
              credentialIssuer.getPrivateKey(),
              "key-1",
              conf);
    }
    assertResult(presentation);
  }

  @SneakyThrows
  private void assertResult(SignedJWT presentation) {
    Assertions.assertNotNull(presentation);
    Assertions.assertDoesNotThrow(() -> jwtVerifier.verify(presentation));
    JWTClaimsSet jwtClaimsSet = presentation.getJWTClaimsSet();
    Map<String, Object> vp = jwtClaimsSet.getJSONObjectClaim("vp");

    Assertions.assertEquals(vp.get("id"), jwtClaimsSet.getJWTID());
  }
}
