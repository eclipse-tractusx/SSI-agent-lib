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

import java.util.Arrays;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;

/**
 * Implements a {@code DidResolver} that can chain multiple other {@code DidResolver}
 * implementations. The resolvers are executed in the same order as there were specified in the
 * constructor. The execution stops when a resolver returns a DID document, all subsequent resolvers
 * are skipped. Likewise, a resolver is skipped if it indicates that it cannot resolve the provided
 * DID or fails to return a DID document.
 */
public class CompositeDidResolver implements DidResolver {
  DidResolver[] didResolvers;

  public CompositeDidResolver(DidResolver... didResolvers) {
    this.didResolvers = didResolvers;
  }

  @Override
  public DidDocument resolve(Did did) throws DidResolverException {
    for (DidResolver didResolver : didResolvers) {
      if (didResolver.isResolvable(did)) {
        try {
          DidDocument result = didResolver.resolve(did);
          if (result != null) {
            return result;
          }
        } catch (DidResolverException dre) {
          throw dre;
        } catch (Throwable th) {
          // catch any other exception and re-throw wrapped as DidResolverException
          throw new DidResolverException(
              String.format("Unrecognized exception: %s", th.getClass().getName()), th);
        }
      }
    }
    return null;
  }

  @Override
  public boolean isResolvable(Did did) {
    return Arrays.stream(didResolvers).anyMatch(resolver -> resolver.isResolvable(did));
  }

  /**
   * Constructs a new {@code CompositeDidResolver} by appending the {@code toBeAppended} resolver to
   * the {@target} resolver.
   *
   * @param target the {@code DidResolver} to append the other to
   * @param toBeAppended the {@code DidResolver} to be appended
   * @return a {@code CompositeDidResolver} instance
   */
  public static CompositeDidResolver append(DidResolver target, DidResolver toBeAppended) {
    return new CompositeDidResolver(target, toBeAppended);
  }
}
