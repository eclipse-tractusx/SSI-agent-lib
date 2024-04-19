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

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.util.JSONObjectUtils;
import java.net.URI;
import java.util.Map;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

/** The type Jwk verification method builder. */
@NoArgsConstructor
public class JWKVerificationMethodBuilder {

  private Did did;
  private JWK jwk;

  /**
   * Did jwk verification method builder.
   *
   * @param did the did
   * @return the jwk verification method builder
   */
  public JWKVerificationMethodBuilder did(Did did) {
    this.did = did;
    return this;
  }

  /**
   * Jwk jwk verification method builder.
   *
   * @param jwk the jwk
   * @return the jwk verification method builder
   */
  public JWKVerificationMethodBuilder jwk(JWK jwk) {
    this.jwk = jwk;
    return this;
  }

  /**
   * Build jwk verification method.
   *
   * @return the jwk verification method
   */
  @SneakyThrows
  public JWKVerificationMethod build() {
    return new JWKVerificationMethod(
        Map.of(
            VerificationMethod.ID,
            URI.create(did.toUri() + "#" + jwk.getKeyID()),
            VerificationMethod.TYPE,
            JWKVerificationMethod.DEFAULT_TYPE,
            VerificationMethod.CONTROLLER,
            this.did.toUri(),
            JWKVerificationMethod.PUBLIC_KEY_JWK,
            JSONObjectUtils.parse(jwk.toJSONString())));
  }
}
