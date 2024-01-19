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

package org.eclipse.tractusx.ssi.lib.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.exception.JwtAudienceCheckFailedException;
import org.eclipse.tractusx.ssi.lib.exception.JwtExpiredException;

/** The type Signed jwt validator. */
public class SignedJwtValidator {

  /**
   * Validate date.
   *
   * @param jwt the jwt
   */
  @SneakyThrows
  public void validateDate(SignedJWT jwt) {
    Date expiryDate = jwt.getJWTClaimsSet().getExpirationTime();
    boolean isExpired = expiryDate.before(new Date()); // Todo add Timezone
    if (isExpired) {
      throw new JwtExpiredException(expiryDate);
    }
  }

  /**
   * Validate audiences.
   *
   * @param jwt the jwt
   * @param expectedAudience the expected audience
   */
  @SneakyThrows
  public void validateAudiences(SignedJWT jwt, String expectedAudience) {
    List<String> audiences = jwt.getJWTClaimsSet().getAudience();
    boolean isValidAudience = audiences.stream().anyMatch(x -> x.equals(expectedAudience));
    if (!isValidAudience) {
      throw new JwtAudienceCheckFailedException(expectedAudience, audiences);
    }
  }
}
