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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.http.HttpClient;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolverException;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.NginxContainer;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public class DidWebResolverIT {
  private DidWebResolver resolver;
  private DidWebResolver httpsResolver;

  @Container
  public static NginxContainer<?> nginx =
      new NginxContainer<>("nginx")
          .withCopyFileToContainer(
              MountableFile.forHostPath("./src/test/resources/nginx-testcontainers"),
              "/usr/share/nginx/html")
          .waitingFor(new HttpWaitStrategy());

  @BeforeEach
  public void initEach() {
    resolver = new DidWebResolver(HttpClient.newHttpClient(), new DidWebParser(), false);
    httpsResolver = new DidWebResolver(HttpClient.newHttpClient(), new DidWebParser(), true);
  }

  @Test
  public void shouldResolveValidWebDid() throws DidResolverException {
    Did validDidWeb =
        new Did(
            new DidMethod("web"),
            new DidMethodIdentifier(nginx.getHost() + "%3A" + nginx.getFirstMappedPort()),
            null);
    assertTrue(resolver.isResolvable(validDidWeb));
    DidDocument actualDidDoc = resolver.resolve(validDidWeb);
    assertEquals(new DidDocument(TestResourceUtil.getPublishedDidDocument()), actualDidDoc);
  }

  @Test
  public void shouldResolveValidExternalWebDid() throws DidResolverException {
    final String didIdentifier = "did.actor:alice";
    Did validDidWeb = new Did(new DidMethod("web"), new DidMethodIdentifier(didIdentifier), null);
    assertTrue(httpsResolver.isResolvable(validDidWeb));
    DidDocument actualDidDoc = httpsResolver.resolve(validDidWeb);
    assertEquals("did:web:" + didIdentifier, actualDidDoc.get("id"));
  }
}
