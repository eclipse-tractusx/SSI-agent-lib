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

package org.eclipse.tractusx.ssi.lib.exception;

/** The type Invalid json ld exception. */
public class InvalidJsonLdException extends Exception {
  /**
   * Instantiates a new Invalid json ld exception.
   *
   * @param message the message
   */
  public InvalidJsonLdException(String message) {
    super(message);
  }

  /**
   * Instantiates a new Invalid json ld exception.
   *
   * @param message the message
   * @param cause the cause
   */
  public InvalidJsonLdException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new Invalid json ld exception.
   *
   * @param cause the cause
   */
  public InvalidJsonLdException(Throwable cause) {
    super(cause);
  }
}
