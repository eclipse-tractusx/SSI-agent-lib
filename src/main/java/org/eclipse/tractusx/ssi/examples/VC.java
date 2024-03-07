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

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.exception.json.TransformJsonLdException;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPrivateKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureGenerateFailedException;
import org.eclipse.tractusx.ssi.lib.exception.proof.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.proof.ed25519.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofGenerator;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;

/** This is example class to demonstrate how create Verifiable Credentials */
public class VC {
  private VC() {
    // static
  }
  /**
   * Create verifiable credential without proof
   *
   * @return the verifiable credential
   */
  public static VerifiableCredential createVCWithoutProof() {

    // VC Bulider
    final VerifiableCredentialBuilder verifiableCredentialBuilder =
        new VerifiableCredentialBuilder();

    // VC Subject
    final VerifiableCredentialSubject verifiableCredentialSubject =
        new VerifiableCredentialSubject(Map.of("test", "test"));

    // Using Builder
    return verifiableCredentialBuilder
        .id(URI.create("did:test:id"))
        .type(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
        .issuer(URI.create("did:test:isser"))
        .expirationDate(Instant.now().plusSeconds(3600))
        .issuanceDate(Instant.now())
        .credentialSubject(verifiableCredentialSubject)
        .build();
  }

  /**
   * Create verifiable credential with ED25519 proof
   *
   * @param credential the credential
   * @param privateKey the private key
   * @param issuer the issuer
   * @return the verifiable credential
   * @throws UnsupportedSignatureTypeException the unsupported signature type exception
   * @throws SsiException the ssi exception
   * @throws InvalidePrivateKeyFormat the invalide private key format
   */
  public static VerifiableCredential createVCWithED25519Proof(
      VerifiableCredential credential, IPrivateKey privateKey, Did issuer)
      throws UnsupportedSignatureTypeException, InvalidPrivateKeyFormatException,
          SignatureGenerateFailedException, TransformJsonLdException {

    // VC Builder
    final VerifiableCredentialBuilder builder =
        new VerifiableCredentialBuilder()
            .context(credential.getContext())
            .id(credential.getId())
            .issuer(issuer.toUri())
            .issuanceDate(Instant.now())
            .credentialSubject(credential.getCredentialSubject())
            .expirationDate(credential.getExpirationDate())
            .type(credential.getTypes());

    // Ed25519 Proof Builder
    final LinkedDataProofGenerator generator =
        LinkedDataProofGenerator.newInstance(SignatureType.ED25519);
    final Ed25519Signature2020 proof =
        (Ed25519Signature2020)
            generator.createProof(builder.build(), URI.create(issuer + "#key-1"), privateKey);

    // Adding Proof to VC
    builder.proof(proof);

    return builder.build();
  }

  /**
   * Create vc with jws proof verifiable credential.
   *
   * @param credential the credential
   * @param privateKey the private key
   * @param issuer the issuer
   * @return the verifiable credential
   * @throws UnsupportedSignatureTypeException the unsupported signature type exception
   * @throws SsiException the ssi exception
   * @throws InvalidePrivateKeyFormat the invalide private key format
   */
  public static VerifiableCredential createVCWithJWSProof(
      VerifiableCredential credential, IPrivateKey privateKey, Did issuer)
      throws UnsupportedSignatureTypeException, InvalidPrivateKeyFormatException,
          SignatureGenerateFailedException, TransformJsonLdException {

    // VC Builder
    final VerifiableCredentialBuilder builder =
        new VerifiableCredentialBuilder()
            .context(credential.getContext())
            .id(credential.getId())
            .issuer(issuer.toUri())
            .issuanceDate(Instant.now())
            .credentialSubject(credential.getCredentialSubject())
            .expirationDate(credential.getExpirationDate())
            .type(credential.getTypes());

    // JWS Proof Builder
    LinkedDataProofGenerator generator = LinkedDataProofGenerator.newInstance(SignatureType.JWS);

    JWSSignature2020 proof = null;
    proof =
        (JWSSignature2020)
            generator.createProof(builder.build(), URI.create(issuer + "#key-1"), privateKey);

    // Adding Proof to VC
    builder.proof(proof);

    return builder.build();
  }
}
