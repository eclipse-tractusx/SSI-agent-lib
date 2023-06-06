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
import org.eclipse.tractusx.ssi.lib.model.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.Ed25519Signature2020Builder;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.transform.TransformedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataSigner;

@RequiredArgsConstructor
public class LinkedDataProofGenerator {

  public static LinkedDataProofGenerator create() {
    return new LinkedDataProofGenerator(
        new LinkedDataHasher(), new LinkedDataTransformer(), new LinkedDataSigner());
  }

  private final LinkedDataHasher hasher;
  private final LinkedDataTransformer transformer;
  private final LinkedDataSigner signer;

  public Ed25519Signature2020 createEd25519Signature2020(
      VerifiableCredential verifiableCredential, URI verificationMethodId, byte[] signingKey) {
    final TransformedLinkedData transformedData = transformer.transform(verifiableCredential);
    final HashedLinkedData hashedData = hasher.hash(transformedData);
    final byte[] signature = signer.sign(hashedData, signingKey);
    final MultibaseString multibaseString = MultibaseFactory.create(signature);

    return new Ed25519Signature2020Builder()
        .proofPurpose(Ed25519Signature2020.PROOF_PURPOSE)
        .proofValue(multibaseString.getEncoded())
        .verificationMethod(verificationMethodId)
        .created(Instant.now())
        .build();
  }
}
