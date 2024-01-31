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

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.ProofPurpose;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.ed21559.Ed25519Signature2020Builder;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020Builder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
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
    if (type == SignatureType.ED21559) {
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
   * @param document the document
   * @param verificationMethodId the verification method id
   * @param privateKey the private key
   * @return the proof
   * @throws SsiException the ssi exception
   * @throws InvalidePrivateKeyFormat the invalide private key format
   */
  public Proof createProof(Verifiable document, URI verificationMethodId, IPrivateKey privateKey)
      throws SsiException, InvalidePrivateKeyFormat {

    return createProof(document, verificationMethodId, privateKey, ProofPurpose.ASSERTION_METHOD);
  }

  public Proof createProof(
      Verifiable document,
      URI verificationMethodId,
      IPrivateKey privateKey,
      ProofPurpose proofPurpose)
      throws SsiException, InvalidePrivateKeyFormat {

    final TransformedLinkedData transformedData = transformer.transform(document);
    final HashedLinkedData hashedData = hasher.hash(transformedData);
    byte[] signature;
    signature = signer.sign(new HashedLinkedData(hashedData.getValue()), privateKey);

    if (type == SignatureType.ED21559) {

      final MultibaseString multibaseString = MultibaseFactory.create(signature);
      return new Ed25519Signature2020Builder()
          .proofPurpose(proofPurpose.purpose)
          .proofValue(multibaseString.getEncoded())
          .verificationMethod(verificationMethodId)
          .created(Instant.now())
          .build();
    } else {

      return new JWSSignature2020Builder()
          .proofPurpose(proofPurpose.purpose)
          .proofValue(new String(signature, StandardCharsets.UTF_8))
          .verificationMethod(verificationMethodId)
          .created(Instant.now())
          .build();
    }
  }
}
