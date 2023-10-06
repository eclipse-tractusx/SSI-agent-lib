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

package org.eclipse.tractusx.ssi.lib.did.resolver;

import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;

/** Defines the interface of a {@code DidResolver} implementation. */
public interface DidResolver {
  /**
   * Returns the <b>DID document</b> for the provided {@code did}.
   *
   * @param did a valid Decentralized Identifier (according to <a
   *     href="https://www.w3.org/TR/did-core/">W3C DID specification</a>)
   * @return the resolved DID document or <em>null</em> if the provided {@code did} could not be
   *     resolved
   * @throws DidResolverException if the DID is invalid or cannot be resolved to a DID document
   */
  DidDocument resolve(Did did) throws DidResolverException;

  /**
   * Indicates whether the resolver is capable of resolving the provided {@code did} to a DID
   * document.
   *
   * <p><u>Note:</u> This method is intended as a quick test to select a resolver that
   * <em>potentially</em> is able to resolve the respective DID document. However, is does not
   * actually have to execute the resolution process (since this may take some time, depending on
   * the DID method and used SSI network. Also, a resolver can decide freely, how it determines
   * whether a DID is considered to be resolvable. In most cases, this may include the DID method
   * and/or parts of the method-specific identifier.
   *
   * @param did a valid Decentralized Identifier (according to <a
   *     href="https://www.w3.org/TR/did-core/">W3C DID specification</a>)
   * @return <em>true</em> if the resolver is capable of resolving the provided {@code did} to a DID
   *     document, <em>false</em> otherwise
   */
  boolean isResolvable(Did did);
}
