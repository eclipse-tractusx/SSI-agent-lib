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

import com.nimbusds.jose.JOSEException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePublicKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.ED21559ProofSigner;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.ED25519ProofVerifier;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofSigner;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofVerifier;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SignAndVerifyTest {

  @Test
  public void testSignAndVerify_ED201559()
      throws IOException, InvalidePrivateKeyFormat, InvalidePublicKeyFormat,
          KeyGenerationException {
    final TestDidDocumentResolver didDocumentResolver = new TestDidDocumentResolver();

    var testIdentity = TestIdentityFactory.newIdentityWithED25519Keys();

    didDocumentResolver.register(testIdentity);

    var data = "Hello World".getBytes();

    var signer = new ED21559ProofSigner();
    var verifier = new ED25519ProofVerifier(didDocumentResolver.withRegistry());

    var signature = signer.sign(new HashedLinkedData(data), testIdentity.getPrivateKey());
    var isSigned =
        verifier.verify(new HashedLinkedData(data), signature, testIdentity.getPublicKey());

    Assertions.assertTrue(isSigned);
  }

  @Test
  public void testSignAndVerify_JWS()
      throws IOException, JOSEException, NoSuchAlgorithmException, InvalidePrivateKeyFormat,
          InvalidePublicKeyFormat, KeyGenerationException {
    final TestDidDocumentResolver didDocumentResolver = new TestDidDocumentResolver();
    var testIdentity = TestIdentityFactory.newIdentityWithED25519Keys();

    didDocumentResolver.register(testIdentity);
    var data = "Hello World".getBytes();
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    var value = digest.digest(data);

    var signer = new JWSProofSigner();
    var verifier = new JWSProofVerifier(didDocumentResolver.withRegistry());

    var signature = signer.sign(new HashedLinkedData(value), testIdentity.getPrivateKey());
    var isSigned =
        verifier.verify(new HashedLinkedData(value), signature, testIdentity.getPublicKey());

    Assertions.assertTrue(isSigned);
  }
}
