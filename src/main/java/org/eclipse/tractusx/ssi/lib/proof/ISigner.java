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

import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPrivateKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureGenerateFailedException;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

/** The interface Signer. */
public interface ISigner {
  /**
   * Sign byte [ ].
   *
   * @param hashedLinkedData the hashed linked data
   * @param privateKey the private key
   * @return the byte [ ]
   * @throws SsiException the ssi exception
   * @throws InvalidePrivateKeyFormat the invalide private key format
   */
  public byte[] sign(HashedLinkedData hashedLinkedData, IPrivateKey privateKey)
      throws InvalidPrivateKeyFormatException, SignatureGenerateFailedException;
}
