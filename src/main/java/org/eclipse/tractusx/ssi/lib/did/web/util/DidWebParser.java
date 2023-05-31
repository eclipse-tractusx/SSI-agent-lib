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

package org.eclipse.tractusx.ssi.lib.did.web.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.exception.DidParseException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;

@RequiredArgsConstructor
public class DidWebParser {

  private static final String PATH_WELL_KNOWN_DID_JSON = "/.well-known/did.json";
  private static final String PATH_DID_JSON = "/did.json";

  public URI parse(Did did) {
    return parse(did, true);
  }

  @SneakyThrows({URISyntaxException.class})
  public URI parse(Did did, boolean enforceHttps) {
    if (!did.getMethod().equals(Constants.DID_WEB_METHOD)) {
      throw new DidParseException(
          "Did Method not allowed: " + did.getMethod() + ". Expected did:web");
    }

    String didUrl = did.getMethodIdentifier().getValue();
    String[] didUrlArray = didUrl.split(":");
    didUrl = String.join("/", didUrlArray);
    didUrl = java.net.URLDecoder.decode(didUrl, StandardCharsets.UTF_8);

    if (enforceHttps) didUrl = "https://" + didUrl;
    else didUrl = "http://" + didUrl;

    boolean pathPartOfDid = didUrlArray.length > 1;
    if (pathPartOfDid) didUrl = didUrl + PATH_DID_JSON;
    else didUrl = didUrl + PATH_WELL_KNOWN_DID_JSON;

    return new URI(didUrl);
  }
}
