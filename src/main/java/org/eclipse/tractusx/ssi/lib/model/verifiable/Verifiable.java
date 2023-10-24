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

package org.eclipse.tractusx.ssi.lib.model.verifiable;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

public abstract class Verifiable extends JsonLdObject {

  public static final String ID = "id";
  public static final String TYPE = "type";
  public static final String PROOF = "proof";

  private VerifiableType verifableType;

  public enum VerifiableType {
    VC,
    VP
  }

  public Verifiable(Map<String, Object> json, VerifiableType type) {
    super(json);
    Objects.requireNonNull(this.getId());
    Objects.requireNonNull(this.getTypes());
    Objects.requireNonNull(type, "Verifable Type should not be null");
    this.verifableType = type;
    this.checkId();
  }

  @NonNull
  public URI getId() {
    return SerializeUtil.asURI(this.get(ID));
  }

  @NonNull
  public List<String> getTypes() {
    return (List<String>) this.get(TYPE);
  }

  public Proof getProof() {

    final Object subject = this.get(PROOF);

    if (subject == null) {
      return null;
    }

    return new Proof((Map<String, Object>) subject);
  }

  public VerifiableType getType() {
    return verifableType;
  }

  /**
   * There exists an error that prevents quads from being created correctly. as this interferes with
   * the credential signature, this is a security risk see
   * https://github.com/eclipse-tractusx/SSI-agent-lib/issues/4 as workaround we ensure that the
   * credential ID starts with one or more letters followed by a colon
   */
  private void checkId() {
    final String regex = "^[a-zA-Z]+:.*$";
    if (!this.getId().toString().matches(regex)) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid VerifiableCredential. Credential ID must start with one or more letters followed by a colon. This is a temporary mitigation for the following security risk: %s",
              "https://github.com/eclipse-tractusx/SSI-agent-lib/issues/4"));
    }
  }
}
