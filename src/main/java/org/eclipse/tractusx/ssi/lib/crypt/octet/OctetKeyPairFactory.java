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

package org.eclipse.tractusx.ssi.lib.crypt.octet;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;

/** The type Octet key pair factory. */
public class OctetKeyPairFactory {

  /**
   * Create {@link OctetKeyPair} from {@link IPrivateKey}
   *
   * @param privateKey the private key
   * @return the octet key pair
   */
  public OctetKeyPair fromPrivateKey(IPrivateKey privateKey) {
    return new OctetKeyPair.Builder(Curve.Ed25519, new Base64URL(""))
        .d(Base64URL.encode(privateKey.asByte()))
        .build();
  }

  /**
   * Create {@link OctetKeyPair} from {@link IPublicKey} and {@link IPrivateKey}
   *
   * @param publicKey the public key
   * @param privateKey the private key
   * @return the octet key pair
   */
  public OctetKeyPair fromKeyPair(IPublicKey publicKey, IPrivateKey privateKey) {
    return new OctetKeyPair.Builder(Curve.Ed25519, Base64URL.encode(publicKey.asByte()))
        .d(Base64URL.encode(privateKey.asByte()))
        .build();
  }

  /**
   * Create {@link OctetKeyPair} from {@link IPublicKey}, {@link IPrivateKey} and id
   *
   * @param keyID the key id
   * @param publicKey the public key
   * @param privateKey the private key
   * @return the octet key pair
   */
  public OctetKeyPair fromKeyPairWithKeyID(
      String keyID, IPublicKey publicKey, IPrivateKey privateKey) {
    return new OctetKeyPair.Builder(Curve.Ed25519, Base64URL.encode(publicKey.asByte()))
        .d(Base64URL.encode(privateKey.asByte()))
        .keyID(keyID)
        .build();
  }
}
