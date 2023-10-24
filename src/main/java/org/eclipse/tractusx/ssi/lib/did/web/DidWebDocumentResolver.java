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

package org.eclipse.tractusx.ssi.lib.did.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.did.web.util.Constants;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.DidWebException;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;

@RequiredArgsConstructor
@Deprecated
/**
 * @deprecated replaced by {@link DidWebResolver}
 */
public class DidWebDocumentResolver implements DidDocumentResolver {

  private final HttpClient client;
  private final DidWebParser parser;
  private final boolean enforceHttps;

  @Override
  public DidMethod getSupportedMethod() {
    return Constants.DID_WEB_METHOD;
  }

  @Override
  public DidDocument resolve(Did did) {
    if (!did.getMethod().equals(Constants.DID_WEB_METHOD))
      throw new SsiException(
          "Handler can only handle the following methods:" + Constants.DID_WEB_METHOD);

    final URI uri = parser.parse(did, enforceHttps);

    final HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

    try {
      final HttpResponse<String> response =
          client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() < 200 || response.statusCode() > 299) {
        throw new DidWebException(
            String.format(
                "Unexpected response when resolving did document [Code=%s, Payload=%s]",
                response.statusCode(), response.body()));
      }
      if (response.body() == null) {
        throw new DidWebException("Empty response body");
      }

      final byte[] body = response.body().getBytes(StandardCharsets.UTF_8);

      // TODO Fix this
      final ObjectMapper mapper = new ObjectMapper();
      final Map<String, Object> json = mapper.readValue(body, Map.class);

      return new DidDocument(json);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
