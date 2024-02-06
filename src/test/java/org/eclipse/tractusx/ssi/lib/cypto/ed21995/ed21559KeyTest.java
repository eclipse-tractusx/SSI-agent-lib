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

package org.eclipse.tractusx.ssi.lib.cypto.ed21995;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.IKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559Generator;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PublicKey;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPrivateKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPublicKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.key.KeyGenerationException;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;
import org.junit.jupiter.api.Test;

/** The type Ed 21559 key test. */
public class ed21559KeyTest {

  /**
   * Test ed 21559 key generation.
   *
   * @throws KeyGenerationException the key generation exception
   */
  @Test
  public void testED21559KeyGeneration() throws KeyGenerationException {
    IKeyGenerator keyGenerator = new x21559Generator();
    KeyPair keyPair = keyGenerator.generateKey();
    assertNotNull(keyPair.getPrivateKey());
    assertNotNull(keyPair.getPublicKey());
  }

  /**
   * Test ed 21559 key serliztion.
   *
   * @throws KeyGenerationException the key generation exception
   * @throws IOException the io exception
   */
  @Test
  @SneakyThrows
  public void testED21559KeySerliztion() throws KeyGenerationException, IOException {
    IKeyGenerator keyGenerator = new x21559Generator();
    KeyPair keyPair = keyGenerator.generateKey();

    assertNotNull(keyPair.getPrivateKey().asStringForStoring());

    assertNotNull(keyPair.getPrivateKey().asStringForExchange(EncodeType.Base64));

    assertNotNull(keyPair.getPublicKey().asStringForStoring());

    assertNotNull(keyPair.getPublicKey().asStringForExchange(EncodeType.Base64));
  }

  /**
   * Test ed 21559 key deserliztion.
   *
   * @throws KeyGenerationException the key generation exception
   * @throws IOException the io exception
   * @throws InvalidePrivateKeyFormat the invalide private key format
   * @throws InvalidePublicKeyFormat the invalide public key format
   */
  @Test
  @SneakyThrows
  public void testED21559KeyDeserliztion()
      throws KeyGenerationException, IOException, InvalidPrivateKeyFormatException,
          InvalidPublicKeyFormatException {
    IKeyGenerator keyGenerator = new x21559Generator();
    KeyPair keyPair = keyGenerator.generateKey();

    var originalPrivateKey = keyPair.getPrivateKey().asByte();
    var originalPublicKey = keyPair.getPublicKey().asByte();

    String privateKeyString = keyPair.getPrivateKey().asStringForStoring();
    var privateKey = new x21559PrivateKey(privateKeyString, true);

    String publicKeyString = keyPair.getPublicKey().asStringForStoring();
    var publicKey = new x21559PublicKey(publicKeyString, true);

    assertTrue(Arrays.equals(originalPrivateKey, privateKey.asByte()));

    assertTrue(Arrays.equals(originalPublicKey, publicKey.asByte()));
  }
}
