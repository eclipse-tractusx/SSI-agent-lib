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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

@ToString
public class JWKVerificationMethod extends VerificationMethod {
  public static final String DEFAULT_TYPE = "JsonWebKey2020";
  public static final String PUBLIC_KEY_JWK = "publicKeyJwk";

  public JWKVerificationMethod(Map<String, Object> json) {
    super(json);

    if (!DEFAULT_TYPE.equals(this.getType())) {
      throw new IllegalArgumentException(
          String.format("Invalid type %s. Expected %s", this.getType(), DEFAULT_TYPE));
    }

    try {
      // validate getters
      Objects.requireNonNull(this.getPublicKeyJwk(), "publicKeyJwk is null");
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid JsonWebKey2020: %s", SerializeUtil.toJson(json)), e);
    }
  }

  public PublicKeyJwk getPublicKeyJwk() {
    var publicKeyJwk = (Map<String, String>) this.get(PUBLIC_KEY_JWK);

    return new PublicKeyJwk(
        publicKeyJwk.get("kty"), publicKeyJwk.get("crv"), publicKeyJwk.get("x"));
  }

  public static boolean isInstance(Map<String, Object> json) {
    return DEFAULT_TYPE.equals(json.get(TYPE));
  }

  @Data
  @AllArgsConstructor
  public static class PublicKeyJwk {
    private String kty, crv, x;
  }
}
