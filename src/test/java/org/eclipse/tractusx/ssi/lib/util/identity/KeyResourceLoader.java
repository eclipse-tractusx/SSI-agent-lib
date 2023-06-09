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

package org.eclipse.tractusx.ssi.lib.util.identity;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.encoders.Hex;
import org.bouncycastle.util.io.pem.PemReader;

public class KeyResourceLoader {

  private static final String PRIVATE_KEY = "keys/ed25519/private.pem";
  private static final String PUBLIC_KEY = "keys/ed25519/public.pem";

  private KeyResourceLoader() {}

  @SneakyThrows
  public static byte @NonNull [] readPrivateKey() {
    final InputStream inputStream =
        KeyResourceLoader.class.getClassLoader().getResourceAsStream(PRIVATE_KEY);
    if (inputStream == null) {
      throw new IllegalArgumentException(PRIVATE_KEY + " not found!");
    }

    final PemReader pemReader = new PemReader(new InputStreamReader(inputStream));
    // final String base64Content = new String(pemReader.readPemObject().getContent());

    var bytes = pemReader.readPemObject().getContent();
    Ed25519PrivateKeyParameters privateKey = new Ed25519PrivateKeyParameters(bytes, 0);
    bytes = Hex.encode(privateKey.getEncoded());

    return bytes;
  }

  @SneakyThrows
  public static byte[] readPublicKey() {
    final InputStream inputStream =
        KeyResourceLoader.class.getClassLoader().getResourceAsStream(PUBLIC_KEY);
    if (inputStream == null) {
      throw new IllegalArgumentException(PUBLIC_KEY + " not found!");
    }

    String publicKeyBody =
        new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).split(" ")[1];
    byte[] encoded = Base64.getDecoder().decode(publicKeyBody);
    Ed25519PublicKeyParameters publicKey = new Ed25519PublicKeyParameters(encoded, 0);
    byte[] bytes = publicKey.getEncoded();
    if (bytes.length != 32) {
      throw new Exception("Invalid PK Length should be 32");
    }
    return bytes;
  }
}
