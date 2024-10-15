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

package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import static org.junit.Assert.assertNotNull;

import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
import org.eclipse.tractusx.ssi.lib.crypt.octet.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofGenerator;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofSigner;
import org.eclipse.tractusx.ssi.lib.serialization.jsonld.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.eclipse.tractusx.ssi.lib.util.vc.TestVerifiableFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** The type Serialized jwt presentation factory impl test. */
class SerializedJwtPresentationFactoryImplTest {

  public static final int CUSTOM_EXPIRATION_TIME = 900;

  public static final int DEFAULT_EXPIRATION_TIME = 60;

  private static LinkedDataProofGenerator linkedDataProofGenerator;

  private static TestIdentity credentialIssuer;

  private static TestDidResolver didResolver;

  private static SignedJwtVerifier jwtVerifier;

  @BeforeAll
  @SneakyThrows
  public static void beforeAll() {
    SsiLibrary.initialize();

    didResolver = new TestDidResolver();

    credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys();
    didResolver.register(credentialIssuer);
    jwtVerifier = new SignedJwtVerifier(didResolver);

    linkedDataProofGenerator =
        new LinkedDataProofGenerator(
            SignatureType.JWS,
            new LinkedDataHasher(),
            new LinkedDataTransformer(),
            new Ed25519ProofSigner());
  }

  @Test
  @DisplayName("Sign JWT with EdSA and verify signature")
  void testJwtVerificationWithEdDSA() {
    credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys();
    didResolver.register(credentialIssuer);
    jwtVerifier = new SignedJwtVerifier(didResolver);

    LinkedHashMap<String, Object> claims = new LinkedHashMap<>();
    SignedJwtFactory signedJwtFactory = new SignedJwtFactory(new OctetKeyPairFactory());
    String keyId = "key-1";
    // When

    SignedJWT signedJWT =
        signedJwtFactory.create(
            credentialIssuer.getDid(),
            credentialIssuer.getDid(),
            claims,
            credentialIssuer.getPrivateKey(),
            keyId);

    // Then
    assertNotNull(signedJWT);
    Assertions.assertDoesNotThrow(() -> jwtVerifier.verify(signedJWT));
  }

  /** Test jwt serialization. */
  @SneakyThrows
  @Test
  void testJwtSerializationWithDefaultExpiration() {

    SerializedJwtPresentationFactory presentationFactory =
        new SerializedJwtPresentationFactoryImpl(
            new SignedJwtFactory(new OctetKeyPairFactory()),
            new JsonLdSerializerImpl(),
            credentialIssuer.getDid());

    VerifiableCredential credentialWithProof = getCredential();

    // Build JWT
    SignedJWT presentation =
        presentationFactory.createPresentation(
            credentialIssuer.getDid(),
            List.of(credentialWithProof),
            "test-audience",
            credentialIssuer.getPrivateKey(),
            "key-2");

    Assertions.assertNotNull(presentation);
    Assertions.assertDoesNotThrow(() -> jwtVerifier.verify(presentation));
    Assertions.assertEquals(
        DEFAULT_EXPIRATION_TIME,
        (presentation.getJWTClaimsSet().getExpirationTime().getTime()
                - presentation.getJWTClaimsSet().getIssueTime().getTime())
            / 1000);
  }

  @Test
  @DisplayName("Try to verify JWT when we do not have matching verification method in did document")
  void testJwtSerializationWithInvalidKid() {
    SerializedJwtPresentationFactory presentationFactory =
        new SerializedJwtPresentationFactoryImpl(
            new SignedJwtFactory(new OctetKeyPairFactory()),
            new JsonLdSerializerImpl(),
            credentialIssuer.getDid());

    VerifiableCredential credentialWithProof = getCredential();

    JwtConfig jwtConfig = JwtConfig.builder().expirationTime(CUSTOM_EXPIRATION_TIME).build();

    // Build JWT
    SignedJWT presentation =
        presentationFactory.createPresentation(
            credentialIssuer.getDid(),
            List.of(credentialWithProof),
            "test-audience",
            credentialIssuer.getPrivateKey(),
            "kid_"
                + UUID.randomUUID(), // pass random kid which will be not part of the did document
            jwtConfig);

    Assertions.assertNotNull(presentation);
    try {
      jwtVerifier.verify(presentation);
    } catch (Exception e) {
      Assertions.assertInstanceOf(IllegalArgumentException.class, e);
    }
  }

  @SneakyThrows
  @Test
  void testJwtSerializationWithCustomExpiration() {

    SerializedJwtPresentationFactory presentationFactory =
        new SerializedJwtPresentationFactoryImpl(
            new SignedJwtFactory(new OctetKeyPairFactory()),
            new JsonLdSerializerImpl(),
            credentialIssuer.getDid());

    VerifiableCredential credentialWithProof = getCredential();

    JwtConfig jwtConfig = JwtConfig.builder().expirationTime(CUSTOM_EXPIRATION_TIME).build();

    // Build JWT
    SignedJWT presentation =
        presentationFactory.createPresentation(
            credentialIssuer.getDid(),
            List.of(credentialWithProof),
            "test-audience",
            credentialIssuer.getPrivateKey(),
            "key-2",
            jwtConfig);

    Assertions.assertNotNull(presentation);
    Assertions.assertDoesNotThrow(() -> jwtVerifier.verify(presentation));
    Assertions.assertEquals(
        CUSTOM_EXPIRATION_TIME,
        (presentation.getJWTClaimsSet().getExpirationTime().getTime()
                - presentation.getJWTClaimsSet().getIssueTime().getTime())
            / 1000);
  }

  @SneakyThrows
  private VerifiableCredential getCredential() {
    // prepare key
    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();

    final VerifiableCredential credential =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, null);

    final Proof proof =
        linkedDataProofGenerator.createProof(
            credential, verificationMethod, credentialIssuer.getPrivateKey());

    return TestVerifiableFactory.createVerifiableCredential(credentialIssuer, proof);
  }
}
