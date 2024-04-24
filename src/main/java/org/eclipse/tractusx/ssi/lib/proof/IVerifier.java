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

import org.eclipse.tractusx.ssi.lib.exception.did.DidParseException;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPublicKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.proof.NoVerificationKeyFoundException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureParseException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureVerificationFailedException;
import org.eclipse.tractusx.ssi.lib.exception.proof.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

/** The interface Verifier. */
public interface IVerifier {

  /**
   * {@link VerifiableCredential} verification method, This method depends on Issuer in VC data
   * model to get the public key of issuer.
   *
   * @param hashedLinkedData the hashed linked data
   * @param verifiable the verifiable
   * @return the boolean
   * @throws SignatureParseException the signature parse exception
   * @throws DidParseException the did parse exception
   * @throws InvalidPublicKeyFormatException the invalid public key format exception
   * @throws SignatureVerificationFailedException the signature verification failed exception
   * @throws UnsupportedSignatureTypeException the unsupported signature type exception
   * @throws NoVerificationKeyFoundException the no verification key found exception
   */
  boolean verify(HashedLinkedData hashedLinkedData, Verifiable verifiable)
      throws SignatureParseException,
          DidParseException,
          InvalidPublicKeyFormatException,
          SignatureVerificationFailedException,
          UnsupportedSignatureTypeException,
          NoVerificationKeyFoundException;
}
