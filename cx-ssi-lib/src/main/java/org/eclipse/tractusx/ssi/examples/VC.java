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

package org.eclipse.tractusx.ssi.examples;

import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.proof.ed21559.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofGenerator;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;

public class VC {
  public static VerifiableCredential createVCWithoutProof() {

    // VC Bulider
    final VerifiableCredentialBuilder verifiableCredentialBuilder =
        new VerifiableCredentialBuilder();

    // VC Subject
    final VerifiableCredentialSubject verifiableCredentialSubject =
        new VerifiableCredentialSubject(Map.of("test", "test"));

    // Using Builder
    final VerifiableCredential credentialWithoutProof =
        verifiableCredentialBuilder
            .id(URI.create("did:test:id"))
            .type(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .issuer(URI.create("did:test:isser"))
            .expirationDate(Instant.now().plusSeconds(3600))
            .issuanceDate(Instant.now())
            .credentialSubject(verifiableCredentialSubject)
            .build();

    return credentialWithoutProof;
  }

  public static VerifiableCredential createVCWithED21559Proof(
      VerifiableCredential credential, byte[] privateKey, Did issuer)
      throws UnsupportedSignatureTypeException {

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
        LinkedDataProofGenerator.newInstance(SignatureType.ED21559);
    final Ed25519Signature2020 proof =
        (Ed25519Signature2020)
            generator.createProof(builder.build(), URI.create(issuer + "#key-1"), privateKey);

    // Adding Proof to VC
    builder.proof(proof);

    return builder.build();
  }

  public static VerifiableCredential createVCWithJWSProof(
      VerifiableCredential credential, byte[] privateKey, Did issuer)
      throws UnsupportedSignatureTypeException {

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
    final LinkedDataProofGenerator generator =
        LinkedDataProofGenerator.newInstance(SignatureType.JWS);
    final JWSSignature2020 proof =
        (JWSSignature2020)
            generator.createProof(builder.build(), URI.create(issuer + "#key-1"), privateKey);

    // Adding Proof to VC
    builder.proof(proof);

    return builder.build();
  }
}
