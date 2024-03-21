package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.util.Date;
import java.util.UUID;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.crypt.ec.ECKeyGenerator;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Test;

class SerializedJwtVCFactoryImplTest {

  @Test
  @SneakyThrows
  void shouldCreateSerializedVc() {
    SignedJwtFactory signedJwtFactory = new SignedJwtFactory(SignatureType.JWS_P256);
    SerializedJwtVCFactoryImpl serializedJwtVCFactory =
        new SerializedJwtVCFactoryImpl(signedJwtFactory);

    ECKeyGenerator g = new ECKeyGenerator(Curve.P_256.getStdName());
    KeyPair keyPair = g.generateKey();

    SignedJWT signedJWT =
        assertDoesNotThrow(
            () ->
                serializedJwtVCFactory.createVCJwt(
                    URI.create("http://example.edu/credentials/1872"),
                    new Did(new DidMethod("web"), new DidMethodIdentifier("issuer"), null),
                    new Did(new DidMethod("web"), new DidMethodIdentifier("holder"), null),
                    new Date(),
                    TestResourceUtil.getAlumniVerifiableCredential(),
                    keyPair.getPrivateKey(),
                    UUID.randomUUID().toString()));

    assertNotNull(signedJWT);
  }
}
