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

import java.io.IOException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePublicKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.eclipse.tractusx.ssi.lib.util.vc.TestCredentialFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LinkedDataProofValidationComponentTest {

  private LinkedDataProofValidation linkedDataProofValidation;
  private LinkedDataProofGenerator linkedDataProofGenerator;

  private TestIdentity credentialIssuer;
  private TestDidDocumentResolver didDocumentResolver;

  @BeforeEach
  public void setup() {}

  @Test
  public void testEd21559ProofGenerationAndVerification()
      throws IOException, UnsupportedSignatureTypeException, InvalidePrivateKeyFormat,
          KeyGenerationException, InvalidePublicKeyFormat {
    SsiLibrary.initialize();
    this.didDocumentResolver = new TestDidDocumentResolver();

    credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys();
    didDocumentResolver.register(credentialIssuer);

    // Generator
    linkedDataProofGenerator = LinkedDataProofGenerator.newInstance(SignatureType.ED21559);

    // Verification
    linkedDataProofValidation =
        LinkedDataProofValidation.newInstance(
            SignatureType.ED21559, didDocumentResolver.withRegistry());

    // prepare key
    // 0 == ED21559
    // 1 == JWS
    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();

    final VerifiableCredential credential =
        TestCredentialFactory.createCredential(credentialIssuer, null);

    final Proof proof =
        linkedDataProofGenerator.createProof(
            credential, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiableCredential credentialWithProof =
        TestCredentialFactory.attachProof(credential, proof);

    var isOk = linkedDataProofValidation.verifiyProof(credentialWithProof);

    Assertions.assertTrue(isOk);
  }

  @Test
  public void testJWSproofGenerationAndVerification()
      throws IOException, UnsupportedSignatureTypeException, NoSuchAlgorithmException,
          InvalidePrivateKeyFormat, KeyGenerationException, InvalidePublicKeyFormat {
    SsiLibrary.initialize();
    this.didDocumentResolver = new TestDidDocumentResolver();

    credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys();
    didDocumentResolver.register(credentialIssuer);

    // Generator
    linkedDataProofGenerator = LinkedDataProofGenerator.newInstance(SignatureType.JWS);
    // Verifier
    linkedDataProofValidation =
        LinkedDataProofValidation.newInstance(
            SignatureType.JWS, didDocumentResolver.withRegistry());

    // prepare key
    // 0 == ED21559
    // 1 == JWS
    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(1).getId();

    final VerifiableCredential credential =
        TestCredentialFactory.createCredential(credentialIssuer, null);

    final JWSSignature2020 proof =
        (JWSSignature2020)
            linkedDataProofGenerator.createProof(
                credential, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiableCredential credentialWithProof =
        TestCredentialFactory.attachProof(credential, proof);

    var isOk = linkedDataProofValidation.verifiyProof(credentialWithProof);

    Assertions.assertTrue(isOk);
  }
}
