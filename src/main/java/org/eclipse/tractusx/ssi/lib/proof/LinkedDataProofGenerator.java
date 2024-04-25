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
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.exception.json.TransformJsonLdException;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPrivateKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureGenerateFailedException;
import org.eclipse.tractusx.ssi.lib.exception.proof.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.ProofPurpose;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.ed25519.Ed25519ProofBuilder;
import org.eclipse.tractusx.ssi.lib.model.proof.ed25519.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSProofBuilder;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable.VerifiableType;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.transform.TransformedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofSigner;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofSigner;

/** The type Linked data proof generator. */
@RequiredArgsConstructor
public class LinkedDataProofGenerator {

  /**
   * New instance linked data proof generator.
   *
   * @param type the type
   * @return the linked data proof generator
   * @throws UnsupportedSignatureTypeException the unsupported signature type exception
   */
  public static LinkedDataProofGenerator newInstance(SignatureType type)
      throws UnsupportedSignatureTypeException {
    if (type == SignatureType.ED25519) {
      return new LinkedDataProofGenerator(
          type, new LinkedDataHasher(), new LinkedDataTransformer(), new Ed25519ProofSigner());
    } else {
      return new LinkedDataProofGenerator(
          type, new LinkedDataHasher(), new LinkedDataTransformer(), new JWSProofSigner(type));
    }
  }

  private final SignatureType type;
  private final LinkedDataHasher hasher;
  private final LinkedDataTransformer transformer;
  private final ISigner signer;

  /**
   * Create proof.
   *
   * @param verifiable the verifiable
   * @param verificationMethodId the verification method id
   * @param privateKey the private key
   * @return the proof
   * @throws SignatureGenerateFailedException the signature generate failed exception
   * @throws TransformJsonLdException the transform json ld exception
   * @throws InvalidPrivateKeyFormatException the invalid private key format exception
   */
  public Proof createProof(Verifiable verifiable, URI verificationMethodId, IPrivateKey privateKey)
      throws SignatureGenerateFailedException,
          TransformJsonLdException,
          InvalidPrivateKeyFormatException {
    return createProof(verifiable, verificationMethodId, privateKey, ProofPurpose.ASSERTION_METHOD);
  }

  /**
   * Create proof.
   *
   * @param verifiable the document
   * @param verificationMethodId the verification method id
   * @param privateKey the private key
   * @param proofPurpose the proof purpose
   * @return the proof
   * @throws InvalidPrivateKeyFormatException the invalid private key format
   * @throws SignatureGenerateFailedException the signature generate failed exception
   * @throws TransformJsonLdException the transform json ld exception
   */
  public Proof createProof(
      Verifiable verifiable,
      URI verificationMethodId,
      IPrivateKey privateKey,
      ProofPurpose proofPurpose)
      throws InvalidPrivateKeyFormatException,
          SignatureGenerateFailedException,
          TransformJsonLdException {

    Proof proof = null;
    if (type == SignatureType.ED25519) {
      proof =
          new Ed25519ProofBuilder()
              .proofPurpose(Ed25519Signature2020.ASSERTION_METHOD)
              .verificationMethod(verificationMethodId)
              .created(Instant.now())
              .proofValue(proofPurpose.purpose)
              .buildProofConfiguration();
    } else {
      proof =
          new JWSProofBuilder()
              .proofPurpose(JWSSignature2020.ASSERTION_METHOD)
              .verificationMethod(verificationMethodId)
              .proofPurpose(proofPurpose.purpose)
              .created(Instant.now())
              .buildProofConfiguration();
    }

    // Adding proof configuration to document
    verifiable.put(Verifiable.PROOF, proof);
    final TransformedLinkedData transformedData;

    // if it's VP then we need to remove the Signature from VCs
    if (verifiable.getType() == VerifiableType.VP) {
      // We need to make a deep copy to keep the original Verifiable as it is for
      // Verification step
      var verifiableWithoutProofSignature = verifiable.deepClone().removeProofSignature();
      transformedData = transformer.transform(verifiableWithoutProofSignature);
    } else {
      transformedData = transformer.transform(verifiable);
    }

    final HashedLinkedData hashedData = hasher.hash(transformedData);

    byte[] signature;
    signature = signer.sign(new HashedLinkedData(hashedData.getValue()), privateKey);

    if (type == SignatureType.ED25519) {

      final MultibaseString multibaseString = MultibaseFactory.create(signature);
      proof.put(Ed25519Signature2020.PROOF_VALUE, multibaseString);
    } else {
      proof.put(JWSSignature2020.JWS, new String(signature, StandardCharsets.UTF_8));
    }

    return proof;
  }
}
