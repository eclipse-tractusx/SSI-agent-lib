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

package org.eclipse.tractusx.ssi.lib.crypt;

import org.eclipse.tractusx.ssi.lib.exception.key.KeyTransformationException;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;

/** The interface Key. */
public interface IKey {
  /**
   * Gets key length.
   *
   * @return the key length
   */
  int getKeyLength();

  /**
   * Convert the key to a string for storing
   *
   * @return the string
   * @throws IOException the io exception
   */
  String asStringForStoring() throws KeyTransformationException;

  /**
   * Convert the key to a string for exchange.
   *
   * @param encodeType the encode type
   * @return the string
   * @throws IOException the io exception
   */
  String asStringForExchange(EncodeType encodeType) throws KeyTransformationException;

  /**
   * Convert the key to a byte array.
   *
   * @return the byte [ ]
   */
  byte[] asByte();
}
