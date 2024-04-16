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

package org.eclipse.tractusx.ssi.examples;

import java.io.IOException;
import java.util.List;

import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.crypt.octet.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationType;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedJwtPresentationFactory;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedJwtPresentationFactoryImpl;

import com.nimbusds.jwt.SignedJWT;

/**
 * This is an example class to demonstrate how to create a Verifiable Presentation in JSON-LD and
 * JWT format
 */
class VP {
  /**
   * Create a verifiable presentation.
   *
   * @param issuer the issuer
   * @param credentials the credentials
   * @return the verifiable presentation
   */
  public static VerifiablePresentation createVP(
      Did issuer, List<VerifiableCredential> credentials) {
    final VerifiablePresentationBuilder verifiablePresentationBuilder =
        new VerifiablePresentationBuilder();
    final VerifiablePresentation verifiablePresentation =
        verifiablePresentationBuilder
            .id(issuer.toUri()) // NOTE: Provide unique ID number to each VP you create!!
            .type(List.of(VerifiablePresentationType.VERIFIABLE_PRESENTATION))
            .verifiableCredentials(credentials)
            .build();
    return verifiablePresentation;
  }

    /**
   * Create vp as a signed jwt.
   *
   * @param issuer the issuer
   * @param credentials the credentials
   * @param audience the audience
   * @param privateKey the private key
   * @param publicKey the public key
   * @param keyId the key id
   * @return the signed jwt
   * @throws IOException the io exception
   */
  public static SignedJWT createVPAsJWT(
      Did issuer,
      List<VerifiableCredential> credentials,
      String audience,
      IPrivateKey privateKey,
      IPublicKey publicKey,
      String keyId)
      throws IOException {

    // Extracting keys
    // final Ed25519KeySet keySet = new Ed25519KeySet(privateKey, publicKey);
    // final Ed25519Key signingKey = Ed25519Key.asPrivateKey(keySet.getPrivateKey());

    final SerializedJwtPresentationFactory presentationFactory =
        new SerializedJwtPresentationFactoryImpl(
            new SignedJwtFactory(new OctetKeyPairFactory()), new JsonLdSerializerImpl(), issuer);

    return presentationFactory.createPresentation(
        issuer, credentials, audience, privateKey, keyId);
  }
}
