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

import java.io.IOException;
import java.net.URI;
import java.util.List;
import org.eclipse.tractusx.ssi.lib.crypt.IKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.crypt.jwk.JsonWebKey;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559Generator;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PublicKey;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePublicKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocumentBuilder;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationMethodBuilder;
import org.eclipse.tractusx.ssi.lib.model.did.JWKVerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.did.JWKVerificationMethodBuilder;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;

public class TestIdentityFactory {

  public static TestIdentity newIdentityWithED25519Keys(boolean PEMformat)
      throws IOException, KeyGenerationException, InvalidePublicKeyFormat,
          InvalidePrivateKeyFormat {

    IPublicKey publicKey = null;
    IPrivateKey privateKey = null;
    final Did did = TestDidFactory.createRandom();

    if (PEMformat) {

      // PKCS8 keys format
      String publicKeyPEM = TestResourceUtil.getPublicKeyEd25519AsString();
      String privateKeyPEM = TestResourceUtil.getPrivateKeyEd25519AsString();

      // 32-byte Ed25519 format
      publicKey = new x21559PublicKey(publicKeyPEM, true);
      privateKey = new x21559PrivateKey(privateKeyPEM, true);
    } else {

      IKeyGenerator keyGenerator = new x21559Generator();
      KeyPair keyPair = keyGenerator.generateKey();
      publicKey = keyPair.getPublicKey();
      privateKey = keyPair.getPrivateKey();
    }

    MultibaseString multibaseString = MultibaseFactory.create(publicKey.asByte());
    final Ed25519VerificationMethodBuilder ed25519VerificationKey2020Builder =
        new Ed25519VerificationMethodBuilder();

    final Ed25519VerificationMethod ed25519VerificationMethod =
        ed25519VerificationKey2020Builder
            .id(URI.create(did + "#key-1"))
            .controller(URI.create(did + "#controller"))
            .publicKeyMultiBase(multibaseString)
            .build();

    // JWK
    JsonWebKey jwk = new JsonWebKey("key-2", publicKey, privateKey);

    final JWKVerificationMethod jwkVerificationMethod =
        new JWKVerificationMethodBuilder().did(did).jwk(jwk).build();

    final DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
    final DidDocument didDocument =
        didDocumentBuilder
            .id(did.toUri())
            .verificationMethods(List.of(ed25519VerificationMethod, jwkVerificationMethod))
            .build();

    return new TestIdentity(did, didDocument, publicKey, privateKey);
  }
}
