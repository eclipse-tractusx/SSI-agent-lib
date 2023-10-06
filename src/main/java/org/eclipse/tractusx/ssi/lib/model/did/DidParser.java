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

package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.eclipse.tractusx.ssi.lib.exception.DidParseException;

public class DidParser {

  public static Did parse(URI uri) {
    Objects.requireNonNull(uri);

    if (!uri.getScheme().equals("did"))
      throw new DidParseException("URI is not a DID. URI: '" + uri + "'");
    var parts = uri.toString().split("#");
    var beforeFragment = parts[0];
    var fragment = "";

    if (parts.length > 1) fragment = parts[1];

    var did = beforeFragment.split(":");
    if (did.length < 3) {
      throw new DidParseException(
          "DID does not contain at least three parts split by ':'. URI: '" + did + "'");
    }

    List<String> methodIdentifierParts = Arrays.stream(did).skip(2).collect(Collectors.toList());

    return new Did(
        new DidMethod(did[1]),
        new DidMethodIdentifier(String.join(":", methodIdentifierParts)),
        fragment);
  }

  public static Did parse(String did) {
    Objects.requireNonNull(did);

    final URI uri;
    try {
      uri = URI.create(did);
    } catch (Exception e) {
      throw new DidParseException("Not able to create DID URI from string: " + did, e);
    }

    return parse(uri);
  }
}
