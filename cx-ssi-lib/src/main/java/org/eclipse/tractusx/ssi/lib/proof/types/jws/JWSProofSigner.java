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

package org.eclipse.tractusx.ssi.lib.proof.types.jws;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.jwk.OctetKeyPair;
import java.io.IOException;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.octet.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.proof.ISigner;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

public class JWSProofSigner implements ISigner {

  @Override
  public byte[] sign(HashedLinkedData hashedLinkedData, IPrivateKey privateKey)
      throws InvalidePrivateKeyFormat {

    OctetKeyPairFactory octetKeyPairFactory = new OctetKeyPairFactory();
    OctetKeyPair keyPair;
    try {
      keyPair = octetKeyPairFactory.fromPrivateKey(privateKey);
    } catch (IOException e) {
      throw new InvalidePrivateKeyFormat(e.getCause());
    }

    JWSSigner signer;
    try {
      signer = new Ed25519Signer(keyPair);
    } catch (JOSEException e) {
      throw new SsiException(e.getMessage());
    }

    // var algorithm = JWSAlgorithm.EdDSA;
    // var type = JOSEObjectType.JWT;
    var header = new JWSHeader.Builder(JWSAlgorithm.EdDSA).build();

    // new JWSHeader(algorithm, type, null, null, null, null, null, null, null, null, null, false,
    // null,
    //        null);
    Payload payload = new Payload(hashedLinkedData.getValue());
    JWSObject jwsObject = new JWSObject(header, payload);

    try {
      jwsObject.sign(signer);
    } catch (JOSEException e) {
      throw new SsiException(e.getMessage());
    }

    return jwsObject.serialize(true).getBytes();
  }
}
