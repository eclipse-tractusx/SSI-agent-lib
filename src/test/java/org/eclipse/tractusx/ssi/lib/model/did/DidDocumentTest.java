/*
 * ******************************************************************************
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
 * *******************************************************************************
 */

package org.eclipse.tractusx.ssi.lib.model.did;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.Curve;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.tractusx.ssi.lib.model.ProofPurpose;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** The type Did document test. */
public class DidDocumentTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  /** Can create did document. */
  @Test
  public void canCreateDidDocument() {
    final List<Map<String, Object>> documents = TestResourceUtil.getAllDidDocuments();

    for (Map<String, Object> document : documents) {
      assertDoesNotThrow(() -> new DidDocument(document));
    }
  }

  /** Can serialize did document. */
  @Test
  @SneakyThrows
  public void canSerializeDidDocument() {
    final List<Map<String, Object>> documents = TestResourceUtil.getAllDidDocuments();
    for (Map<String, Object> document : documents) {
      var doc = new DidDocument(document);
      var json = doc.toJson();
      var mapFromJson = MAPPER.readValue(json, Map.class);
      Assertions.assertEquals(mapFromJson.get(DidDocument.ID), doc.get(DidDocument.ID));
    }
  }

  /** Can deserialize did document. */
  @Test
  @SneakyThrows
  public void canDeserializeDidDocument() {
    final List<Map<String, Object>> documents = TestResourceUtil.getAllDidDocuments();
    for (Map<String, Object> document : documents) {
      var docFromMap = new DidDocument(document);
      var json = docFromMap.toJson();
      var docFromJson = DidDocument.fromJson(json);
      Assertions.assertEquals(docFromJson.get(DidDocument.ID), docFromMap.get(DidDocument.ID));
    }
  }

  @Test
  void createDidDocumentWithProofPurpose()
      throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
    Did assertionMethod = DidParser.parse("did:web:document-test");
    TestIdentityFactory.VerificationMethodConfig assertionMethodVM =
        TestIdentityFactory.generateVerificationMethod(
            Curve.SECP256K1, "secp256k1", assertionMethod);

    Did authentication = DidParser.parse("did:web:document-test");
    TestIdentityFactory.VerificationMethodConfig authenticationVM =
        TestIdentityFactory.generateVerificationMethod(
            Curve.SECP256K1, "secp256k1", authentication);

    Did capabilityI = DidParser.parse("did:web:document-test");
    TestIdentityFactory.VerificationMethodConfig capabilityIVM =
        TestIdentityFactory.generateVerificationMethod(Curve.SECP256K1, "secp256k1", capabilityI);

    Did capabilityD = DidParser.parse("did:web:document-test");
    TestIdentityFactory.VerificationMethodConfig capabilityDVM =
        TestIdentityFactory.generateVerificationMethod(Curve.SECP256K1, "secp256k1", capabilityD);

    Did embeddedAuthentication = DidParser.parse("did:web:document-test");
    TestIdentityFactory.VerificationMethodConfig embeddedAuthenticationVM =
        TestIdentityFactory.generateVerificationMethod(
            Curve.SECP256K1, "secp256k1", embeddedAuthentication);

    DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
    didDocumentBuilder
        .id(URI.create("did:web:document-test"))
        .assertionMethod(List.of(assertionMethod.toUri()))
        .authentication(
            List.of(authentication.toUri(), embeddedAuthenticationVM.getVerificationMethod()))
        .capabilityDelegation(List.of(capabilityD.toUri()))
        .capabilityInvocation(List.of(capabilityI.toUri()))
        .verificationMethods(
            List.of(
                assertionMethodVM.getVerificationMethod(),
                authenticationVM.getVerificationMethod(),
                capabilityIVM.getVerificationMethod(),
                capabilityDVM.getVerificationMethod()));

    DidDocument didDocument = assertDoesNotThrow(didDocumentBuilder::build);

    assertTrue(CollectionUtils.isNotEmpty(didDocument.getVerificationMethods()));
    assertTrue(
        CollectionUtils.isNotEmpty(
            (Collection<?>) didDocument.get(ProofPurpose.AUTHENTICATION.purpose)));
    assertTrue(
        CollectionUtils.isNotEmpty(
            (Collection<?>) didDocument.get(ProofPurpose.ASSERTION_METHOD.purpose)));
    assertTrue(
        CollectionUtils.isNotEmpty(
            (Collection<?>) didDocument.get(ProofPurpose.CAPABILITY_DELEGATION.purpose)));
    assertTrue(
        CollectionUtils.isNotEmpty(
            (Collection<?>) didDocument.get(ProofPurpose.CAPABILITY_INVOCATION.purpose)));

    List<Object> authenticationRelationship =
        (List<Object>) didDocument.get(ProofPurpose.AUTHENTICATION.purpose);

    boolean foundEmbedded = false;
    boolean foundReference = false;
    for (Object o : authenticationRelationship) {
      if (o instanceof URI) {
        foundReference = true;
      } else if (o instanceof VerificationMethod) {
        foundEmbedded = true;
      }
    }

    assertTrue(foundReference && foundEmbedded);
  }
}
