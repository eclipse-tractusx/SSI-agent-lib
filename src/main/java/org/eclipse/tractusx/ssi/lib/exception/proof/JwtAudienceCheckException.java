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

package org.eclipse.tractusx.ssi.lib.exception.proof;

import java.util.List;

/** The type Jwt audience check failed exception. */
public class JwtAudienceCheckException extends SignatureValidationException {
  private static final long serialVersionUID = 1L;
  /**
   * Instantiates a new Jwt audience check failed exception.
   *
   * @param expectedAudience the expected audience
   * @param actualAudience the actual audience
   */
  public JwtAudienceCheckException(String expectedAudience, List<String> actualAudience) {
    super(
        "JWT audience check failed. Expected audience: "
            + expectedAudience
            + ", actual audience: "
            + String.join(",  ", actualAudience));
  }

  public JwtAudienceCheckException(String message) {
    super(message);
  }

  public JwtAudienceCheckException(String message, Throwable cause) {
    super(message, cause);
  }

  public JwtAudienceCheckException(Throwable cause) {
    super(cause);
  }

  public JwtAudienceCheckException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
