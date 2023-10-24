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
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolver;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolverException;
import org.eclipse.tractusx.ssi.lib.did.web.util.Constants;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;

@RequiredArgsConstructor
public class DidWebResolver implements DidResolver {

  private final HttpClient client;
  private final DidWebParser parser;
  private final boolean enforceHttps;

  @Override
  public boolean isResolvable(Did did) {
    return Constants.DID_WEB_METHOD.equals(did.getMethod());
  }

  @SuppressWarnings("unchecked")
  @Override
  public DidDocument resolve(Did did) throws DidResolverException {
    if (!did.getMethod().equals(Constants.DID_WEB_METHOD))
      throw new DidResolverException(
          String.format(
              "%s can only handle the following methods: %s",
              this.getClass().getSimpleName(), Constants.DID_WEB_METHOD));

    final URI uri = parser.parse(did, enforceHttps);

    final HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

    try {
      final HttpResponse<String> response =
          client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() < 200 || response.statusCode() > 299) {
        throw new DidResolverException(
            String.format(
                "Unexpected response when resolving did document [Code=%s, Payload=%s]",
                response.statusCode(), response.body()));
      }
      if (response.body() == null) {
        throw new DidResolverException("Empty response body");
      }

      final byte[] body = response.body().getBytes(StandardCharsets.UTF_8);

      final ObjectMapper mapper = new ObjectMapper();
      final Map<String, Object> json = mapper.readValue(body, Map.class);

      return new DidDocument(json);
    } catch (Exception e) {
      throw new DidResolverException(
          String.format("Unexpected exception: %s", e.getClass().getName()), e);
    }
  }
}
