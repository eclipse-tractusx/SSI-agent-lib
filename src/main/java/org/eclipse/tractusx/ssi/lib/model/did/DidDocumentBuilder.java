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

package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;

/** The type Did document builder. */
@NoArgsConstructor
public class DidDocumentBuilder {
  private URI id;
  private final List<VerificationMethod> verificationMethods = new ArrayList<>();

  /**
   * Id did document builder.
   *
   * @param id the document id
   * @return the did document builder
   */
  public DidDocumentBuilder id(URI id) {
    this.id = id;
    return this;
  }

  /**
   * Verification methods did document builder.
   *
   * @param verificationMethods the verification methods
   * @return the did document builder
   */
  public DidDocumentBuilder verificationMethods(List<VerificationMethod> verificationMethods) {
    this.verificationMethods.addAll(verificationMethods);
    return this;
  }

  /**
   * Verification method did document builder.
   *
   * @param verificationMethod the verification method
   * @return the did document builder
   */
  public DidDocumentBuilder verificationMethod(VerificationMethod verificationMethod) {
    this.verificationMethods.add(verificationMethod);
    return this;
  }

  /**
   * Build did document.
   *
   * @return the did document
   */
  public DidDocument build() {
    return new DidDocument(
        Map.of(
            DidDocument.CONTEXT,
            DidDocument.DEFAULT_CONTEXT,
            DidDocument.ID,
            id.toString(),
            DidDocument.VERIFICATION_METHOD,
            verificationMethods));
  }
}
