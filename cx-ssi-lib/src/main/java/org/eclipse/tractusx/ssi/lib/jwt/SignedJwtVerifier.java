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

package org.eclipse.tractusx.ssi.lib.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.JwtException;
import org.eclipse.tractusx.ssi.lib.exception.JwtSignatureCheckFailedException;
import org.eclipse.tractusx.ssi.lib.model.did.*;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolverRegistry;

/**
 * Convenience/helper class to generate and verify Signed JSON Web Tokens (JWTs) for communicating
 * between connector instances.
 */
@RequiredArgsConstructor
public class SignedJwtVerifier {

  private final DidDocumentResolverRegistry didDocumentResolverRegistry;

  /**
   * Verifies a VerifiableCredential using the issuer's public key
   *
   * @param jwt a {@link SignedJWT} that was sent by the claiming party.
   * @return true if verified, false otherwise
   */
  @SneakyThrows({JOSEException.class})
  public void verify(SignedJWT jwt) throws JwtException, DidDocumentResolverNotRegisteredException {

    JWTClaimsSet jwtClaimsSet;
    try {
      jwtClaimsSet = jwt.getJWTClaimsSet();
    } catch (ParseException e) {
      throw new JwtException(e);
    }

    final String issuer = jwtClaimsSet.getIssuer();
    final Did issuerDid = DidParser.parse(issuer);

    final DidDocumentResolver didDocumentResolver;
    didDocumentResolver = didDocumentResolverRegistry.get(issuerDid.getMethod());

    final DidDocument issuerDidDocument = didDocumentResolver.resolve(issuerDid);
    final List<VerificationMethod> verificationMethods = issuerDidDocument.getVerificationMethods();

    // verify JWT signature
    // TODO Don't try out each key. Better -> use key authorization key
    for (VerificationMethod verificationMethod : verificationMethods) {
      if (!Ed25519VerificationKey2020.isInstance(verificationMethod)) continue;

      var method = new Ed25519VerificationKey2020(verificationMethod);
      var multibase = method.getPublicKeyBase58();

      Ed25519PublicKeyParameters publicKeyParameters =
          new Ed25519PublicKeyParameters(multibase.getDecoded(), 0);
      var keyPair =
          new OctetKeyPair.Builder(
                  Curve.Ed25519, Base64URL.encode(publicKeyParameters.getEncoded()))
              .build();
      var isValid = jwt.verify(new Ed25519Verifier(keyPair));

      if (!isValid) {
        throw new JwtSignatureCheckFailedException(issuerDid, verificationMethod.getId());
      }
    }
  }
}
