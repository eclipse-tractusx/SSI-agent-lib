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

import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePublicKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.NoVerificationKeyFoundExcpetion;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
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
   * @throws UnsupportedSignatureTypeException the unsupported signature type exception
   * @throws DidDocumentResolverNotRegisteredException the did document resolver not registered
   *     exception
   * @throws InvalidePublicKeyFormat the invalide public key format
   * @throws NoVerificationKeyFoundExcpetion the no verification key found excpetion
   */
  public boolean verify(HashedLinkedData hashedLinkedData, Verifiable verifiable)
      throws UnsupportedSignatureTypeException, DidDocumentResolverNotRegisteredException,
          InvalidePublicKeyFormat, NoVerificationKeyFoundExcpetion;
}
