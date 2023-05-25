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

package org.eclipse.tractusx.ssi.lib.resolver;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import java.util.Arrays;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519Key;

public class OctetKeyPairFactory {

  public OctetKeyPair get(Ed25519Key signingKeyBytes) {

    var key = signingKeyBytes.getEncoded();
    var length = key.length;
    // TODO Document why last 32 bytes
    byte[] b1 = Arrays.copyOfRange(key, length - 32, length);

    return new OctetKeyPair.Builder(Curve.Ed25519, new Base64URL(""))
        .d(Base64URL.encode(b1))
        .build();
  }
}
