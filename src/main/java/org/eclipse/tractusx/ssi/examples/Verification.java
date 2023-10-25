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

package org.eclipse.tractusx.ssi.examples;

import com.nimbusds.jwt.SignedJWT;
import java.net.http.HttpClient;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebResolver;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.JwtException;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofValidation;

/**
 * This is example class to demonstrate how to verify @{@link SignedJWT} and {@link
 * VerifiableCredential}
 */
public class Verification {

  /**
   * Verify jwt.
   *
   * @param jwt the jwt
   */
  public static void verifyJWT(SignedJWT jwt) {
    // DID Resolver constructor params
    DidWebParser didParser = new DidWebParser();
    var httpClient = HttpClient.newHttpClient();
    var enforceHttps = false;
    var didResolver = new DidWebResolver(httpClient, didParser, enforceHttps);

    SignedJwtVerifier jwtVerifier = new SignedJwtVerifier(didResolver);
    try {
      jwtVerifier.verify(jwt);
    } catch (JwtException | DidDocumentResolverNotRegisteredException e) {
      // An exception will be thrown here in case JWT verification failed or DID
      // Document Resolver not able to resolve.
      e.printStackTrace();
    }
  }

  /**
   * Verify ed21559 signed ld.
   *
   * @param verifiableCredential the verifiable credential
   * @return the boolean
   */
  public static boolean verifyED21559LD(VerifiableCredential verifiableCredential) {
    // DID Resolver constructor params
    DidWebParser didParser = new DidWebParser();
    var httpClient = HttpClient.newHttpClient();
    var enforceHttps = false;
    var didResolver = new DidWebResolver(httpClient, didParser, enforceHttps);

    LinkedDataProofValidation proofValidation = LinkedDataProofValidation.newInstance(didResolver);
    return proofValidation.verify(verifiableCredential);
  }

  /**
   * Verify jws signed ld.
   *
   * @param verifiableCredential the verifiable credential
   * @return the boolean
   */
  public static boolean verifyJWSLD(VerifiableCredential verifiableCredential) {
    // DID Resolver constructor params
    DidWebParser didParser = new DidWebParser();
    var httpClient = HttpClient.newHttpClient();
    var enforceHttps = false;
    var didResolver = new DidWebResolver(httpClient, didParser, enforceHttps);

    LinkedDataProofValidation proofValidation = LinkedDataProofValidation.newInstance(didResolver);
    return proofValidation.verify(verifiableCredential);
  }
}
