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

import java.util.Map;
import java.util.Objects;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

@ToString
public class Ed25519VerificationMethod extends VerificationMethod {
  public static final String DEFAULT_TYPE = "Ed25519VerificationKey2020";

  public static final String PUBLIC_KEY_BASE_58 = "publicKeyMultibase";

  public static boolean isInstance(Map<String, Object> json) {
    return DEFAULT_TYPE.equals(json.get(TYPE));
  }

  public Ed25519VerificationMethod(Map<String, Object> json) {
    super(json);

    if (!DEFAULT_TYPE.equals(this.getType())) {
      throw new IllegalArgumentException(
          String.format("Invalid type %s. Expected %s", this.getType(), DEFAULT_TYPE));
    }

    try {
      // validate getters
      Objects.requireNonNull(this.getPublicKeyBase58(), "publicKeyBase58 is null");
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid Ed25519VerificationKey2020: %s", SerializeUtil.toJson(json)), e);
    }
  }

  public MultibaseString getPublicKeyBase58() {
    return MultibaseFactory.create((String) this.get(PUBLIC_KEY_BASE_58));
  }
}
