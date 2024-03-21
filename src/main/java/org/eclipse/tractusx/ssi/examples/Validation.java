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
import org.eclipse.tractusx.ssi.lib.exception.proof.JwtAudienceCheckException;
import org.eclipse.tractusx.ssi.lib.exception.proof.JwtExpiredException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureParseException;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtValidator;

/** This is example class to demonstrate how to validate JWT expiry date and audience */
public class Validation {

  private Validation() {
    // static
  }
  /**
   * Validate jwt date.
   *
   * @param signedJWT the signed jwt
   * @param audience the audience
   * @throws JwtAudienceCheckException the jwt audience check failed exception
   * @throws JwtExpiredException the jwt expired exception
   */
  public static void validateJWTDate(SignedJWT signedJWT)
      throws JwtExpiredException, SignatureParseException {
    SignedJwtValidator jwtValidator = new SignedJwtValidator();
    jwtValidator.validateDate(signedJWT);
  }

  /**
   * Validate jwt audiences.
   *
   * @param signedJWT the signed jwt
   * @param audience the audience
   * @throws JwtAudienceCheckException the jwt audience check failed exception
   * @throws JwtExpiredException the jwt expired exception
   */
  public static void validateJWTAudiences(SignedJWT signedJWT, String audience)
      throws JwtAudienceCheckException, SignatureParseException {
    SignedJwtValidator jwtValidator = new SignedJwtValidator();
    jwtValidator.validateAudiences(signedJWT, audience);
  }
}
