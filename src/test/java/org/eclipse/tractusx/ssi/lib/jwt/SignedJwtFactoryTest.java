package org.eclipse.tractusx.ssi.lib.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.crypt.ec.ECKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.x25519.X25519Generator;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;
import org.junit.jupiter.api.Test;

class SignedJwtFactoryTest {

  @Test
  @SneakyThrows
  void shouldCreateSignedJwt() {
    SignedJwtFactory signedJwtFactory = new SignedJwtFactory();

    X25519Generator g = new X25519Generator();
    KeyPair keyPair = g.generateKey();

    SignedJWT signedJWT =
        assertDoesNotThrow(
            () ->
                signedJwtFactory.create(
                    URI.create("did:web:id"),
                    new Did(new DidMethod("web"), new DidMethodIdentifier("issuer"), null),
                    new Did(new DidMethod("web"), new DidMethodIdentifier("holder"), null),
                    new Date(),
                    new LinkedHashMap<>(Map.of("hallo", "du")),
                    keyPair.getPrivateKey(),
                    UUID.randomUUID().toString()));

    assertEquals(JWSAlgorithm.EdDSA, signedJWT.getHeader().getAlgorithm());
  }

  @Test
  @SneakyThrows
  void shouldCreateSignedJwtWithEC() {
    SignedJwtFactory signedJwtFactory = new SignedJwtFactory(SignatureType.JWS_P256);

    ECKeyGenerator g = new ECKeyGenerator(Curve.P_256.getStdName());
    KeyPair keyPair = g.generateKey();

    SignedJWT signedJWT =
        assertDoesNotThrow(
            () ->
                signedJwtFactory.create(
                    URI.create("did:web:id"),
                    new Did(new DidMethod("web"), new DidMethodIdentifier("issuer"), null),
                    new Did(new DidMethod("web"), new DidMethodIdentifier("holder"), null),
                    new Date(),
                    new LinkedHashMap<>(Map.of("hallo", "du")),
                    keyPair.getPrivateKey(),
                    UUID.randomUUID().toString()));

    assertEquals(JWSAlgorithm.ES256, signedJWT.getHeader().getAlgorithm());
  }
}
