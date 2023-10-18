package org.eclipse.tractusx.ssi.lib.model.did;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.crypt.jwk.JsonWebKey;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PublicKey;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.junit.jupiter.api.Test;

class JsonWebKey2020BuilderTest {

  @SneakyThrows
  @Test
  public void testJsonWebKey2020VerificationMethod() {
    final Did did = DidWebFactory.fromHostname("localhost");
    String keyId = "1";
    OctetKeyPair octetKeyPair =
        new OctetKeyPairGenerator(Curve.Ed25519).keyID(keyId).keyUse(KeyUse.SIGNATURE).generate();

    IPrivateKey privateKey = new x21559PrivateKey(octetKeyPair.getDecodedD());
    IPublicKey publicKey = new x21559PublicKey(octetKeyPair.getDecodedX());

    // JWK
    JsonWebKey jwk = new JsonWebKey(keyId, publicKey, privateKey);

    final JWKVerificationMethodBuilder builder = new JWKVerificationMethodBuilder();
    final JWKVerificationMethod jwk2020VerificationMethod =
        builder.did(did).jwk(octetKeyPair).build();

    assertNotNull(jwk2020VerificationMethod);
    assertEquals(jwk2020VerificationMethod.getType(), "JsonWebKey2020");
    assertEquals(jwk2020VerificationMethod.getId().toString(), "did:web:localhost#" + keyId);
    assertEquals(jwk2020VerificationMethod.getController().toString(), "did:web:localhost");

    assertEquals(
        ((OctetKeyPair) jwk2020VerificationMethod.getJwk()).getKeyType().getValue(), "OKP");
    assertEquals(
        ((OctetKeyPair) jwk2020VerificationMethod.getJwk()).getCurve().getName(), "Ed25519");
    assertNotNull(((OctetKeyPair) jwk2020VerificationMethod.getJwk()).getX());
  }
}
