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

package org.eclipse.tractusx.ssi.lib.proof.types.ed25519;

import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.eclipse.tractusx.ssi.lib.proof.ISigner;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

public class ED21559ProofSigner implements ISigner {

  @Override
  public byte[] sign(HashedLinkedData hashedLinkedData, byte[] signingKey) {
    final byte[] message = hashedLinkedData.getValue();

    Ed25519PrivateKeyParameters secretKeyParameters =
        new Ed25519PrivateKeyParameters(signingKey, 0);

    final Ed25519Signer signer = new Ed25519Signer();
    signer.init(true, secretKeyParameters);
    signer.update(message, 0, message.length);

    return signer.generateSignature();
  }
}
