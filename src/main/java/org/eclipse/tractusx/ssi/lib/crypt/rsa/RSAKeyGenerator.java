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

package org.eclipse.tractusx.ssi.lib.crypt.rsa;

import java.security.InvalidParameterException;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.lib.crypt.IKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.exception.key.KeyGenerationException;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
public class RSAKeyGenerator implements IKeyGenerator {

  private final int keySize;

  public RSAKeyGenerator(final int keySize) {
    this.keySize = keySize;
  }

  public KeyPair generateKey() throws KeyGenerationException {
    KeyPairGenerator kpg = null;
    try {
      kpg = KeyPairGenerator.getInstance("RSA");
      kpg.initialize(keySize);
    } catch (NoSuchAlgorithmException | InvalidParameterException e) {
      throw new KeyGenerationException(e);
    }

    java.security.KeyPair keyPair = kpg.generateKeyPair();

    return new KeyPair(
        new RSAPublicKeyWrapper(keyPair.getPublic().getEncoded()),
        new RSAPrivateKeyWrapper(keyPair.getPrivate().getEncoded()));
  }
}
