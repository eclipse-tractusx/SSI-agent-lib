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

package org.eclipse.tractusx.ssi.lib.model.base;

import io.ipfs.multibase.Multibase;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;

/** The type Base64. */
@Value
@EqualsAndHashCode
public class Base64 implements MultibaseString {

  /**
   * Check if it can decode a string.
   *
   * @param encoded the encoded string
   * @return the boolean
   */
  public static boolean canDecode(String encoded) {
    return Multibase.encoding(encoded).equals(Multibase.Base.Base64);
  }

  /**
   * Create Base64.
   *
   * @param decoded the decoded
   * @return the Base64
   */
  public static Base64 create(byte[] decoded) {
    final String encoded = Multibase.encode(Multibase.Base.Base64, decoded);
    return new Base64(decoded, encoded);
  }

  /**
   * Create Base64.
   *
   * @param encoded the encoded
   * @return the Base64
   */
  public static Base64 create(String encoded) {

    if (!canDecode(encoded)) {
      throw new IllegalArgumentException();
    }

    final byte[] decoded = Multibase.decode(encoded);

    return new Base64(decoded, encoded);
  }

  byte @NonNull [] decoded;
  @NonNull String encoded;
}
