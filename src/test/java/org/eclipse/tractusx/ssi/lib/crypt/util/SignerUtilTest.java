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

package org.eclipse.tractusx.ssi.lib.crypt.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import java.security.PrivateKey;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.rsa.RSAPrivateKeyWrapper;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SignerUtilTest {

  @SneakyThrows
  @Test
  void testGetSignerWithInvalidType() {
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> {
          SignerUtil.getSigner(SignatureType.ED25519, null);
        });
  }

  @SneakyThrows
  @Test
  void testGetSignerWithRSA() {
    RSAKeyGenerator keyGen = new RSAKeyGenerator(4096);
    RSAKey generate = keyGen.generate();
    PrivateKey privateKey = generate.toPrivateKey();

    RSAPrivateKeyWrapper rsaPrivateKeyWrapper = new RSAPrivateKeyWrapper(privateKey.getEncoded());
    Assertions.assertDoesNotThrow(
        () -> SignerUtil.getSigner(SignatureType.JWS_RSA, rsaPrivateKeyWrapper));

    Assertions.assertDoesNotThrow(() -> SignerUtil.getRSASigner(rsaPrivateKeyWrapper));
  }

  @Test
  void ecShouldThrow() {
    assertThrows(IllegalStateException.class, () -> SignerUtil.getECPrivateKey(new byte[12]));
  }

  @Test
  void rsaShouldThrow() {
    assertThrows(IllegalStateException.class, () -> SignerUtil.getRSAPrivateKey(new byte[12]));
  }
}
