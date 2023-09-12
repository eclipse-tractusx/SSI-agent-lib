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

package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@EqualsAndHashCode
public class Did {

  @EqualsAndHashCode.Include @Setter @Getter @NonNull DidMethod method;
  @EqualsAndHashCode.Include @Setter @Getter @NonNull DidMethodIdentifier methodIdentifier;
  @EqualsAndHashCode.Include @Setter @Getter String fragment;

  public Did(DidMethod method, DidMethodIdentifier didMethodIdentifier, String fragment) {
    this.method = method;
    this.methodIdentifier = didMethodIdentifier;
    this.fragment = fragment;
  }

  public Did(DidMethod method, DidMethodIdentifier didMethodIdentifier) {
    new Did(method, didMethodIdentifier, null);
  }

  public Did excludeFragment() {
    Did newDid = new Did(method, methodIdentifier, null);
    return newDid;
  }

  public URI toUri() {
    return URI.create(toString());
  }

  @Override
  public String toString() {
    StringBuilder uri = new StringBuilder(String.format("did:%s:%s", method, methodIdentifier));
    if (fragment != null && !fragment.isBlank()) {
      uri.append(String.format("#%s", fragment));
    }
    return uri.toString();
  }
}
