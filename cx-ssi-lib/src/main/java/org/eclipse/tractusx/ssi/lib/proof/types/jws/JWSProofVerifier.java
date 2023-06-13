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

import java.net.URI;
import java.text.ParseException;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidParser;
import org.eclipse.tractusx.ssi.lib.model.did.JWKVerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.IVerifier;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public class JWSProofVerifier implements IVerifier {

  private final DidDocumentResolverRegistry didDocumentResolverRegistry;

  public boolean verify(HashedLinkedData hashedLinkedData, VerifiableCredential credential)
      throws UnsupportedSignatureTypeException, DidDocumentResolverNotRegisteredException {

    final URI issuer = credential.getIssuer();
    final Did issuerDid = DidParser.parse(issuer);

    final DidDocumentResolver didDocumentResolver;
    didDocumentResolver = didDocumentResolverRegistry.get(issuerDid.getMethod());

    final DidDocument document = didDocumentResolver.resolve(issuerDid);
   
    final Proof proof = credential.getProof();
    if (!proof.getType().equals(JWSSignature2020.JWS_VERIFICATION_KEY_2020)) {
      throw new UnsupportedSignatureTypeException(proof.getType());
    }

    final JWSSignature2020 jwsSignature2020 = new JWSSignature2020(proof);

    final URI verificationMethodId = jwsSignature2020.getVerificationMethod();

    final JWKVerificationMethod key = document.getVerificationMethods().stream()
        .filter(v -> v.getId().equals(verificationMethodId))
        .filter(JWKVerificationMethod::isInstance)
        .map(JWKVerificationMethod::new)
        .findFirst()
        .orElseThrow();
   
    var x = Base64URL.from(key.getPublicKeyJwk().getX());

    var keyPair = new OctetKeyPair.Builder(
         Curve.Ed25519,x)
         .build();

    Payload payload = new Payload(hashedLinkedData.getValue());
    JWSObject jws;
    
    try {
      jws = JWSObject.parse(jwsSignature2020.getJws(), payload);
    } catch (ParseException e) {
      throw new SsiException(e.getMessage());
    }

    JWSVerifier verifier;
    try {
      verifier = new Ed25519Verifier(keyPair);
    } catch (JOSEException e) {
      throw new SsiException(e.getMessage());
    }

    try {
      return jws.verify(verifier);
    } catch (JOSEException e) {
      throw new SsiException(e.getMessage());
    }
  }

  @SneakyThrows
  public boolean verify(HashedLinkedData hashedLinkedData, byte[] signature, byte[] publicKey) {

    var keyPair = new OctetKeyPair.Builder(
        Curve.Ed25519, Base64URL.encode(publicKey))
        .build();
    JWSVerifier verifier = (JWSVerifier) new Ed25519Verifier(keyPair.toPublicJWK());
    Payload payload = new Payload(hashedLinkedData.getValue());
    JWSObject jws;
    try {

      jws = JWSObject.parse(new String(signature), payload);
    } catch (ParseException e) {
      throw new SsiException(e.getMessage());
    }
    return jws.verify(verifier);
  }
}
