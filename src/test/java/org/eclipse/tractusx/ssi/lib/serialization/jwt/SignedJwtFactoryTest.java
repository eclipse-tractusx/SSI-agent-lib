package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import static org.junit.Assert.assertNotNull;

import com.nimbusds.jwt.SignedJWT;
import java.util.LinkedHashMap;
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
import org.eclipse.tractusx.ssi.lib.crypt.octet.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SignedJwtFactoryTest {

  private static TestIdentity credentialIssuer;

  @BeforeAll
  public static void beforeAll() {
    SsiLibrary.initialize();
    credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys();
  }

  @Test
  @DisplayName("Should return a signed Jwt with the given Map of claims and the given private key.")
  void shouldReturnSignedJWT() {

    LinkedHashMap<String, Object> claims = new LinkedHashMap<>();
    SignedJwtFactory signedJwtFactory = new SignedJwtFactory(new OctetKeyPairFactory());
    String keyId = "key-1";
    // When

    SignedJWT signedJWT =
        signedJwtFactory.create(
            credentialIssuer.getDid(),
            credentialIssuer.getDid(),
            claims,
            credentialIssuer.getPrivateKey(),
            keyId);

    // Then
    assertNotNull(signedJWT);
  }
}
