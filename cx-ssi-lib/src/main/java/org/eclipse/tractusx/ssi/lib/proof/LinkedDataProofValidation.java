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

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.transform.TransformedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataVerifier;

@RequiredArgsConstructor
public class LinkedDataProofValidation {

  public static LinkedDataProofValidation newInstance(
      DidDocumentResolverRegistry didDocumentResolverRegistry) {
    return new LinkedDataProofValidation(
        new LinkedDataHasher(),
        new LinkedDataTransformer(),
        new LinkedDataVerifier(didDocumentResolverRegistry));
  }

  private final LinkedDataHasher hasher;
  private final LinkedDataTransformer transformer;
  private final LinkedDataVerifier verifier;

  @SneakyThrows
  public boolean checkProof(VerifiableCredential verifiableCredential) {
    final TransformedLinkedData transformedData = transformer.transform(verifiableCredential);
    final HashedLinkedData hashedData = hasher.hash(transformedData);
    return verifier.verify(hashedData, verifiableCredential);
  }
}
