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
import java.time.Instant;
import lombok.RequiredArgsConstructor;

import org.eclipse.tractusx.ssi.lib.base.ISigner;
import org.eclipse.tractusx.ssi.lib.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.ed21559.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.proof.ed21559.Ed25519Signature2020Builder;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020Builder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.transform.TransformedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.ED21559ProofSigner;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofSigner;

import com.nimbusds.jose.JOSEException;

@RequiredArgsConstructor
public class LinkedDataProofGenerator {

  public static LinkedDataProofGenerator newInstance(SignatureType type) throws UnsupportedSignatureTypeException {
    switch (type) {
      case ED21559:
        return new LinkedDataProofGenerator(
            new LinkedDataHasher(), new LinkedDataTransformer(), new ED21559ProofSigner(),
            type);
      case JWS:
        return new LinkedDataProofGenerator(
            new LinkedDataHasher(), new LinkedDataTransformer(), new JWSProofSigner(), type);
      default:
        throw new UnsupportedSignatureTypeException("Currently we only support JWS and ED25519 signature!");
    }
  }

  private final LinkedDataHasher hasher;
  private final LinkedDataTransformer transformer;
  private final ISigner signer;
  private final SignatureType type;

  public Proof createProof(
      VerifiableCredential verifiableCredential, URI verificationMethodId, byte[] signingKey) {

    final TransformedLinkedData transformedData = transformer.transform(verifiableCredential);
    final HashedLinkedData hashedData = hasher.hash(transformedData);
    byte[] signature;
    
    try {
      signature = signer.sign(hashedData, signingKey);
    } catch (JOSEException e) {
      throw new SsiException(e.getMessage());
    }


    final MultibaseString multibaseString = MultibaseFactory.create(signature);

    switch (type) {
      case ED21559:
        return new Ed25519Signature2020Builder()
            .proofPurpose(Ed25519Signature2020.PROOF_PURPOSE)
            .proofValue(multibaseString.getEncoded())
            .verificationMethod(verificationMethodId)
            .created(Instant.now())
            .build();
      case JWS:
        return new JWSSignature2020Builder()
            .proofPurpose(JWSSignature2020.PROOF_PURPOSE)
            .proofValue(multibaseString.getEncoded())
            .verificationMethod(verificationMethodId)
            .created(Instant.now())
            .build();
      default:
        throw new UnsupportedOperationException("Currently we only support JWS and ED25519 signature!");
    }

  }
}
