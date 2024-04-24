/*
 * ******************************************************************************
 * Copyright (c) 2021,2024 Contributors to the Eclipse Foundation
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
 * *******************************************************************************
 */

package org.eclipse.tractusx.ssi.lib.proof;

import java.security.MessageDigest;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofSigner;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofVerifier;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofSigner;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofVerifier;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** The type Sign and verify test. */
class SignAndVerifyTest {

  /** Test sign and verify ed 25519. */
  @Test
  @SneakyThrows
  void testSignAndVerify_ED25519() {
    final TestDidResolver didResolver = new TestDidResolver();

    var testIdentity = TestIdentityFactory.newIdentityWithEDVerificationMethod();

    didResolver.register(testIdentity);

    var data = "Hello World".getBytes();
    var signer = new Ed25519ProofSigner();
    var verifier = new Ed25519ProofVerifier(didResolver);

    var signature = signer.sign(new HashedLinkedData(data), testIdentity.getPrivateKey());
    var isSigned =
        verifier.verify(new HashedLinkedData(data), signature, testIdentity.getPublicKey());

    Assertions.assertTrue(isSigned);
  }

  /** Test sign and verify jws. */
  @Test
  @SneakyThrows
  void testSignAndVerify_JWS() {

    final TestDidResolver didResolver = new TestDidResolver();
    var testIdentity = TestIdentityFactory.newIdentityWithEDVerificationMethod();

    didResolver.register(testIdentity);
    var data = "Hello World".getBytes();
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    var value = digest.digest(data);

    var signer = new JWSProofSigner();
    var verifier = new JWSProofVerifier(didResolver);

    var signature = signer.sign(new HashedLinkedData(value), testIdentity.getPrivateKey());
    var isSigned =
        verifier.verify(new HashedLinkedData(value), signature, testIdentity.getPublicKey());

    Assertions.assertTrue(isSigned);
  }
}
