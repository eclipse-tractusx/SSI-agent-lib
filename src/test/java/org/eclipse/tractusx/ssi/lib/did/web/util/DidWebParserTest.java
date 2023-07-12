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
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class DidWebParserTest {

  private final DidWebParser parser = new DidWebParser();

  @ParameterizedTest
  @CsvSource({
    "localhost, https://localhost/.well-known/did.json",
    "localhost%3A8080, https://localhost:8080/.well-known/did.json",
    "some-host, https://some-host/.well-known/did.json",
    "some-host:path, https://some-host/path/did.json",
    "some-host:path1:path2, https://some-host/path1/path2/did.json",
    "some-host%3A9090:path1:path2, https://some-host:9090/path1/path2/did.json"
  })
  public void testResolveUriFromDid(String methodIdentifier, String expectedUri) {

    final Did did = new Did(new DidMethod("web"), new DidMethodIdentifier(methodIdentifier));
    final URI uri = parser.parse(did);

    Assertions.assertEquals(expectedUri, uri.toString(), "Could not resolve URI from DID" + did);
  }
}
