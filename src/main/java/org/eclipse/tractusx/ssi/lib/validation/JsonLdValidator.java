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

package org.eclipse.tractusx.ssi.lib.validation;

import org.eclipse.tractusx.ssi.lib.exception.InvalidJsonLdException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;

/** The interface Json ld validator. */
public interface JsonLdValidator {

  /**
   * Validate.
   *
   * @param verifiablePresentation the verifiable presentation
   * @throws InvalidJsonLdException the invalid json ld exception
   */
  void validate(VerifiablePresentation verifiablePresentation) throws InvalidJsonLdException;

  /**
   * Validate.
   *
   * @param verifiableCredential the verifiable credential
   * @throws InvalidJsonLdException the invalid json ld exception
   */
  void validate(VerifiableCredential verifiableCredential) throws InvalidJsonLdException;
}
