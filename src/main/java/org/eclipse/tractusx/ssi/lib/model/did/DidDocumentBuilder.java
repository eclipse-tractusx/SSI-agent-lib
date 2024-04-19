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

package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.model.ProofPurpose;

/** The type Did document builder. */
@NoArgsConstructor
public class DidDocumentBuilder {

  private URI id;

  private final List<VerificationMethod> verificationMethods = new ArrayList<>();

  private List<Object> authentication;

  private List<Object> assertionMethod;

  private List<Object> capabilityInvocation;

  private List<Object> capabilityDelegation;

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

  public DidDocumentBuilder authentication(List<Object> authentication) {
    checkVerificationMethodTypes(authentication);
    this.authentication = authentication;
    return this;
  }

  public DidDocumentBuilder assertionMethod(List<Object> assertionMethod) {
    checkVerificationMethodTypes(assertionMethod);
    this.assertionMethod = assertionMethod;
    return this;
  }

  public DidDocumentBuilder capabilityInvocation(List<Object> capabilityInvocation) {
    checkVerificationMethodTypes(capabilityInvocation);
    this.capabilityInvocation = capabilityInvocation;
    return this;
  }

  public DidDocumentBuilder capabilityDelegation(List<Object> capabilityDelegation) {
    checkVerificationMethodTypes(capabilityDelegation);
    this.capabilityDelegation = capabilityDelegation;
    return this;
  }

  private void checkVerificationMethodTypes(List<Object> vms) {
    Objects.requireNonNull(vms);

    if (vms.isEmpty()) {
      throw new IllegalArgumentException("verification methods must not be empty");
    }

    vms.forEach(
        vm -> {
          if (!(vm instanceof URI) && !(vm instanceof VerificationMethod)) {
            throw new IllegalArgumentException(
                "verification Method must be of type URI or VerificationMethod");
          }
        });
  }

  /**
   * Build did document.
   *
   * @return the did document
   */
  public DidDocument build() {

    Map<String, Object> requiredEntries =
        Map.of(
            JsonLdObject.CONTEXT,
            DidDocument.DEFAULT_CONTEXT,
            DidDocument.ID,
            id.toString(),
            DidDocument.VERIFICATION_METHOD,
            verificationMethods);

    HashMap<String, Object> entries = new HashMap<>(requiredEntries);
    if (CollectionUtils.isNotEmpty(assertionMethod)) {
      entries.put(ProofPurpose.ASSERTION_METHOD.purpose, assertionMethod);
    }

    if (CollectionUtils.isNotEmpty(authentication)) {
      entries.put(ProofPurpose.AUTHENTICATION.purpose, authentication);
    }

    if (CollectionUtils.isNotEmpty(capabilityDelegation)) {
      entries.put(ProofPurpose.CAPABILITY_DELEGATION.purpose, capabilityDelegation);
    }

    if (CollectionUtils.isNotEmpty(capabilityInvocation)) {
      entries.put(ProofPurpose.CAPABILITY_INVOCATION.purpose, capabilityInvocation);
    }

    return new DidDocument(entries);
  }
}
