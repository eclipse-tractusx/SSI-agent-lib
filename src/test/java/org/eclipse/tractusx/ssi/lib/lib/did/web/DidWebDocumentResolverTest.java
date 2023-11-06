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

package org.eclipse.tractusx.ssi.lib.lib.did.web;

import java.net.ServerSocket;
import java.net.http.HttpClient;
import java.time.Duration;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebDocumentResolver;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DidWebDocumentResolverTest {

  @Test
  @SneakyThrows
  // When the MIW tries to resolve a DID document that is not protected by TLS, but the DID resolve
  // is configured to
  // enforce HTTPS, the MIW will hang without timeout.
  // This test is to ensure that the MIW does not hang anymore when trying to resolve a DID document
  // that is not protected by TLS.
  public void testTimeout() {

    final HttpClient httpClient = HttpClient.newHttpClient();
    final DidWebParser didParser = new DidWebParser();
    final boolean enforceHttps = true;
    final Duration timeout = Duration.ofMillis(100);
    final DidWebDocumentResolver didWebDocumentResolver =
        new DidWebDocumentResolver(httpClient, didParser, enforceHttps, timeout);

    // open a local port to create an endpoint that is not protected by TLS
    try (var ss = new ServerSocket(0)) {
      final int port = ss.getLocalPort();
      final Did did = DidParser.parse("did:web:localhost%3A" + port);
      Assertions.assertThrows(RuntimeException.class, () -> didWebDocumentResolver.resolve(did));
    }
  }
}
