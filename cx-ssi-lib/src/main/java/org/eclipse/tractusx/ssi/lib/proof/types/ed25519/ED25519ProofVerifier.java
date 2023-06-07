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

import java.net.URI;

import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.eclipse.tractusx.ssi.lib.base.IVerifier;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidParser;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.ed21559.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolverRegistry;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;


@RequiredArgsConstructor
public class ED25519ProofVerifier implements IVerifier {

    private final DidDocumentResolverRegistry didDocumentResolverRegistry;

    public boolean verify(HashedLinkedData hashedLinkedData, VerifiableCredential credential)
        throws UnsupportedSignatureTypeException, DidDocumentResolverNotRegisteredException {
  
      final URI issuer = credential.getIssuer();
      final Did issuerDid = DidParser.parse(issuer);
  
      final DidDocumentResolver didDocumentResolver;
      didDocumentResolver = didDocumentResolverRegistry.get(issuerDid.getMethod());
  
      final DidDocument document = didDocumentResolver.resolve(issuerDid);
      final Proof proof = credential.getProof();
      if (!proof.getType().equals(Ed25519Signature2020.ED25519_VERIFICATION_KEY_2018)) {
        throw new UnsupportedSignatureTypeException(proof.getType());
      }
      final Ed25519Signature2020 ed25519Signature2020 = new Ed25519Signature2020(proof);
  
      final URI verificationMethodId = ed25519Signature2020.getVerificationMethod();
      final Ed25519VerificationKey2020 key =
          document.getVerificationMethods().stream()
              .filter(v -> v.getId().equals(verificationMethodId))
              .filter(Ed25519VerificationKey2020::isInstance)
              .map(Ed25519VerificationKey2020::new)
              .findFirst()
              .orElseThrow();
  
      final MultibaseString publicKey = key.getPublicKeyBase58();
      final MultibaseString signature = ed25519Signature2020.getProofValue();
      return verify(hashedLinkedData, signature.getDecoded(), publicKey.getDecoded());
    }
  
    @SneakyThrows
    public boolean verify(HashedLinkedData hashedLinkedData, byte[] signature, byte[] publicKey) {
  
      final byte[] message = hashedLinkedData.getValue();
  
      Signer verifier = new Ed25519Signer();
      Ed25519PublicKeyParameters publicKeyParameters = new Ed25519PublicKeyParameters(publicKey, 0);
      verifier.init(false, publicKeyParameters);
      verifier.update(message, 0, message.length);
  
      return verifier.verifySignature(signature);
    }
    
}
