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

package org.eclipse.tractusx.ssi.lib.crypt.x21559;

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
import org.eclipse.tractusx.ssi.lib.exception.InvalidPrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;

public class x21559PrivateKey implements IPrivateKey {

  private final @NonNull byte[] key;

  public x21559PrivateKey(byte[] privateKey) throws InvalidPrivateKeyFormat {
    if (this.getKeyLength() != privateKey.length) {
      throw new InvalidPrivateKeyFormat(getKeyLength(), privateKey.length);
    }
    this.key = privateKey;
  }

  public x21559PrivateKey(String privateKey, boolean PEMFormat)
      throws InvalidPrivateKeyFormat, IOException {
    if (PEMFormat) {
      StringReader sr = new StringReader(privateKey);
      PemReader reader = new PemReader(sr);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(reader.readPemObject().getContent());
      Ed25519PrivateKeyParameters ed25519PrivateKeyParameters =
          new Ed25519PrivateKeyParameters(keySpec.getEncoded());
      this.key = ed25519PrivateKeyParameters.getEncoded();

    } else {
      this.key = MultibaseFactory.create(privateKey).getDecoded();
    }

    if (this.getKeyLength() != key.length) {
      throw new InvalidPrivateKeyFormat(getKeyLength(), privateKey.length());
    }
  }

  @Override
  public String asStringForStoring() throws IOException {

    PemObject pemObject = new PemObject("ED21559 Private Key", this.key);
    StringWriter sw = new StringWriter();
    PemWriter writer = new PemWriter(sw);
    writer.writeObject(pemObject);
    writer.close();
    return sw.toString();
  }

  @Override
  public String asStringForExchange(EncodeType encodeType) throws IOException {

    return MultibaseFactory.create(encodeType, key).getEncoded();
  }

  @Override
  public byte[] asByte() {
    return this.key;
  }

  @Override
  public int getKeyLength() {
    return 32;
  }
}
