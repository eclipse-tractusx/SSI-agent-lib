package org.eclipse.tractusx.ssi.lib.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.factories.DefaultJWSSignerFactory;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.produce.JWSSignerFactory;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import org.eclipse.tractusx.ssi.lib.crypt.ec.ECPrivateKeyWrapper;
import org.eclipse.tractusx.ssi.lib.crypt.ec.ECPublicKeyWrapper;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.JwtException;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Test;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
class SignedJwtVerifierTest {
  @Test
  void verifyEcSignature()
      throws JOSEException, InvalidAlgorithmParameterException, NoSuchAlgorithmException,
          ParseException, DidDocumentResolverNotRegisteredException, JwtException {
    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256r1", Curve.P_256);
    String keyId =
        testIdentity
            .getDidDocument()
            .getVerificationMethods()
            .get(0)
            .getId()
            .toString()
            .split("#")[1];
    JWTClaimsSet claimsSet =
        new JWTClaimsSet.Builder()
            .claim("hallo", "world")
            .issuer(testIdentity.getDid().toString())
            .build();

    JWSObject jwsObject =
        new JWSObject(
            new JWSHeader.Builder(JWSAlgorithm.ES256)
                .keyID(testIdentity.getDid().toUri().toString() + "#" + keyId)
                .build(),
            new Payload(claimsSet.toJSONObject()));

    ECKey ecKey =
        new ECKey.Builder(
                Curve.P_256, ((ECPublicKeyWrapper) testIdentity.getPublicKey()).getPublicKey())
            .privateKey(((ECPrivateKeyWrapper) testIdentity.getPrivateKey()).getPrivateKey())
            .keyID(testIdentity.getDid().toUri().toString() + "#" + keyId)
            .build();

    final TestDidResolver didResolver = new TestDidResolver();
    didResolver.register(testIdentity);

    SignedJWT signedJWT = signJWT(jwsObject, ecKey);

    SignedJwtVerifier v = new SignedJwtVerifier(didResolver);
    assertTrue(v.verify(signedJWT));
  }

  private SignedJWT signJWT(JWSObject jws, JWK jwk) throws JOSEException, ParseException {
    JWSSignerFactory fac = new DefaultJWSSignerFactory();
    JWSSigner signer = fac.createJWSSigner(jwk);

    jws.sign(signer);

    return SignedJWT.parse(jws.serialize());
  }
}
