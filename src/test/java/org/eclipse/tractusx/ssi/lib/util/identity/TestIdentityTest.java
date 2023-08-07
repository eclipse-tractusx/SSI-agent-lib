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

import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.junit.jupiter.api.Test;

public class TestIdentityTest {

  @Test
  @SneakyThrows
  public void testPublicPrivateKey() {

    byte[] message = "Json String".getBytes(StandardCharsets.UTF_8);

    // Load public key
    var identity = TestIdentityFactory.newIdentityWithED25519Keys(false);

    // Sign
    AsymmetricKeyParameter privateKeyParameters =
        new Ed25519PrivateKeyParameters(identity.getPrivateKey(), 0);
    Signer signer = new Ed25519Signer();
    signer.init(true, privateKeyParameters);
    signer.update(message, 0, message.length);
    byte[] signature = signer.generateSignature();

    // Verify
    AsymmetricKeyParameter publicKeyParameters =
        new Ed25519PublicKeyParameters(identity.getPublicKey(), 0);
    Signer verifier = new Ed25519Signer();
    verifier.init(false, publicKeyParameters);
    verifier.update(message, 0, message.length);
    boolean verified = verifier.verifySignature(signature);

    System.out.println("Verification: " + verified); // Verification: true
  }
}
