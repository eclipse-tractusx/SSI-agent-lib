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

package org.eclipse.tractusx.ssi.examples;

import java.net.http.HttpClient;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidDocumentResolverRegistryImpl;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebDocumentResolver;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;

public class ResolveDIDDoc {
  public static DidDocument ResovleDocument(String didUrl)
      throws DidDocumentResolverNotRegisteredException {

    // DID Resolver Constracture params
    DidWebParser didParser = new DidWebParser();
    var httpClient = HttpClient.newHttpClient();
    var enforceHttps = false;

    // DID Method
    DidMethod didWeb = new DidMethod("web");

    // DID
    Did did = DidWebFactory.fromHostname(didUrl);

    var didDocumentResolverRegistry = new DidDocumentResolverRegistryImpl();
    didDocumentResolverRegistry.register(
        new DidWebDocumentResolver(httpClient, didParser, enforceHttps));
    return didDocumentResolverRegistry.get(didWeb).resolve(did);
  }
}
