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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

// spec https://www.w3.org/TR/did-core/#verification-methods
@ToString
public class VerificationMethod extends HashMap<String, Object> {
  public static final String ID = "id";
  public static final String TYPE = "type";
  public static final String CONTROLLER = "controller";

  public VerificationMethod(Map<String, Object> json) {
    super(json);

    try {
      // validate getters
      Objects.requireNonNull(this.getId(), "id is null");
      Objects.requireNonNull(this.getType(), "type is null");
      Objects.requireNonNull(this.getController(), "controller is null");
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid VerificationMethod: %s", SerializeUtil.toJson(json)), e);
    }
  }

  public URI getId() {
    return SerializeUtil.asURI(this.get(ID));
  }

  public String getType() {
    return (String) this.get(TYPE);
  }

  public URI getController() {
    return SerializeUtil.asURI(this.get(CONTROLLER));
  }
}
