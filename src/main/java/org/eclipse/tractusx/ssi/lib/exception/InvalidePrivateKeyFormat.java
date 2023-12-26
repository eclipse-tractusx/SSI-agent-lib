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

/** The type Invalide private key format. */
public class InvalidePrivateKeyFormat extends Exception {
  /**
   * Instantiates a new Invalide private key format.
   *
   * @param correctLength the correct length
   * @param providedLength the provided length
   */
  public InvalidePrivateKeyFormat(int correctLength, int providedLength) {
    super(
        String.format(
            "Invalide Private Key Format, this key should have '%s' as lenght but we got %s",
            correctLength, providedLength));
  }

  /**
   * Instantiates a new Invalide private key format.
   *
   * @param cause the cause
   */
  public InvalidePrivateKeyFormat(Throwable cause) {}
}
