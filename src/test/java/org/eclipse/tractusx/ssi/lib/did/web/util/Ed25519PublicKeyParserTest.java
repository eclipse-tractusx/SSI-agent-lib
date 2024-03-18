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

package org.eclipse.tractusx.ssi.lib.did.web.util;

import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.junit.jupiter.api.Test;

class Ed25519PublicKeyParserTest {

  @Test
  void shouldParseString() {
    String keyString =
        "-----BEGIN PUBLIC KEY-----\n"
            + "MCowBQYDK2VwAyEABqAmUe/amV/nAVUt01XyrLpmQLOyLqF6LnAkH4QdyqI=\n"
            + "-----END PUBLIC KEY-----";

    MultibaseString multibaseString =
        assertDoesNotThrow(() -> Ed25519PublicKeyParser.parsePublicKey(keyString));
    assertNotNull(multibaseString);
  }
}
