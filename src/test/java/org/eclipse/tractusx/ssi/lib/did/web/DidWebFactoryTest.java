/*
 * ******************************************************************************
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
 * *******************************************************************************
 */

package org.eclipse.tractusx.ssi.lib.did.web;

import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/** The type Did web factory test. */
class DidWebFactoryTest {

  /**
   * Test create did from hostname.
   *
   * @param hostname the hostname
   * @param expectedDid the expected did
   */
  @ParameterizedTest
  @CsvSource({
    "localhost, did:web:localhost",
    "localhost:8080, did:web:localhost%3A8080",
    "some-host, did:web:some-host",
  })
  void testCreateDidFromHostname(String hostname, String expectedDid) {
    final Did did = DidWebFactory.fromHostname(hostname);

    Assertions.assertEquals(expectedDid, did.toString());
  }

  /**
   * Test create did from hostname and path.
   *
   * @param hostname the hostname
   * @param path the path
   * @param expectedDid the expected did
   */
  @ParameterizedTest
  @CsvSource({
    "some-host, path, did:web:some-host:path",
    "some-host, path/, did:web:some-host:path",
    "some-host, /path, did:web:some-host:path",
    "some-host, /path1/path2, did:web:some-host:path1:path2",
    "some-host:9090, path1/path2, did:web:some-host%3A9090:path1:path2",
  })
  void testCreateDidFromHostnameAndPath(String hostname, String path, String expectedDid) {
    final Did did = DidWebFactory.fromHostnameAndPath(hostname, path);

    Assertions.assertEquals(expectedDid, did.toString());
  }
}
