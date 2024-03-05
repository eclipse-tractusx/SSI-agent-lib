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

package org.eclipse.tractusx.ssi.lib.proof.types.jws;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import java.net.URI;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolver;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolverException;
import org.eclipse.tractusx.ssi.lib.exception.did.DidParseException;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPublicKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.proof.NoVerificationKeyFoundException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureParseException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureVerificationFailedException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidParser;
import org.eclipse.tractusx.ssi.lib.model.did.JWKVerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.proof.IVerifier;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

/** The type Jws proof verifier. */
@RequiredArgsConstructor
public class JWSProofVerifier implements IVerifier {

  private final DidResolver didResolver;

  @SneakyThrows({DidResolverException.class})
  public boolean verify(HashedLinkedData hashedLinkedData, Verifiable document)
      throws SignatureParseException, DidParseException, InvalidPublicKeyFormatException,
          SignatureVerificationFailedException {

    final Proof proof = document.getProof();
    if (!proof.getType().equals(JWSSignature2020.JWS_VERIFICATION_KEY_2020)) {
      throw new SignatureParseException(
          String.format("Unsupported verification method: %s", proof.getType()));
    }

    final JWSSignature2020 jwsSignature2020 = new JWSSignature2020(proof);

    Payload payload = new Payload(hashedLinkedData.getValue());

    JWSObject jws;

    try {
      jws = JWSObject.parse(jwsSignature2020.getJws(), payload);
    } catch (ParseException e) {
      throw new SignatureParseException(jwsSignature2020.getJws());
    }

    OctetKeyPair keyPair = null;
    try {
      keyPair = this.discoverOctectKey(jwsSignature2020);
    } catch (NoVerificationKeyFoundException e) {
      throw new SignatureParseException(e.getMessage());
    }

    JWSVerifier verifier;
    try {
      verifier = new Ed25519Verifier(keyPair);
    } catch (JOSEException e) {
      throw new InvalidPublicKeyFormatException(e.getMessage());
    }

    try {
      return jws.verify(verifier);
    } catch (JOSEException e) {
      throw new SignatureVerificationFailedException(e.getMessage());
    }
  }

  private OctetKeyPair discoverOctectKey(JWSSignature2020 signature)
      throws NoVerificationKeyFoundException, DidParseException, DidResolverException {

    final Did issuer = DidParser.parse(signature.getVerificationMethod());

    final DidDocument document = this.didResolver.resolve(issuer);

    final URI verificationMethodId = signature.getVerificationMethod();

    final JWKVerificationMethod key =
        document.getVerificationMethods().stream()
            .filter(v -> v.getId().equals(verificationMethodId))
            .filter(JWKVerificationMethod::isInstance)
            .map(JWKVerificationMethod::new)
            .findFirst()
            .orElseThrow(
                () ->
                    new NoVerificationKeyFoundException(
                        "No JWS verification Key found in DID Document"));

    var x = Base64URL.from(key.getPublicKeyJwk().getX());

    var keyPair = new OctetKeyPair.Builder(Curve.Ed25519, x).build();

    return keyPair;
  }

  /**
   * Verify hashedLinkedData.
   *
   * @param hashedLinkedData the hashed linked data
   * @param signature the signature
   * @param publicKey the public key
   * @return the boolean
   * @throws SignatureParseException
   * @throws SignatureVerificationFailedException
   * @throws InvalidPublicKeyFormatException
   */
  public boolean verify(HashedLinkedData hashedLinkedData, byte[] signature, IPublicKey publicKey)
      throws SignatureParseException, SignatureVerificationFailedException,
          InvalidPublicKeyFormatException {

    var keyPair =
        new OctetKeyPair.Builder(Curve.Ed25519, Base64URL.encode(publicKey.asByte())).build();
    JWSVerifier verifier;
    try {
      verifier = (JWSVerifier) new Ed25519Verifier(keyPair.toPublicJWK());
    } catch (JOSEException e) {
      throw new InvalidPublicKeyFormatException(e.getMessage());
    }
    Payload payload = new Payload(hashedLinkedData.getValue());
    JWSObject jws;
    try {

      jws = JWSObject.parse(new String(signature), payload);
    } catch (ParseException e) {
      throw new SignatureParseException(String.format("Error while parsing JWS %s", signature));
    }
    try {
      return jws.verify(verifier);
    } catch (JOSEException e) {
      throw new SignatureVerificationFailedException(e.getMessage());
    }
  }
}
