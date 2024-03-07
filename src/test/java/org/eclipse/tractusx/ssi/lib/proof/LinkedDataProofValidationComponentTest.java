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

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.eclipse.tractusx.ssi.lib.util.vc.TestVerifiableFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The type Linked data proof validation component test. */
class LinkedDataProofValidationComponentTest {

  private LinkedDataProofValidation linkedDataProofValidation;
  private LinkedDataProofGenerator linkedDataProofGenerator;

  private TestIdentity credentialIssuer;
  private TestDidResolver didResolver;

  /** Sets . */
  @BeforeEach
  public void setup() {
    SsiLibrary.initialize();
    this.didResolver = new TestDidResolver();
  }

  /** Test vc proof failure on manipulated credential. */
  @Test
  @SneakyThrows
  void testVCProofFailureOnManipulatedCredential() {
    credentialIssuer = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    didResolver.register(credentialIssuer);

    // Generator
    linkedDataProofGenerator = LinkedDataProofGenerator.newInstance(SignatureType.ED25519);

    // Verification
    linkedDataProofValidation = LinkedDataProofValidation.newInstance(this.didResolver);

    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();

    final VerifiableCredential credential =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, null);

    final Proof proof =
        linkedDataProofGenerator.createProof(
            credential, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiableCredential credentialWithProof =
        TestVerifiableFactory.attachProof(credential, proof);

    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(VerifiableCredential.TIME_FORMAT).withZone(ZoneOffset.UTC);
    credentialWithProof.put(
        VerifiableCredential.EXPIRATION_DATE,
        formatter.format(Instant.now().plusSeconds(60 * 60 * 24 * 365 * 10)));

    var isOk = linkedDataProofValidation.verify(credentialWithProof);

    Assertions.assertFalse(isOk);
  }

  /** Test vc ed 25519 proof generation and verification. */
  @Test
  @SneakyThrows
  void testVCEd25519ProofGenerationAndVerification() {

    credentialIssuer = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    didResolver.register(credentialIssuer);

    // Generator
    linkedDataProofGenerator = LinkedDataProofGenerator.newInstance(SignatureType.ED25519);

    // Verification
    linkedDataProofValidation = LinkedDataProofValidation.newInstance(this.didResolver);

    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();

    final VerifiableCredential credential =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, null);

    final Proof proof =
        linkedDataProofGenerator.createProof(
            credential, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiableCredential credentialWithProof =
        TestVerifiableFactory.attachProof(credential, proof);

    var isOk = linkedDataProofValidation.verify(credentialWithProof);

    Assertions.assertTrue(isOk);
  }

  /** Test vcjws proof generation and verification. */
  @Test
  @SneakyThrows
  void testVCJWSProofGenerationAndVerification() {

    credentialIssuer = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    didResolver.register(credentialIssuer);

    // Generator
    linkedDataProofGenerator = LinkedDataProofGenerator.newInstance(SignatureType.JWS);

    // Verifier
    linkedDataProofValidation = LinkedDataProofValidation.newInstance(this.didResolver);

    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(1).getId();

    final VerifiableCredential credential =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, null);

    final JWSSignature2020 proof =
        (JWSSignature2020)
            linkedDataProofGenerator.createProof(
                credential, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiableCredential credentialWithProof =
        TestVerifiableFactory.attachProof(credential, proof);

    var isOk = linkedDataProofValidation.verify(credentialWithProof);

    Assertions.assertTrue(isOk);
  }

  /** Test vp ed 25519 proof generation and verification. */
  @Test
  @SneakyThrows
  void testVPEd25519ProofGenerationAndVerification() {

    credentialIssuer = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    didResolver.register(credentialIssuer);

    // Generator
    linkedDataProofGenerator = LinkedDataProofGenerator.newInstance(SignatureType.ED25519);

    // Verifier
    linkedDataProofValidation = LinkedDataProofValidation.newInstance(didResolver);

    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();

    final VerifiableCredential vc =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, null);

    final Proof vcProof =
        linkedDataProofGenerator.createProof(
            vc, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiableCredential vcWithProof = TestVerifiableFactory.attachProof(vc, vcProof);

    final VerifiablePresentation vp =
        TestVerifiableFactory.createVerifiablePresentation(
            credentialIssuer, List.of(vcWithProof), null);

    final Proof vpProof =
        linkedDataProofGenerator.createProof(
            vp, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiablePresentation vpWithProof = TestVerifiableFactory.attachProof(vp, vpProof);

    var isOk = linkedDataProofValidation.verify(vpWithProof);

    Assertions.assertTrue(isOk);
  }

  /** Test vpjws proof generation and verification. */
  @Test
  @SneakyThrows
  void testVPJWSProofGenerationAndVerification() {

    credentialIssuer = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    didResolver.register(credentialIssuer);

    // Generator
    linkedDataProofGenerator = LinkedDataProofGenerator.newInstance(SignatureType.JWS);

    // Verifier
    linkedDataProofValidation = LinkedDataProofValidation.newInstance(didResolver);

    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(1).getId();

    final VerifiableCredential vc =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, null);

    final JWSSignature2020 vcProof =
        (JWSSignature2020)
            linkedDataProofGenerator.createProof(
                vc, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiableCredential vcWithProof = TestVerifiableFactory.attachProof(vc, vcProof);

    final VerifiablePresentation vp =
        TestVerifiableFactory.createVerifiablePresentation(
            credentialIssuer, List.of(vcWithProof), null);

    final JWSSignature2020 vpProof =
        (JWSSignature2020)
            linkedDataProofGenerator.createProof(
                vp, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiablePresentation vpWithProof = TestVerifiableFactory.attachProof(vp, vpProof);

    var isOk = linkedDataProofValidation.verify(vpWithProof);

    Assertions.assertTrue(isOk);
  }

  /** Test verification method. */
  @Test
  @SneakyThrows
  void testVerificationMethodOfVC() {

    credentialIssuer = TestIdentityFactory.newIdentityWithEDVerificationMethod();
    didResolver.register(credentialIssuer);

    // Generator
    linkedDataProofGenerator = LinkedDataProofGenerator.newInstance(SignatureType.ED25519);

    // Verification
    linkedDataProofValidation = LinkedDataProofValidation.newInstance(this.didResolver);

    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();

    final VerifiableCredential credential =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, null);

    credential.replace("issuer", "did:test:4efee956-GGGG-42c0-8efb-0716e5e3f8de");
    final Proof proof =
        linkedDataProofGenerator.createProof(
            credential, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiableCredential credentialWithProof =
        TestVerifiableFactory.attachProof(credential, proof);

    var isOk = linkedDataProofValidation.verify(credentialWithProof);

    Assertions.assertFalse(isOk);
  }
}
