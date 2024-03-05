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

/** The type SSI exception. */
public class SSIException extends Exception {

  /**
   * Instantiates a new SSI exception.
   *
   * @param message the message
   */
  protected SSIException(String message) {
    super(message);
  }

  /**
   * Instantiates a new SSI exception.
   *
   * @param message the message
   * @param cause the cause
   */
  protected SSIException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new SSI exception.
   *
   * @param cause the cause
   */
  protected SSIException(Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new SSI exception.
   *
   * @param message the message
   * @param cause the cause
   * @param enableSuppression the enable suppression
   * @param writableStackTrace the writable stack trace
   */
  protected SSIException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
