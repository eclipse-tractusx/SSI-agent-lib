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
package org.eclipse.tractusx.ssi.lib.did.resolver;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;

/**
 * Adapter of the {@link DidResolver} interface to a <a href=
 * "https://github.com/decentralized-identity/universal-resolver/">Universal Resolver</a> service
 * (deployed separately). <br>
 * The resolver must be initialized with the appropriate root endpoint (e.g.
 * <em>http://localhost:8080</em>)
 */
public class DidUniResolver implements DidResolver {
  private final HttpClient client;
  private final URI uniResolverEndpoint;
  private static final String uniResolverResolvePath = "./1.0/identifiers/";

  public DidUniResolver(HttpClient client, String uniResolverEndpoint)
      throws MalformedURLException, URISyntaxException {
    this(client, URI.create(uniResolverEndpoint));
  }

  public DidUniResolver(HttpClient client, URI uniResolverEndpoint)
      throws MalformedURLException, URISyntaxException {
    this.client = client;
    this.uniResolverEndpoint = new URL(uniResolverEndpoint.toURL(), "/").toURI();
  }

  @Override
  public DidDocument resolve(Did did) throws DidResolverException {
    URI requestUri =
        uniResolverEndpoint.resolve(uniResolverResolvePath).resolve("./" + did.toString());
    final HttpRequest request = HttpRequest.newBuilder().uri(requestUri).GET().build();

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

      return DidDocument.fromJson(response.body());
    } catch (DidResolverException e) {
      throw e;
    } catch (Exception e) {
      throw new DidResolverException(
          String.format("Unexpected exception: %s", e.getClass().getName()), e);
    }
  }

  /**
   * Always returns {@code true} since there is no easy way to determine which drivers are present
   * in the universal resolver.
   *
   * @param did a valid Decentralized Identifier (according to <a
   *     href="https://www.w3.org/TR/did-core/">W3C DID specification</a>)
   * @return <em>true</em>
   */
  @Override
  public boolean isResolvable(Did did) {
    return true;
  }
}
