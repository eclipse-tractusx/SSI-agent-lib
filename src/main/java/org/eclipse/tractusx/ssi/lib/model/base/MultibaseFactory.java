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

package org.eclipse.tractusx.ssi.lib.model.base;

import org.eclipse.tractusx.ssi.lib.model.MultibaseString;

public class MultibaseFactory {

  public static MultibaseString create(EncodeType encodeType, byte[] decoded) {
    if (encodeType == EncodeType.Base58) return Base58Bitcoin.create(decoded);
    else return Base64.create(decoded);
  }

  public static MultibaseString create(byte[] decoded) {
    return Base58Bitcoin.create(decoded);
  }

  public static MultibaseString create(String encoded) {

    if (Base58Bitcoin.canDecode(encoded)) {
      return Base58Bitcoin.create(encoded);
    }
    if (Base58Flickr.canDecode(encoded)) {
      return Base58Flickr.create(encoded);
    }
    if (Base64WithPadding.canDecode(encoded)) {
      return Base64WithPadding.create(encoded);
    }
    if (Base64.canDecode(encoded)) {
      return Base64.create(encoded);
    }

    throw new IllegalArgumentException(
        "Encoded Multibase String is not supported. Must be Base64, Base64_WithPadding, Base58_Bitcoin or Base58_Flickr.");
  }
}
