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

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.NginxContainer;
import org.testcontainers.containers.wait.strategy.HostPortWaitStrategy;
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public class DidUniResolverIT {
  private DidUniResolver resolver;

  @Container
  public static NginxContainer<?> nginx =
      new NginxContainer<>("nginx")
          .withCopyFileToContainer(
              MountableFile.forHostPath("./src/test/resources/nginx-testcontainers"),
              "/usr/share/nginx/html")
          .waitingFor(new HttpWaitStrategy());

  /**
   * Actually this is only a driver for the Universal Resolver but as it handles request the same
   * way we can use it instead for this test. <br>
   * However, it support 'did:key' only!
   */
  @Container
  public static GenericContainer<?> uniResolver =
      new GenericContainer<>("universalresolver/driver-did-key:latest")
          .withExposedPorts(8080)
          .waitingFor(new HostPortWaitStrategy());

  @BeforeEach
  public void initEach() throws MalformedURLException, URISyntaxException {
    resolver =
        new DidUniResolver(
            HttpClient.newHttpClient(),
            "http://" + uniResolver.getHost() + ":" + uniResolver.getFirstMappedPort());
  }

  @Test
  public void shouldResolveValidDid() throws DidResolverException {
    Did validDidWeb =
        new Did(
            new DidMethod("key"),
            new DidMethodIdentifier("z6Mkfriq1MqLBoPWecGoDLjguo1sB9brj6wT3qZ5BxkKpuP6"),
            null);
    assertTrue(resolver.isResolvable(validDidWeb));
    DidDocument actualDidDoc = resolver.resolve(validDidWeb);
    assertNotNull(actualDidDoc);
  }
}
