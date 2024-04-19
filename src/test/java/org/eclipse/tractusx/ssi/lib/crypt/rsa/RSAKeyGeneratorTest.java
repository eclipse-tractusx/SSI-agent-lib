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

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.exception.key.KeyGenerationException;
import org.junit.jupiter.api.Test;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
class RSAKeyGeneratorTest {
  @Test
  void shouldGenerateKeyForGivenCurve() {
    RSAKeyGenerator generator = new RSAKeyGenerator(2048);
    KeyPair keyPair = assertDoesNotThrow(generator::generateKey);
    assertTrue(keyPair.getPublicKey() instanceof RSAPublicKeyWrapper);
    assertTrue(keyPair.getPrivateKey() instanceof RSAPrivateKeyWrapper);
  }

  @Test
  void shouldThrow() {
    RSAKeyGenerator generator = new RSAKeyGenerator(-5);
    assertThrows(KeyGenerationException.class, generator::generateKey);
  }
}
