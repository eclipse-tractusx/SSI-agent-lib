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
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.ed25519.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The type Linked data transformer test.
 */
class LinkedDataTransformerTest {

  private final LinkedDataTransformer linkedDataTransformer = new LinkedDataTransformer();

  /**
   * Test linked data transformer.
   */
  @Test
  @SneakyThrows
  void testLinkedDataTransformer() {
    final VerifiableCredentialBuilder verifiableCredentialBuilder =
        new VerifiableCredentialBuilder();

    final VerifiableCredentialSubject verifiableCredentialSubject =
        new VerifiableCredentialSubject(Map.of("test", "test"));
    final VerifiableCredential credentialWithoutProof =
        verifiableCredentialBuilder
            .id(URI.create("did:test:id"))
            .type(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .issuer(URI.create("did:test:isser"))
            .expirationDate(Instant.now().plusSeconds(3600))
            .issuanceDate(Instant.now())
            .verifiableCredentialStatus(TestResourceUtil.getStatusListEntry())
            .credentialSubject(verifiableCredentialSubject)
            .verifiableCredentialStatus(TestResourceUtil.getStatusListEntry())
            .build();

    // check status added in VC

    Assertions.assertTrue(credentialWithoutProof.getVerifiableCredentialStatus().isPresent());

    var transformedWithoutProof = linkedDataTransformer.transform(credentialWithoutProof);

    final VerifiableCredential verifiableCredentialWithProof =
        verifiableCredentialBuilder
            .proof(
                new Ed25519Signature2020(
                    Map.of(
                        Proof.TYPE,
                        "Ed25519Signature2020",
                        Ed25519Signature2020.PROOF_PURPOSE,
                        Ed25519Signature2020.ASSERTION_METHOD,
                        Ed25519Signature2020.VERIFICATION_METHOD,
                        "did:test:id",
                        Ed25519Signature2020.CREATED,
                        Instant.now().toString(),
                        Ed25519Signature2020.PROOF_VALUE,
                        "MnWjKcdzqVcpeH1bZGtvjw==")))
            .build();

    var transformedWithProof = linkedDataTransformer.transform(verifiableCredentialWithProof);

    Assertions.assertNotEquals(transformedWithProof.getValue(), transformedWithoutProof.getValue());
  }
}
