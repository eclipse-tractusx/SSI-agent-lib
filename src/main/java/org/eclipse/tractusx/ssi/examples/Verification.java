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
 * *******************************************************************************
 */

package org.eclipse.tractusx.ssi.examples;

import com.nimbusds.jwt.SignedJWT;
import java.net.http.HttpClient;
import java.security.SignatureException;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebResolver;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.did.DidParseException;
import org.eclipse.tractusx.ssi.lib.exception.did.DidResolverException;
import org.eclipse.tractusx.ssi.lib.exception.json.TransformJsonLdException;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPublicKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.proof.NoVerificationKeyFoundException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureParseException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureVerificationException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureVerificationFailedException;
import org.eclipse.tractusx.ssi.lib.exception.proof.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofValidation;

/**
 * This is example class to demonstrate how to verify @{@link SignedJWT} and {@link
 * VerifiableCredential}*
 */
public class Verification {

  private Verification() {
    throw new IllegalStateException("Example class");
  }

  /**
   * Verify jwt.
   *
   * @param jwt the jwt
   * @throws DidParseException the did parse exception
   * @throws DidResolverException the did resolver exception
   * @throws SignatureVerificationFailedException the signature verification failed exception
   * @throws SignatureVerificationException the signature verification exception
   * @throws SignatureParseException the signature parse exception
   * @throws SignatureException the signature exception
   */
  public static void verifyJWT(SignedJWT jwt)
      throws DidParseException, DidResolverException, SignatureVerificationFailedException,
          SignatureVerificationException, SignatureParseException, SignatureException {
    {
      // DID Resolver constructor params
      DidWebParser didParser = new DidWebParser();
      var httpClient = HttpClient.newHttpClient();
      var enforceHttps = false;
      var didResolver = new DidWebResolver(httpClient, didParser, enforceHttps);

      SignedJwtVerifier jwtVerifier = new SignedJwtVerifier(didResolver);

      jwtVerifier.verify(jwt);
    }
  }

  /**
   * Verify jwsld boolean.
   *
   * @param verifiableCredential the credential
   * @return true if the credential is valid
   * @throws TransformJsonLdException the transform json ld exception
   * @throws NoVerificationKeyFoundException the no verification key found exception
   * @throws UnsupportedSignatureTypeException the unsupported signature type exception
   * @throws InvalidPublicKeyFormatException the invalid public key format exception
   * @throws SignatureParseException the signature parse exception
   * @throws DidParseException the did parse exception
   * @throws SignatureVerificationFailedException the signature verification failed exception
   */
  public static boolean verifyJWSLD(VerifiableCredential verifiableCredential)
      throws TransformJsonLdException, NoVerificationKeyFoundException,
          UnsupportedSignatureTypeException, InvalidPublicKeyFormatException,
          SignatureParseException, DidParseException, SignatureVerificationFailedException {
    // DID Resolver constructor params
    DidWebParser didParser = new DidWebParser();
    var httpClient = HttpClient.newHttpClient();
    var enforceHttps = false;
    var didResolver = new DidWebResolver(httpClient, didParser, enforceHttps);

    LinkedDataProofValidation proofValidation = LinkedDataProofValidation.newInstance(didResolver);
    return proofValidation.verify(verifiableCredential);
  }
}
