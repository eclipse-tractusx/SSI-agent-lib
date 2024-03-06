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

package org.eclipse.tractusx.ssi.lib.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import org.eclipse.tractusx.ssi.lib.exception.proof.JwtAudienceCheckException;
import org.eclipse.tractusx.ssi.lib.exception.proof.JwtExpiredException;
import org.eclipse.tractusx.ssi.lib.exception.proof.SignatureParseException;

/** The type Signed jwt validator. */
public class SignedJwtValidator {

  public void validateDate(SignedJWT jwt) throws JwtExpiredException, SignatureParseException {
    Date expiryDate;
    try {
      expiryDate = jwt.getJWTClaimsSet().getExpirationTime();
    } catch (ParseException e) {
      throw new SignatureParseException(e.getMessage());
    }
    boolean isExpired = expiryDate.before(new Date()); // Todo add Timezone
    if (isExpired) {
      throw new JwtExpiredException(expiryDate);
    }
  }

  public void validateAudiences(SignedJWT jwt, String expectedAudience)
      throws SignatureParseException, JwtAudienceCheckException {
    List<String> audiences;
    try {
      audiences = jwt.getJWTClaimsSet().getAudience();
    } catch (ParseException e) {
      throw new SignatureParseException(e.getMessage());
    }
    boolean isValidAudience = audiences.stream().anyMatch(x -> x.equals(expectedAudience));
    if (!isValidAudience) {
      throw new JwtAudienceCheckException(expectedAudience, audiences);
    }
  }
}
