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

package org.eclipse.tractusx.ssi.lib.model.verifiable.presentation;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

@NoArgsConstructor
public class VerifiablePresentationBuilder {
  private List<URI> context = List.of(VerifiablePresentation.DEFAULT_CONTEXT);
  private URI id;
  private List<String> types;
  private List<VerifiableCredential> verifiableCredentials;

  public VerifiablePresentationBuilder context(List<URI> context) {
    this.context = context;
    return this;
  }

  public VerifiablePresentationBuilder id(URI id) {
    this.id = id;
    return this;
  }

  public VerifiablePresentationBuilder type(List<String> types) {
    this.types = types;
    return this;
  }

  public VerifiablePresentationBuilder verifiableCredentials(
      List<VerifiableCredential> verifiableCredentials) {
    this.verifiableCredentials = verifiableCredentials;
    return this;
  }

  public VerifiablePresentation build() {
    Map<String, Object> map = new HashMap<>();
    map.put(VerifiablePresentation.CONTEXT, context);
    map.put(VerifiablePresentation.ID, id.toString());
    map.put(VerifiablePresentation.TYPE, types);
    map.put(VerifiablePresentation.VERIFIABLE_CREDENTIAL, verifiableCredentials);

    return new VerifiablePresentation(map);
  }
}
