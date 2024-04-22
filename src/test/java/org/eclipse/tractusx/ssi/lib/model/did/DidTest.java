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

package org.eclipse.tractusx.ssi.lib.model.did;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** The type Did test. */
class DidTest {

  public static final String MY_FRAGMENT = "myFragment";
  public static final String MY_KEY = "myKey";
  public static final String OTHER_KEY = "otherKey";
  public static final String TEST = "test";

  /** Test did equals. */
  @Test
  void testDidEquals() {

    Did did1 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), MY_FRAGMENT);
    Did did2 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), MY_FRAGMENT);

    Assertions.assertEquals(did1, did2);
  }

  @Test
  void testDidNotEqualsIdentifier() {

    Did did1 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), MY_FRAGMENT);
    Did did2 = new Did(new DidMethod(TEST), new DidMethodIdentifier(OTHER_KEY), MY_FRAGMENT);

    Assertions.assertNotEquals(did1, did2);
  }

  @Test
  void testDidEqualsConvenienceConstructor() {

    Did did1 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY));
    Did did2 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY));

    Assertions.assertEquals(did1, did2);
  }

  @Test
  void testDidNotEqualsFragment() {

    Did did1 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), MY_FRAGMENT);
    Did did2 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), "otherFragment");

    Assertions.assertNotEquals(did1, did2);
  }

  @Test
  void testDidEqualsFragmentNull() {

    Did did1 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), null);
    Did did2 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), null);

    Assertions.assertEquals(did1, did2);
  }

  @Test
  void testDidEqualsFragmentBlank() {

    Did did1 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), "");
    Did did2 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), "");

    Assertions.assertEquals(did1, did2);
  }

  @Test
  void testDidEqualsFragmentNullBlank() {

    Did did1 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), null);
    Did did2 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), "");

    Assertions.assertEquals(did1, did2);
  }

  @Test
  void testDidEqualsNoFragment() {

    Did did1 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY));
    Did did2 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY));

    Assertions.assertEquals(did1, did2);
  }

  @Test
  void testDidNotEqualsFragmentNull() {

    Did did1 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY), MY_FRAGMENT);
    Did did2 = new Did(new DidMethod(TEST), new DidMethodIdentifier(MY_KEY));

    Assertions.assertNotEquals(did1, did2);
  }
}
