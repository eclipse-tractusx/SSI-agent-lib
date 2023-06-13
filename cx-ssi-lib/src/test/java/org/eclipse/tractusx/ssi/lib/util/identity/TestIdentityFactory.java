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
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.List;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.jcajce.provider.asymmetric.edec.KeyPairGeneratorSpi;
import org.eclipse.tractusx.ssi.lib.crypt.jwk.JsonWebKey;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocumentBuilder;
import org.eclipse.tractusx.ssi.lib.model.did.DidParser;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationMethodBuilder;
import org.eclipse.tractusx.ssi.lib.model.did.JWKVerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.did.JWKVerificationMethodBuilder;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;

public class TestIdentityFactory {

  public static TestIdentity newIdentityWithED25519Keys(boolean PKCS8Format) throws IOException {

    byte[] publicKey = null;
    byte[] privateKey = null;
    final Did did = TestDidFactory.createRandom();

    if (PKCS8Format) {

      // PKCS8 keys format
      publicKey = TestResourceUtil.getPublicKeyEd25519();
      privateKey = TestResourceUtil.getPrivateKeyEd25519();

      Ed25519PrivateKeyParameters ed25519PrivateKeyParameters =
          (Ed25519PrivateKeyParameters) PrivateKeyFactory.createKey(privateKey);
      Ed25519PublicKeyParameters ed25519publicKeyParameters =
          (Ed25519PublicKeyParameters) PublicKeyFactory.createKey(publicKey);

      // 32-byte Ed25519 format
      publicKey = ed25519publicKeyParameters.getEncoded();
      privateKey = ed25519PrivateKeyParameters.getEncoded();
    } else {
      KeyPairGeneratorSpi.Ed25519 ed25519 = new KeyPairGeneratorSpi.Ed25519();
      ed25519.initialize(256, new SecureRandom());
      KeyPair keyPair = ed25519.generateKeyPair();
      PublicKey PubKey = keyPair.getPublic();
      PrivateKey PivKey = keyPair.getPrivate();
      
      
      // Ed25519PrivateKeyParameters privateKeyParameters =
      // new Ed25519PrivateKeyParameters (PivKey.getEncoded(),0);
      // Ed25519PublicKeyParameters publicKeyParameters =
      //     new  Ed25519PublicKeyParameters(PubKey.getEncoded(),0);

      Ed25519PrivateKeyParameters privateKeyParameters =
      (Ed25519PrivateKeyParameters) PrivateKeyFactory.createKey(PivKey.getEncoded());
      Ed25519PublicKeyParameters publicKeyParameters =
      (Ed25519PublicKeyParameters) PublicKeyFactory.createKey(PubKey.getEncoded());

      publicKey = publicKeyParameters.getEncoded();

      privateKey = privateKeyParameters.getEncoded();
    }

   

    System.out.println(new String(publicKey));
    final MultibaseString publicKeyMultiBase = MultibaseFactory.create(publicKey);
    System.out.println(new String(publicKeyMultiBase.getDecoded()));

    final Ed25519VerificationMethodBuilder ed25519VerificationKey2020Builder =
        new Ed25519VerificationMethodBuilder();
    
    final Ed25519VerificationMethod ed25519VerificationMethod =
        ed25519VerificationKey2020Builder
            .id(URI.create(did + "#key-1"))
            .controller(URI.create(did + "#controller"))
            .publicKeyMultiBase(publicKeyMultiBase)
            .build();


    //JWK 
    JsonWebKey jwk = JsonWebKey.fromED21559("key-2",publicKey, privateKey);

    final JWKVerificationMethod jwkVerificationMethod =
           new JWKVerificationMethodBuilder().did(did).jwk(jwk).build();


    final DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
    final DidDocument didDocument =
        didDocumentBuilder.id(did.toUri()).verificationMethods(List.of(ed25519VerificationMethod,jwkVerificationMethod)).build();

    return new TestIdentity(did, didDocument, publicKey, privateKey);
  }

  public static TestIdentity newBPNIdentityWithED25519Keys(boolean PKCS8Format) throws IOException {

    byte[] publicKey = null;
    byte[] privateKey = null;

    if (PKCS8Format) {
      // PKCS8 keys format
      publicKey = TestResourceUtil.getPublicKeyEd25519();
      privateKey = TestResourceUtil.getPrivateKeyEd25519();

      Ed25519PrivateKeyParameters ed25519PrivateKeyParameters =
          (Ed25519PrivateKeyParameters) PrivateKeyFactory.createKey(privateKey);
      Ed25519PublicKeyParameters ed25519publicKeyParameters =
          (Ed25519PublicKeyParameters) PublicKeyFactory.createKey(publicKey);

      // 32-byte Ed25519 format
      publicKey = ed25519publicKeyParameters.getEncoded();
      privateKey = ed25519PrivateKeyParameters.getEncoded();
    } else {
      KeyPairGeneratorSpi.Ed25519 ed25519 = new KeyPairGeneratorSpi.Ed25519();
      ed25519.initialize(256, new SecureRandom());
      KeyPair keyPair = ed25519.generateKeyPair();
      PublicKey PubKey = keyPair.getPublic();
      PrivateKey PivKey = keyPair.getPrivate();
      Ed25519PrivateKeyParameters ed25519PrivateKeyParameters =
          (Ed25519PrivateKeyParameters) PrivateKeyFactory.createKey(PivKey.getEncoded());
      Ed25519PublicKeyParameters publicKeyParameters =
          (Ed25519PublicKeyParameters) PublicKeyFactory.createKey(PubKey.getEncoded());

      publicKey = publicKeyParameters.getEncoded();

      privateKey = ed25519PrivateKeyParameters.getEncoded();
    }

    DidDocument didDocument = new DidDocument(TestResourceUtil.getBPNDidDocument());
    Did did = DidParser.parse(didDocument.getId()); // new Did(new DidMethod("web"),new
    // DidMethodIdentifier(didDocument.getId().toString()));

    return new TestIdentity(did, didDocument, publicKey, privateKey);
  }
}
