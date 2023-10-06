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
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolver;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolverException;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.JwtException;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedVerificationMethodException;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
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
   */
  @SneakyThrows({JOSEException.class, DidResolverException.class})
  public boolean verify(SignedJWT jwt)
      throws JwtException, DidDocumentResolverNotRegisteredException {

    JWTClaimsSet jwtClaimsSet;
    try {
      jwtClaimsSet = jwt.getJWTClaimsSet();
    } catch (ParseException e) {
      throw new JwtException(e);
    }

    final String issuer = jwtClaimsSet.getIssuer();
    final Did issuerDid = DidParser.parse(issuer);

    final DidDocument issuerDidDocument = didResolver.resolve(issuerDid);
    final List<VerificationMethod> verificationMethods = issuerDidDocument.getVerificationMethods();

    // verify JWT signature
    // TODO Don't try out each key. Better -> use key authorization key
    for (VerificationMethod verificationMethod : verificationMethods) {
      if (JWKVerificationMethod.isInstance(verificationMethod)) {
        final JWKVerificationMethod method = new JWKVerificationMethod(verificationMethod);
        final String kty = method.getPublicKeyJwk().getKty();
        final String crv = method.getPublicKeyJwk().getCrv();
        final String x = method.getPublicKeyJwk().getX();
        if (kty.equals("OKP") && crv.equals("Ed25519")) {
          final OctetKeyPair keyPair =
              new OctetKeyPair.Builder(Curve.Ed25519, Base64URL.from(x)).build();
          if (jwt.verify(new Ed25519Verifier(keyPair))) return true;
        } else {
          throw new UnsupportedVerificationMethodException(
              method, "only kty:OKP with crv:Ed25519 is supported");
        }
      } else if (Ed25519VerificationMethod.isInstance(verificationMethod)) {
        final Ed25519VerificationMethod method = new Ed25519VerificationMethod(verificationMethod);
        final MultibaseString multibase = method.getPublicKeyBase58();
        final Ed25519PublicKeyParameters publicKeyParameters =
            new Ed25519PublicKeyParameters(multibase.getDecoded(), 0);
        final OctetKeyPair keyPair =
            new OctetKeyPair.Builder(
                    Curve.Ed25519, Base64URL.encode(publicKeyParameters.getEncoded()))
                .build();

        if (jwt.verify(new Ed25519Verifier(keyPair))) return true;
      }
    }

    return false;
  }
}
