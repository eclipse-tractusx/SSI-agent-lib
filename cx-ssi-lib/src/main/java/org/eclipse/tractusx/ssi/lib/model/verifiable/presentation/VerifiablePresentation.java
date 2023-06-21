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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

@ToString
public class VerifiablePresentation extends JsonLdObject {
  public static final String DEFAULT_CONTEXT = "https://www.w3.org/2018/credentials/v1";

  public static final String ID = "id";
  public static final String TYPE = "type";
  public static final String VERIFIABLE_CREDENTIAL = "verifiableCredential";

  public VerifiablePresentation(Map<String, Object> json) {
    super(json);

    try {
      // validate getters
      Objects.requireNonNull(this.getId(), "id s null");
      Objects.requireNonNull(this.getTypes(), "context is null");
      Objects.requireNonNull(this.getVerifiableCredentials(), "VCs is null");
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid VerifiablePresentation: %s", SerializeUtil.toJson(json)), e);
    }
  }

  @NonNull
  public URI getId() {
    return SerializeUtil.asURI(this.get(ID));
  }

  @NonNull
  public List<String> getTypes() {
    return SerializeUtil.asStringList(this.get(TYPE));
  }

  @NonNull
  public List<VerifiableCredential> getVerifiableCredentials() {
    final List<Map<String, Object>> credentials =
        (List<Map<String, Object>>) this.get(VERIFIABLE_CREDENTIAL);
    return credentials.stream().map(VerifiableCredential::new).collect(Collectors.toList());
  }
}
