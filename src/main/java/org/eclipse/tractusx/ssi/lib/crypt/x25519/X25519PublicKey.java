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

package org.eclipse.tractusx.ssi.lib.crypt.x25519;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.spec.X509EncodedKeySpec;
import lombok.NonNull;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPublicKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.key.KeyTransformationException;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;

/** The type X 25519 public key. */
public class X25519PublicKey implements IPublicKey {

  private static final int KEY_LENGTH = 32;
  private final @NonNull byte[] originalKey;

  /**
   * Instantiates a new X 25519 public key.
   *
   * @param publicKey the public key
   * @throws InvalidPublicKeyFormatException the invalide public key format
   */
  public X25519PublicKey(byte[] publicKey) throws InvalidPublicKeyFormatException {
    if (this.getKeyLength() != publicKey.length)
      throw new InvalidPublicKeyFormatException(getKeyLength(), publicKey.length);
    this.originalKey = publicKey;
  }

  /**
   * Instantiates a new X25519 public key.
   *
   * @param publicKey the public key
   * @param pemFormat the pe mformat
   * @throws InvalidPublicKeyFormatException the invalide public key format
   * @throws IOException the io exception
   */
  public X25519PublicKey(String publicKey, boolean pemFormat)
      throws InvalidPublicKeyFormatException, IOException {

    if (pemFormat) {
      StringReader sr = new StringReader(publicKey);
      PemReader reader = new PemReader(sr);
      PemObject pemObject = reader.readPemObject();
      X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(pemObject.getContent());
      Ed25519PublicKeyParameters ed25519PublicKeyParameters =
          new Ed25519PublicKeyParameters(encodedKeySpec.getEncoded());
      this.originalKey = ed25519PublicKeyParameters.getEncoded();
    } else {
      this.originalKey = MultibaseFactory.create(publicKey).getDecoded();
    }

    if (this.getKeyLength() != originalKey.length)
      throw new InvalidPublicKeyFormatException(getKeyLength(), originalKey.length);
  }

  @Override
  public String asStringForStoring() throws KeyTransformationException {
    PemObject pemObject = new PemObject("ED25519 Public Key", this.originalKey);
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
    return MultibaseFactory.create(encodeType, originalKey).getEncoded();
  }

  @Override
  public byte[] asByte() {
    return this.originalKey;
  }

  @Override
  public int getKeyLength() {
    return KEY_LENGTH;
  }

  public OctetKeyPair toJwk() {
    final Ed25519PublicKeyParameters publicKeyParameters =
        new Ed25519PublicKeyParameters(originalKey, 0);

    return new OctetKeyPair.Builder(
            Curve.Ed25519, Base64URL.encode(publicKeyParameters.getEncoded()))
        .build();
  }
}
