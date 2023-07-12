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

package org.eclipse.tractusx.ssi.lib.model.verifiable.presentation;

import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VerifiablePresentationTest {

  @Test
  public void equalsSuccess() {
    final VerifiablePresentation vp1 = TestResourceUtil.getAlumniVerifiablePresentation();
    final VerifiablePresentation vp2 = TestResourceUtil.getAlumniVerifiablePresentation();

    Assertions.assertEquals(vp1, vp2);
  }

  @Test
  public void equalsFailure() {
    final VerifiablePresentation vp1 = TestResourceUtil.getAlumniVerifiablePresentation();
    final VerifiablePresentation vp2 = TestResourceUtil.getAlumniVerifiablePresentation();
    vp2.put(VerifiablePresentation.ID, "foo");

    Assertions.assertNotEquals(vp1, vp2);
  }
}
