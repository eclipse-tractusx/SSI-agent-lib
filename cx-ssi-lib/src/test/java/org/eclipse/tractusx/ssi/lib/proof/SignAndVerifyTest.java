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

package org.eclipse.tractusx.ssi.lib.proof;

import java.io.IOException;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataSigner;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataVerifier;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SignAndVerifyTest {

  @Test
  public void testSignAndVerify() throws IOException {
    final TestDidDocumentResolver didDocumentResolver = new TestDidDocumentResolver();

    var testIdentity = TestIdentityFactory.newBPNIdentityWithED25519Keys(true);

    didDocumentResolver.register(testIdentity);

    var data = "Hello World".getBytes();

    var signer = new LinkedDataSigner();
    var verifier = new LinkedDataVerifier(didDocumentResolver.withRegistry());

    var signature = signer.sign(new HashedLinkedData(data), testIdentity.getPrivateKey());
    var isSigned =
        verifier.verify(new HashedLinkedData(data), signature, testIdentity.getPublicKey());

    Assertions.assertTrue(isSigned);
  }
}
