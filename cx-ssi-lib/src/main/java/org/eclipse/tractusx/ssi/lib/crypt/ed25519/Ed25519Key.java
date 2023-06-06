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

package org.eclipse.tractusx.ssi.lib.crypt.ed25519;

import java.io.IOException;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;

public class Ed25519Key {
  byte[] encoded;

  public static Ed25519Key asPrivateKey(byte[] privateKey) throws IOException {
    Ed25519PrivateKeyParameters ed25519PrivateKeyParameters =
        new Ed25519PrivateKeyParameters(privateKey, 0);

    return new Ed25519Key(ed25519PrivateKeyParameters.getEncoded());
  }

  public static Ed25519Key asPublicKey(byte[] publicKey) throws IOException {
    Ed25519PublicKeyParameters ed25519publicKeyParameters =
        new Ed25519PublicKeyParameters(publicKey, 0);
    return new Ed25519Key(ed25519publicKeyParameters.getEncoded());
  }

  private Ed25519Key(byte[] key) {
    this.encoded = key;
  }

  public byte[] getEncoded() {
    return this.encoded;
  }
}
