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
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
import org.eclipse.tractusx.ssi.lib.model.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataSigner;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataVerifier;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
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
  public void testLinkedDataProofCheck() throws IOException {
    SsiLibrary.initialize();
    this.didDocumentResolver = new TestDidDocumentResolver();

    credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys(false);
    didDocumentResolver.register(credentialIssuer);

    linkedDataProofValidation =
        new LinkedDataProofValidation(
            new LinkedDataHasher(),
            new LinkedDataTransformer(),
            new LinkedDataVerifier(didDocumentResolver.withRegistry()));
    linkedDataProofGenerator =
        new LinkedDataProofGenerator(
            new LinkedDataHasher(), new LinkedDataTransformer(), new LinkedDataSigner());

    // prepare key
    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();
    final byte[] privateKey = credentialIssuer.getPrivateKey();

    final VerifiableCredential credential = createCredential(null);

    final Ed25519Signature2020 proof =
        linkedDataProofGenerator.createEd25519Signature2020(
            credential, verificationMethod, privateKey);

    final VerifiableCredential credentialWithProof = createCredential(proof);

    var isOk = linkedDataProofValidation.checkProof(credentialWithProof);

    Assertions.assertTrue(isOk);
  }

  //   @Test
  //   public void testLinkedDataProofCheckBPNCredential() throws IOException {

  //     this.didDocumentResolver = new TestDidDocumentResolver();

  //     credentialIssuer = TestIdentityFactory.newBPNIdentityWithED25519Keys(false);

  //     didDocumentResolver.register(credentialIssuer);

  //     linkedDataProofValidation =
  //         new LinkedDataProofValidation(
  //             new LinkedDataHasher(),
  //             new LinkedDataTransformer(),
  //             new LinkedDataVerifier(didDocumentResolver.withRegistry()));

  //     final VerifiableCredential credentialWithProof =
  //         new VerifiableCredential(TestResourceUtil.getBPNVerifiableCredential());

  //     var isOk = linkedDataProofValidation.checkProof(credentialWithProof);

  //     Assertions.assertTrue(isOk);
  //   }

  //   @Test
  //   public void testJWTProofCheck() throws IOException {

  //     this.didDocumentResolver = new TestDidDocumentResolver();

  //     credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys(false);
  //     didDocumentResolver.register(credentialIssuer);

  //     // prepare key
  //     final URI verificationMethod =
  //         credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();
  //     final byte[] privateKey = credentialIssuer.getPrivateKey();

  //     VerifiableCredential credential = createCredential(null);

  //     // Ed25519 Proof Builder
  //     final LinkedDataProofGenerator generator = LinkedDataProofGenerator.create();
  //     final Ed25519Signature2020 proof =
  //         generator.createEd25519Signature2020(
  //             credential,verificationMethod , privateKey);

  //     final Ed25519Signature2020 proof =
  //         linkedDataProofGenerator.createEd25519Signature2020(
  //             credential, verificationMethod, privateKey);

  //     final VerifiableCredential credentialWithProof = createCredential(proof);

  //     var isOk = linkedDataProofValidation.checkProof(credentialWithProof);

  //     Assertions.assertTrue(isOk);
  //   }

  @SneakyThrows
  private VerifiableCredential createCredential(Ed25519Signature2020 proof) {
    final VerifiableCredentialBuilder verifiableCredentialBuilder =
        new VerifiableCredentialBuilder();

    final VerifiableCredentialSubject verifiableCredentialSubject =
        new VerifiableCredentialSubject(Map.of("foo", "bar"));

    return verifiableCredentialBuilder
        .id(URI.create("did:test:id"))
        .type(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
        .issuer(credentialIssuer.getDid().toUri())
        .expirationDate(Instant.parse("2025-02-15T17:21:42Z").plusSeconds(3600))
        .issuanceDate(Instant.parse("2023-02-15T17:21:42Z"))
        .proof(proof)
        .credentialSubject(verifiableCredentialSubject)
        .build();
  }
}
