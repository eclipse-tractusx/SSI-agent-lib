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

package org.eclipse.tractusx.ssi.lib.did.resolver;

import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;

/**
 * The interface Did document resolver registry.
 *
 * @deprecated replaced by {@link DidResolver}
 */
public interface DidDocumentResolverRegistry {
  /**
   * Get did document resolver.
   *
   * @param did the did
   * @return the did document resolver
   * @throws DidDocumentResolverNotRegisteredException the did document resolver not registered
   *     exception
   */
  DidDocumentResolver get(DidMethod did) throws DidDocumentResolverNotRegisteredException;

  /**
   * Register a new did document resolver
   *
   * @param resolver the resolver
   */
  void register(DidDocumentResolver resolver);

  /**
   * Unregister a did document resolver
   *
   * @param resolver the resolver
   */
  void unregister(DidDocumentResolver resolver);
}
