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

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolver;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable.VerifiableType;
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

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class LinkedDataProofValidation {

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
   * To verifiy {@link VerifiableCredential} or {@link VerifiablePresentation} In this method we are
   * depending on Verification Method to resolve the DID Document and fetching the required Public
   * Key
   */
  @SneakyThrows
  public boolean verify(Verifiable verifiable) {
    if (verifiable.getProof() == null) {
      throw new UnsupportedSignatureTypeException("Proof can't be empty");
    }
    boolean isVerified = false;
    IVerifier verifier = null;

    var type = verifiable.getProof().getType();

    if (type != null && !type.isBlank()) {
      if (type.equals(SignatureType.ED21559.type))
        verifier = new Ed25519ProofVerifier(this.didResolver);
      else if (type.equals(SignatureType.JWS.type))
        verifier = new JWSProofVerifier(this.didResolver);
      else
        throw new UnsupportedSignatureTypeException(
            String.format("%s is not supported type", type));
    } else {
      throw new UnsupportedSignatureTypeException("Proof type can't be empty");
    }

    final TransformedLinkedData transformedData = transformer.transform(verifiable);
    final HashedLinkedData hashedData = hasher.hash(transformedData);

    isVerified =
        jsonLdValidator.validate(verifiable)
            && verifier.verify(hashedData, verifiable)
            && validateVerificationMethodOfVC(verifiable);

    return isVerified;
  }

  /**
   * This method is to validate the Verification Method of VC
   *
   * @param verifiable
   * @return
   * @throws UnsupportedSignatureTypeException
   */
  @SneakyThrows
  private Boolean validateVerificationMethodOfVC(Verifiable verifiable) {
    // Verifiable Presentation doesn't have an Issuer
    if (verifiable.getType() == VerifiableType.VP) {
      return true;
    }
    final VerifiableCredential vc = new VerifiableCredential(verifiable);
    final String issuer = vc.getIssuer().toString();
    final String verficationMethod = getVerificationMethod(verifiable);
    final String[] splitVerificationMethod = verficationMethod.split("#");
    if (splitVerificationMethod[0].equals(issuer)) {
      return true;
    }
    return false;
  }

  /**
   * This method is to get the Verification Method of VC
   *
   * @param verifiable
   * @return
   * @throws UnsupportedSignatureTypeException
   */
  @SneakyThrows
  private String getVerificationMethod(Verifiable verifiable) {
    try {
      return (String) verifiable.getProof().get("verificationMethod");
    } catch (Exception e) {
      throw new UnsupportedSignatureTypeException("Signature type is not supported");
    }
  }
}
