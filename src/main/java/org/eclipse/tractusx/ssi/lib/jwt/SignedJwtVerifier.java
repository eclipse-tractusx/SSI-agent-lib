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

package org.eclipse.tractusx.ssi.lib.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.crypto.impl.ECDSAProvider;
import com.nimbusds.jose.crypto.impl.EdDSAProvider;
import com.nimbusds.jose.crypto.impl.RSASSAProvider;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.proc.JWSVerifierFactory;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolver;
import org.eclipse.tractusx.ssi.lib.exception.did.DidParseException;
import org.eclipse.tractusx.ssi.lib.exception.did.DidResolverException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureParseException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureVerificationException;
import org.eclipse.tractusx.ssi.lib.exception.proof.UnsupportedVerificationMethodException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidParser;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.did.JWKVerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.did.VerificationMethod;

/**
 * Convenience/helper class to verify Signed JSON Web Tokens (JWTs) for communicating between
 * connector instances.
 */
@RequiredArgsConstructor
public class SignedJwtVerifier {

  private final DidResolver didResolver;

  /**
   * Verifies a VerifiableCredential using the issuer's public key
   *
   * @param jwt a {@link SignedJWT} that was sent by the claiming party.
   * @return true if verified, false otherwise
   * @throws DidParseException
   * @throws SignatureException
   * @throws DidResolverException
   * @throws SignatureVerificationException
   * @throws UnsupportedVerificationMethodException
   * @throws SignatureParseException
   */
  public boolean verify(SignedJWT jwt)
      throws DidParseException, DidResolverException, SignatureVerificationException,
          SignatureParseException {

    JWTClaimsSet jwtClaimsSet;
    try {
      jwtClaimsSet = jwt.getJWTClaimsSet();
    } catch (ParseException e) {
      throw new SignatureParseException(e.getMessage());
    }

    final String issuer = jwtClaimsSet.getIssuer();
    final Did issuerDid = DidParser.parse(issuer);

    final DidDocument issuerDidDocument =
        didResolver
            .resolve(issuerDid)
            .orElseThrow(() -> new IllegalStateException("document could not be resolved"));
    final List<VerificationMethod> verificationMethods = issuerDidDocument.getVerificationMethods();

    // verify JWT signature
    Map<String, VerificationMethod> verificationMethodMap = toMap(verificationMethods);

    String keyID = jwt.getHeader().getKeyID();
    VerificationMethod verificationMethod = verificationMethodMap.get(keyID);
    if (verificationMethod == null) {
      throw new IllegalArgumentException(
          String.format("no verification method for keyID %s found", keyID));
    }

    try {
      if (JWKVerificationMethod.isInstance(verificationMethod)) {
        final JWKVerificationMethod method = new JWKVerificationMethod(verificationMethod);
        JWSVerifier verifier = getVerifier(jwt.getHeader(), method.getJwk());
        return jwt.verify(verifier);
      } else if (Ed25519VerificationMethod.isInstance(verificationMethod)) {
        final Ed25519VerificationMethod method = new Ed25519VerificationMethod(verificationMethod);
        return jwt.verify(new Ed25519Verifier(method.getOctetKeyPair()));
      }
    } catch (JOSEException e) {
      throw new SignatureVerificationException(e.getMessage());
    }

    return false;
  }

  private Map<String, VerificationMethod> toMap(List<VerificationMethod> l) {
    Map<String, VerificationMethod> result = new HashMap<>();
    l.forEach(v -> result.put(v.getId().toString(), v));
    return result;
  }

  private JWSVerifier getVerifier(JWSHeader header, JWK key) throws JOSEException {
    if (EdDSAProvider.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {
      return new Ed25519Verifier(((OctetKeyPair) key).toPublicJWK());
    } else {
      JWSVerifierFactory verifierFactory = new DefaultJWSVerifierFactory();
      if (RSASSAProvider.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()))
        return verifierFactory.createJWSVerifier(header, key.toRSAKey().toRSAPublicKey());
      if (ECDSAProvider.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()))
        return verifierFactory.createJWSVerifier(header, key.toECKey().toPublicKey());
    }
    throw new IllegalArgumentException(
        String.format("algorithm %s is not supported", header.getAlgorithm().getName()));
  }
}
