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
 * *******************************************************************************
 */

package org.eclipse.tractusx.ssi.lib.crypt.x25519;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.spec.PKCS8EncodedKeySpec;
import lombok.NonNull;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPrivateKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.key.KeyTransformationException;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;

/** The type X25519 private key. */
public class X25519PrivateKey implements IPrivateKey {

  private static final int KEY_LENGTH = 32;
  private final @NonNull byte[] key;

  /**
   * Instantiates a new X 25519 private key.
   *
   * @param privateKey the private key
   * @throws InvalidePrivateKeyFormat the invalide private key format
   */
  public X25519PrivateKey(byte[] privateKey) throws InvalidPrivateKeyFormatException {
    if (this.getKeyLength() != privateKey.length) {
      throw new InvalidPrivateKeyFormatException(getKeyLength(), privateKey.length);
    }
    this.key = privateKey;
  }

  /**
   * Instantiates a new X 25519 private key.
   *
   * @param privateKey the private key
   * @param pemFormat the pem format
   * @throws InvalidePrivateKeyFormat the invalide private key format
   * @throws IOException the io exception
   */
  public X25519PrivateKey(String privateKey, boolean pemFormat)
      throws InvalidPrivateKeyFormatException {
    if (pemFormat) {
      StringReader sr = new StringReader(privateKey);
      PemReader reader = new PemReader(sr);
      PKCS8EncodedKeySpec keySpec;
      try {
        keySpec = new PKCS8EncodedKeySpec(reader.readPemObject().getContent());
      } catch (IOException e) {
        throw new InvalidPrivateKeyFormatException(e.getMessage());
      }
      Ed25519PrivateKeyParameters ed25519PrivateKeyParameters =
          new Ed25519PrivateKeyParameters(keySpec.getEncoded());
      this.key = ed25519PrivateKeyParameters.getEncoded();

    } else {
      this.key = MultibaseFactory.create(privateKey).getDecoded();
    }

    if (this.getKeyLength() != key.length) {
      throw new InvalidPrivateKeyFormatException(getKeyLength(), privateKey.length());
    }
  }

  @Override
  public String asStringForStoring() throws KeyTransformationException {

    PemObject pemObject = new PemObject("ED25519 Private Key", this.key);
    StringWriter sw = new StringWriter();
    PemWriter writer = new PemWriter(sw);
    try {
      writer.writeObject(pemObject);
      writer.close();
    } catch (IOException e) {
      throw new KeyTransformationException(e.getMessage());
    }

    return sw.toString();
  }

  @Override
  public String asStringForExchange(EncodeType encodeType) {

    return MultibaseFactory.create(encodeType, key).getEncoded();
  }

  @Override
  public byte[] asByte() {
    return this.key;
  }

  @Override
  public int getKeyLength() {
    return KEY_LENGTH;
  }
}
