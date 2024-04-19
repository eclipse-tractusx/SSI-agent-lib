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

package org.eclipse.tractusx.ssi.lib.crypt.jwk;

import com.nimbusds.jose.jwk.OctetKeyPair;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.crypt.octet.OctetKeyPairFactory;

/** The type JsonWebKey. */
@Deprecated(forRemoval = true, since = "nimbus provides JWK implementations")
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonWebKey {
  private final OctetKeyPair keyPair;

  /**
   * Instantiates a new JsonWebKey.
   *
   * @param id the id
   * @param publicKey the public key
   * @param privateKey the private key
   */
  public JsonWebKey(String id, IPublicKey publicKey, IPrivateKey privateKey) {
    OctetKeyPairFactory keyPairFactory = new OctetKeyPairFactory();
    OctetKeyPair keyOctetKeyPair = keyPairFactory.fromKeyPairWithKeyID(id, publicKey, privateKey);
    this.keyPair = keyOctetKeyPair;
  }

  /**
   * Gets the curve name of the key pair.
   *
   * @return the curve
   */
  public String getCurv() {
    return keyPair.getCurve().getName();
  }

  /**
   * Gets key id.
   *
   * @return the key id
   */
  public String getKeyID() {
    return keyPair.getKeyID();
  }

  /**
   * Gets key type.
   *
   * @return the key type
   */
  public String getKeyType() {
    return this.keyPair.getKeyType().getValue();
  }

  /**
   * Gets the x member contains the x coordinate for the elliptic curve point.
   *
   * @return the x
   */
  public String getX() {
    return this.keyPair.getX().toString();
  }
}
