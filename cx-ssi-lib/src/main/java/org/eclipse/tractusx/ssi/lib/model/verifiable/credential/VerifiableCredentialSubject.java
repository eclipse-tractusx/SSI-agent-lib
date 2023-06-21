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

package org.eclipse.tractusx.ssi.lib.model.verifiable.credential;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

public class VerifiableCredentialSubject extends HashMap<String, Object> {

  public static final String ID = "id";

  public VerifiableCredentialSubject(Map<String, Object> json) {
    super(json);

    try {
      // validate getters
      this.getId();
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid VerifiableCredential: %s", SerializeUtil.toJson(json)), e);
    }
  }

  public URI getId() {
    Object id = this.get(ID);
    return id == null ? null : SerializeUtil.asURI(id);
  }
}
