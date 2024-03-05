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

package org.eclipse.tractusx.ssi.lib.base;

import java.nio.charset.StandardCharsets;
import org.eclipse.tractusx.ssi.lib.model.base.Base64WithPadding;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** The type Base 64 with padding test. */
public class Base64WithPaddingTest {

  private static final byte[] DECODED =
      "Multibase is awesome! \\o/".getBytes(StandardCharsets.UTF_8);
  private static final String ENCODED = "MTXVsdGliYXNlIGlzIGF3ZXNvbWUhIFxvLw==";

  /** Test encoding. */
  @Test
  public void testEncoding() {
    var multibase = Base64WithPadding.create(DECODED);
    Assertions.assertEquals(ENCODED, multibase.getEncoded());
  }

  /** Test decoding. */
  @Test
  public void testDecoding() {
    var multibase = Base64WithPadding.create(ENCODED);
    Assertions.assertEquals(new String(DECODED), new String(multibase.getDecoded()));
  }
}
