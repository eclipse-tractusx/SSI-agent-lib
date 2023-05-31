/********************************************************************************
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
 ********************************************************************************/

package org.eclipse.tractusx.ssi.lib.base;

import io.ipfs.multibase.Multibase;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;

@Value
@EqualsAndHashCode
public class Base64WithPadding implements MultibaseString {

  public static boolean canDecode(String encoded) {
    return Multibase.encoding(encoded).equals(Multibase.Base.Base64Pad);
  }

  public static Base64WithPadding create(byte[] decoded) {
    final String encoded = Multibase.encode(Multibase.Base.Base64Pad, decoded);
    return new Base64WithPadding(decoded, encoded);
  }

  public static Base64WithPadding create(String encoded) {

    if (!canDecode(encoded)) {
      throw new IllegalArgumentException(
          "Encoded base64 String not in Base64 format (with padding)");
    }

    final byte[] base64 = Multibase.decode(encoded);

    return new Base64WithPadding(base64, encoded);
  }

  byte @NonNull [] decoded;
  @NonNull String encoded;
}
