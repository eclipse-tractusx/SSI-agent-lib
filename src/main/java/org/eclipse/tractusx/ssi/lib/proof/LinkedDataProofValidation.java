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

package org.eclipse.tractusx.ssi.lib.proof;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolver;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.transform.TransformedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofVerifier;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofVerifier;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdValidator;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdValidatorImpl;

/** The type Linked data proof validation. */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LinkedDataProofValidation {

  /**
   * New instance linked data proof validation.
   *
   * @param didResolver the did resolver
   * @return the linked data proof validation
   */
  public static LinkedDataProofValidation newInstance(DidResolver didResolver) {

    if (didResolver == null) {
      throw new NullPointerException("Document Resolver shouldn't be null");
    }

    return new LinkedDataProofValidation(
        new LinkedDataHasher(),
        new LinkedDataTransformer(),
        didResolver,
        new JsonLdValidatorImpl());
  }

  private final LinkedDataHasher hasher;
  private final LinkedDataTransformer transformer;
  private final DidResolver didResolver;
  private final JsonLdValidator jsonLdValidator;

  /**
   * To verify {@link VerifiableCredential} or {@link VerifiablePresentation}. In this method we are
   * depending on Verification Method to resolve the DID Document and fetching the required Public
   * Key
   *
   * @param verifiable the verifiable
   * @return the boolean
   */
  @SneakyThrows
  public boolean verify(Verifiable verifiable) {

    var type = verifiable.getProof().getType();
    IVerifier verifier = null;

    if (type != null && !type.isBlank()) {
      if (type.equals(SignatureType.ED21559.toString())) {
        verifier = new Ed25519ProofVerifier(this.didResolver);
      } else if (type.equals(SignatureType.JWS.toString())) {
        verifier = new JWSProofVerifier(this.didResolver);
      } else {
        throw new UnsupportedSignatureTypeException(
            String.format("%s is not supported type", type));
      }
    } else {
      throw new UnsupportedSignatureTypeException("Proof type can't be empty");
    }

    final TransformedLinkedData transformedData = transformer.transform(verifiable);
    final HashedLinkedData hashedData = hasher.hash(transformedData);

    return jsonLdValidator.validate(verifiable) && verifier.verify(hashedData, verifiable);
  }
}
