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
 ********************************************************************************/

package org.eclipse.tractusx.ssi.lib.proof.types.jws;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.util.SignerUtil;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPrivateKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureGenerateFailedException;
import org.eclipse.tractusx.ssi.lib.proof.ISigner;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

/** The type Jws proof signer. */
public class JWSProofSigner implements ISigner {

  private final SignatureType signatureType;

  public JWSProofSigner(final SignatureType signatureType) {
    this.signatureType = signatureType;
  }

  public JWSProofSigner() {
    this.signatureType = SignatureType.JWS;
  }

  @Override
  public byte[] sign(HashedLinkedData hashedLinkedData, IPrivateKey privateKey)
      throws InvalidPrivateKeyFormatException, SignatureGenerateFailedException {

    JWSSigner signer;
    try {
      signer = SignerUtil.getSigner(signatureType, privateKey);
    } catch (JOSEException e) {
      throw new InvalidPrivateKeyFormatException(e.getMessage());
    }

    var header = new JWSHeader.Builder(new JWSAlgorithm(signatureType.algorithm)).build();
    Payload payload = new Payload(hashedLinkedData.getValue());
    JWSObject jwsObject = new JWSObject(header, payload);

    try {
      jwsObject.sign(signer);
    } catch (JOSEException e) {
      throw new SignatureGenerateFailedException(e.getMessage());
    }

    return jwsObject.serialize(true).getBytes();
  }
}
