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

package org.eclipse.tractusx.ssi.lib.util.identity;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolver;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;

public class TestDidResolver implements DidResolver {
  private final Map<Did, DidDocument> documents = new HashMap<>();

  @Override
  public boolean isResolvable(Did did) {
    return TestDidFactory.DID_METHOD.equals(did.getMethod());
  }

  @Override
  public DidDocument resolve(Did did) {
    // We don't need fragment when we are searching for DID document ID.
    Did didWithoutFragment = did.excludeFragment();
    if (!documents.containsKey(didWithoutFragment))
      throw new RuntimeException(
          String.format(
              "Did not found: %s. Got [%s]",
              did.toString(),
              documents.values().stream()
                  .map(DidDocument::toString)
                  .collect(Collectors.joining(", "))));

    return documents.get(didWithoutFragment);
  }

  public void register(TestIdentity testIdentity) {
    documents.put(testIdentity.getDid(), testIdentity.getDidDocument());
  }
}
