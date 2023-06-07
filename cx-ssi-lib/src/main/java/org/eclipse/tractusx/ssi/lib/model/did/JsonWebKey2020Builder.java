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

import com.nimbusds.jose.jwk.OctetKeyPair;
import java.net.URI;
import java.util.Map;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class JsonWebKey2020Builder {
  private Did did;
  private OctetKeyPair octetKeyPair;

  public JsonWebKey2020Builder did(Did did) {
    this.did = did;
    return this;
  }

  public JsonWebKey2020Builder octetKeyPair(OctetKeyPair octetKeyPair) {
    this.octetKeyPair = octetKeyPair;
    return this;
  }

  public JsonWebKey2020 build() {
    return new JsonWebKey2020(
        Map.of(
            JsonWebKey2020.ID,
            URI.create(did.toUri() + "#" + octetKeyPair.getKeyID()),
            JsonWebKey2020.TYPE,
            JsonWebKey2020.DEFAULT_TYPE,
            JsonWebKey2020.CONTROLLER,
            this.did.toUri(),
            JsonWebKey2020.PUBLIC_KEY_JWK,
            Map.of(
                "kty", octetKeyPair.getKeyType().getValue(),
                "crv", octetKeyPair.getCurve().getName(),
                "x", octetKeyPair.getX().toString())));
  }
}
