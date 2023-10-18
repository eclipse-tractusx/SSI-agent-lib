/********************************************************************************
 * Copyright (c) 2021,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.ssi.lib.proof;

import com.nimbusds.jose.jwk.Curve;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePublicKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofSigner;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofVerifier;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofSigner;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofVerifier;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SignAndVerifyTest {

  @Test
  public void testSignAndVerify_ED201559()
      throws IOException, InvalidePrivateKeyFormat, InvalidePublicKeyFormat,
          KeyGenerationException {
    final TestDidResolver didResolver = new TestDidResolver();

    var testIdentity = TestIdentityFactory.newIdentityWithED25519Keys();

    didResolver.register(testIdentity);

    var data = "Hello World".getBytes();
    var signer = new Ed25519ProofSigner();
    var verifier = new Ed25519ProofVerifier(didResolver);

    var signature = signer.sign(new HashedLinkedData(data), testIdentity.getPrivateKey());
    var isSigned =
        verifier.verify(new HashedLinkedData(data), signature, testIdentity.getPublicKey());

    Assertions.assertTrue(isSigned);
  }

  @Test
  public void testSignAndVerify_JWS_ED()
      throws IOException, NoSuchAlgorithmException, InvalidePrivateKeyFormat,
          KeyGenerationException {
    var testIdentity = TestIdentityFactory.newIdentityWithED25519Keys();
    verifyJws(testIdentity, SignatureType.JWS);
  }

  @Test
  public void testSignAndVerify_JWS_RSA()
      throws NoSuchAlgorithmException, InvalidePrivateKeyFormat {
    var testIdentity = TestIdentityFactory.newIdentityWithRSAKeys();
    verifyJws(testIdentity, SignatureType.JWS_RSA);
  }

  @Test
  public void testSignAndVerify_JWS_EC_P256()
      throws NoSuchAlgorithmException, InvalidePrivateKeyFormat,
          InvalidAlgorithmParameterException {
    var testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256r1", Curve.P_256);
    verifyJws(testIdentity, SignatureType.JWS_P256);
  }

  @Test
  public void testSignAndVerify_JWS_EC_P384()
      throws NoSuchAlgorithmException, InvalidePrivateKeyFormat,
          InvalidAlgorithmParameterException {
    var testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp384r1", Curve.P_384);
    verifyJws(testIdentity, SignatureType.JWS_P384);
  }

  @Test
  public void testSignAndVerify_JWS_EC_256K1()
      throws NoSuchAlgorithmException, InvalidePrivateKeyFormat,
          InvalidAlgorithmParameterException {
    var testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256k1", Curve.SECP256K1);
    verifyJws(testIdentity, SignatureType.JWS_SEC_P_256K1);
  }

  void verifyJws(TestIdentity testIdentity, SignatureType type)
      throws NoSuchAlgorithmException, InvalidePrivateKeyFormat {
    final TestDidResolver didResolver = new TestDidResolver();
    didResolver.register(testIdentity);
    var data = "Hello World".getBytes();
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    var value = digest.digest(data);

    var signer = new JWSProofSigner(type);
    var verifier = new JWSProofVerifier(didResolver);

    var signature = signer.sign(new HashedLinkedData(value), testIdentity.getPrivateKey());
    var isSigned =
        verifier.verify(new HashedLinkedData(value), signature, testIdentity.getPublicKey(), type);

    Assertions.assertTrue(isSigned);
  }
}
