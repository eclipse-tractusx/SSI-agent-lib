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

import java.util.HashMap;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;

/**
 * @deprecated replaced by {@link DidResolver}
 */
public class DidDocumentResolverRegistryImpl implements DidDocumentResolverRegistry {

  public final Map<DidMethod, DidDocumentResolver> resolvers = new HashMap<>();

  @Override
  public DidDocumentResolver get(DidMethod didMethod)
      throws DidDocumentResolverNotRegisteredException {

    if (!resolvers.containsKey(didMethod))
      throw new DidDocumentResolverNotRegisteredException(didMethod);

    return resolvers.get(didMethod);
  }

  @Override
  public void register(DidDocumentResolver resolver) {
    if (resolvers.containsKey(resolver.getSupportedMethod()))
      throw new SsiException(
          String.format(
              "Resolver for method '%s' is already registered", resolver.getSupportedMethod()));
    resolvers.put(resolver.getSupportedMethod(), resolver);
  }

  @Override
  public void unregister(DidDocumentResolver resolver) {
    if (!resolvers.containsKey(resolver.getSupportedMethod()))
      throw new SsiException(
          String.format("Resolver for method '%s' not registered", resolver.getSupportedMethod()));
    resolvers.remove(resolver.getSupportedMethod());
  }
}
