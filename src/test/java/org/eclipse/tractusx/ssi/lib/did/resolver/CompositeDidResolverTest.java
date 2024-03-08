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

package org.eclipse.tractusx.ssi.lib.did.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.exception.did.DidParseException;
import org.eclipse.tractusx.ssi.lib.exception.did.DidResolverException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** The type Composite did resolver test. */
@ExtendWith(MockitoExtension.class)
class CompositeDidResolverTest {
  @Mock private DidResolver resolver1;
  @Mock private DidResolver resolver2;

  private static final DidDocument RESOLVED_DID_DOC =
      new DidDocument(TestResourceUtil.getPublishedDidDocument());
  private static final Did DID =
      new Did(new DidMethod("web"), new DidMethodIdentifier("localhost"), null);

  /** Reset mocks. */
  @BeforeEach
  public void resetMocks() {
    reset(resolver1);
    reset(resolver2);
  }

  /**
   * Should resolve with one resolver.
   *
   * @throws DidResolverException the did resolver exception
   */
  @Test
  @SneakyThrows
  void shouldResolveWithOneResolver() {
    when(resolver1.isResolvable(any())).thenReturn(true);
    when(resolver1.resolve(any())).thenReturn(Optional.of(RESOLVED_DID_DOC));
    CompositeDidResolver resolver = new CompositeDidResolver(resolver1);
    assertTrue(resolver.isResolvable(DID));
    assertEquals(Optional.of(RESOLVED_DID_DOC), resolver.resolve(DID));
  }

  /**
   * Should resolve with first resolver.
   *
   * @throws DidResolverException the did resolver exception
   */
  @Test
  @SneakyThrows
  void shouldResolveWithFirstResolver() {
    when(resolver1.isResolvable(any())).thenReturn(true);
    when(resolver1.resolve(any())).thenReturn(Optional.of(RESOLVED_DID_DOC));

    CompositeDidResolver resolver = new CompositeDidResolver(resolver1, resolver2);
    assertTrue(resolver.isResolvable(DID));
    assertEquals(RESOLVED_DID_DOC, resolver.resolve(DID).get());
    verify(resolver2, never()).isResolvable(any());
    verify(resolver2, never()).resolve(any());
  }

  /**
   * Should resolve with second resolver.
   *
   * @throws DidResolverException the did resolver exception
   */
  @Test
  @SneakyThrows
  void shouldResolveWithSecondResolver() {
    when(resolver1.isResolvable(any())).thenReturn(false);
    when(resolver2.isResolvable(any())).thenReturn(true);
    when(resolver2.resolve(any())).thenReturn(Optional.of(RESOLVED_DID_DOC));

    CompositeDidResolver resolver = new CompositeDidResolver(resolver1, resolver2);
    assertTrue(resolver.isResolvable(DID));
    assertEquals(RESOLVED_DID_DOC, resolver.resolve(DID).get());
    verify(resolver1, never()).resolve(any());
  }

  /**
   * Must not resolve on false.
   *
   * @throws DidResolverException the did resolver exception
   */
  @Test
  @SneakyThrows
  void mustNotResolveOnFalse() {
    when(resolver1.isResolvable(any())).thenReturn(false);
    when(resolver2.isResolvable(any())).thenReturn(false);

    CompositeDidResolver resolver = new CompositeDidResolver(resolver1, resolver2);
    assertFalse(resolver.isResolvable(DID));
    assertFalse(resolver.resolve(DID).isPresent());
    verify(resolver1, never()).resolve(any());
    verify(resolver2, never()).resolve(any());
  }

  /**
   * Must not resolve on exception.
   *
   * @throws DidResolverException the did resolver exception
   */
  @Test
  @SneakyThrows
  void mustNotResolveOnException() {
    when(resolver1.isResolvable(any())).thenReturn(true);
    when(resolver1.resolve(any())).thenThrow(DidResolverException.class);
    when(resolver1.isResolvable(any())).thenReturn(true);

    CompositeDidResolver resolver = new CompositeDidResolver(resolver1, resolver2);
    assertThrows(
        DidResolverException.class,
        () -> {
          resolver.resolve(DID);
        });
    verify(resolver1, times(1)).resolve(any());
    verify(resolver2, never()).resolve(any());
  }

  /**
   * Should construct composite resolver using static method.
   *
   * @throws DidResolverException the did resolver exception
   */
  @Test
  @SneakyThrows
  void shouldConstructCompositeResolverUsingStaticMethod() {
    when(resolver1.isResolvable(any())).thenReturn(true);
    when(resolver1.resolve(any())).thenReturn(Optional.of(RESOLVED_DID_DOC));

    DidResolver resolver = CompositeDidResolver.append(resolver1, resolver2);
    resolver.resolve(DID);
    verify(resolver1, times(1)).resolve(any());
    verify(resolver2, never()).resolve(any());

    when(resolver1.isResolvable(any())).thenReturn(false);
    when(resolver2.isResolvable(any())).thenReturn(true);
    when(resolver2.resolve(any())).thenReturn(Optional.of(RESOLVED_DID_DOC));

    resolver.resolve(DID);
    verify(resolver1, times(1)).resolve(any());
    verify(resolver2, times(1)).resolve(any());
  }

  @Test
  @SneakyThrows
  void shouldThrow() {
    when(resolver1.isResolvable(any())).thenReturn(true);
    when(resolver1.resolve(any())).thenThrow(new DidParseException("sdf"));

    CompositeDidResolver resolver = new CompositeDidResolver(resolver1);
    assertThrows(DidResolverException.class, () -> resolver.resolve(DID));
  }
}
