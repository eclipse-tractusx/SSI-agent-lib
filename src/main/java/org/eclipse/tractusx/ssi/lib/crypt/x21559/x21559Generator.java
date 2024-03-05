/*
 * ******************************************************************************
 * Copyright (c) 2021,2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.ssi.lib.crypt.x21559;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.generators.Ed25519KeyPairGenerator;
import org.bouncycastle.crypto.params.Ed25519KeyGenerationParameters;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.eclipse.tractusx.ssi.lib.crypt.IKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPrivateKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPublicKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.key.KeyGenerationException;

/** X21559 key generator. */
public class x21559Generator implements IKeyGenerator {

  @Override
  public KeyPair generateKey() throws KeyGenerationException {

    SecureRandom secureRandom = new SecureRandom();

    Ed25519KeyPairGenerator keyPairGenerator = new Ed25519KeyPairGenerator();
    keyPairGenerator.init(new Ed25519KeyGenerationParameters(secureRandom));
    AsymmetricCipherKeyPair keyPair = keyPairGenerator.generateKeyPair();

    Ed25519PrivateKeyParameters privateKey = (Ed25519PrivateKeyParameters) keyPair.getPrivate();
    Ed25519PublicKeyParameters publicKey = (Ed25519PublicKeyParameters) keyPair.getPublic();

    x21559PrivateKey x21559PrivateKey;
    try {
      x21559PrivateKey = new x21559PrivateKey(privateKey.getEncoded());
    } catch (InvalidPrivateKeyFormatException e) {
      throw new KeyGenerationException(e.getCause());
    }
    x21559PublicKey x21559PublicKey;
    try {
      x21559PublicKey = new x21559PublicKey(publicKey.getEncoded());
    } catch (InvalidPublicKeyFormatException e) {
      throw new KeyGenerationException(e.getCause());
    }
    return new KeyPair(x21559PublicKey, x21559PrivateKey);
  }
}
