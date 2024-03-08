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

package org.eclipse.tractusx.ssi.lib.did.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Optional;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.did.DidResolverException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** The type Did web resolver test. */
@ExtendWith(MockitoExtension.class)
class DidWebResolverTest {

  @Mock private HttpClient httpClient;
  @Mock private HttpResponse<Object> response;
  @Mock private DidWebParser parser;

  private DidWebResolver resolver;

  /** Init each. */
  @BeforeEach
  public void initEach() {
    resolver = new DidWebResolver(httpClient, parser, false);
  }

  /**
   * Should resolve valid web did.
   *
   * @throws DidResolverException the did resolver exception
   * @throws IOException the io exception
   * @throws InterruptedException the interrupted exception
   * @throws URISyntaxException the uri syntax exception
   */
  @Test
  @SneakyThrows
  void shouldResolveValidWebDid() {
    Did validDidWeb = new Did(new DidMethod("web"), new DidMethodIdentifier("localhost"), null);
    assertTrue(resolver.isResolvable(validDidWeb));
    when(httpClient.send(any(), any())).thenReturn(response);
    when(response.statusCode()).thenReturn(200);
    when(response.body()).thenReturn(TestResourceUtil.getPublishedDidDocumentAsString());
    when(parser.parse(any(), anyBoolean())).thenReturn(new URI("http://dummy.net/did.json"));

    Optional<DidDocument> actualDidDoc = resolver.resolve(validDidWeb);
    assertEquals(new DidDocument(TestResourceUtil.getPublishedDidDocument()), actualDidDoc.get());
  }

  /**
   * Should not resolve non web did.
   *
   * @throws DidResolverException the did resolver exception
   */
  @Test
  @SneakyThrows
  void shouldNotResolveNonWebDid() {
    Did validDidKey =
        new Did(
            new DidMethod("key"),
            new DidMethodIdentifier("z6Mkfriq1MqLBoPWecGoDLjguo1sB9brj6wT3qZ5BxkKpuP6"),
            null);
    assertFalse(resolver.isResolvable(validDidKey));
    assertThrows(
        DidResolverException.class,
        () -> {
          resolver.resolve(validDidKey);
        });
  }

  @ParameterizedTest
  @ValueSource(ints = {199, 300})
  @SneakyThrows
  void shouldThrowWhenStatusCodeIsNot2XX(int code) {
    Did validDidWeb = new Did(new DidMethod("web"), new DidMethodIdentifier("localhost"), null);
    assertTrue(resolver.isResolvable(validDidWeb));
    when(httpClient.send(any(), any())).thenReturn(response);
    when(response.statusCode()).thenReturn(code);
    when(parser.parse(any(), anyBoolean())).thenReturn(new URI("http://dummy.net/did.json"));

    assertThrows(DidResolverException.class, () -> resolver.resolve(validDidWeb));
  }

  @Test
  @SneakyThrows
  void shouldResolveWhenBodyIsNull() {
    Did validDidWeb = new Did(new DidMethod("web"), new DidMethodIdentifier("localhost"), null);
    assertTrue(resolver.isResolvable(validDidWeb));
    when(httpClient.send(any(), any())).thenReturn(response);
    when(response.statusCode()).thenReturn(200);
    when(response.body()).thenReturn(null);
    when(parser.parse(any(), anyBoolean())).thenReturn(new URI("http://dummy.net/did.json"));

    assertThrows(DidResolverException.class, () -> resolver.resolve(validDidWeb));
  }

  @Test
  @SneakyThrows
  void shouldResolveWhenInterrupted() {
    Did validDidWeb = new Did(new DidMethod("web"), new DidMethodIdentifier("localhost"), null);
    assertTrue(resolver.isResolvable(validDidWeb));
    when(parser.parse(any(), anyBoolean())).thenReturn(new URI("http://dummy.net/did.json"));
    doThrow(new InterruptedException()).when(httpClient).send(any(), any());
    assertThrows(DidResolverException.class, () -> resolver.resolve(validDidWeb));
  }

  @Test
  @SneakyThrows
  void shouldResolveWhenIOException() {
    Did validDidWeb = new Did(new DidMethod("web"), new DidMethodIdentifier("localhost"), null);
    assertTrue(resolver.isResolvable(validDidWeb));
    when(parser.parse(any(), anyBoolean())).thenReturn(new URI("http://dummy.net/did.json"));
    doThrow(new IOException()).when(httpClient).send(any(), any());
    assertThrows(DidResolverException.class, () -> resolver.resolve(validDidWeb));
  }
}
