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

package org.eclipse.tractusx.ssi.examples;

import java.net.http.HttpClient;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebResolver;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.did.DidParseException;
import org.eclipse.tractusx.ssi.lib.exception.did.DidResolverException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;

/** This is an example class to demonstrate did document resolve from given did web url */
public class ResolveDIDDoc {

  private ResolveDIDDoc() {
    // static
  }

  /**
   * Resolve did document.
   *
   * @param didUrl the did url
   * @return the did document
   * @throws DidResolverException when no document found or retrieving failed
   * @throws DidParseException exception
   */
  public static DidDocument resovleDocument(String didUrl)
      throws DidParseException, DidResolverException {

    // DID Resolver Constracture params
    DidWebParser didParser = new DidWebParser();
    var httpClient = HttpClient.newHttpClient();
    var enforceHttps = false;

    // DID
    Did did = DidWebFactory.fromHostname(didUrl);

    var didResolver = new DidWebResolver(httpClient, didParser, enforceHttps);

    return didResolver
        .resolve(did)
        .orElseThrow(
            () -> new DidParseException(String.format("no did document found for %s", didUrl)));
  }
}
