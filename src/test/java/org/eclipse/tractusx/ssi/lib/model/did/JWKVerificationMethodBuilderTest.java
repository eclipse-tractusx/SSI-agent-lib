package org.eclipse.tractusx.ssi.lib.model.did;

import com.nimbusds.jose.jwk.Curve;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class JWKVerificationMethodBuilderTest {

  @Test
  @SneakyThrows
  void testWithInvalidData() {

    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256r1", Curve.P_256);

    Assertions.assertThrows(
        Exception.class,
        () -> {
          new JWKVerificationMethodBuilder().jwk(testIdentity.getPublicKey().toJwk()).build();
        });
  }
}
