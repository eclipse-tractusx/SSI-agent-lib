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

package org.eclipse.tractusx.ssi.lib.verifiable;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** The type Verifiable presentation test. */
class VerifiablePresentationTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  /** Can serialize vp. */
  @Test
  @SneakyThrows
  void canSerializeVP() {
    final Map<String, Object> vpFromMap = TestResourceUtil.getAlumniVerifiablePresentation();
    var vp = new VerifiablePresentation(vpFromMap);
    var json = vp.toJson();
    var mapFromJson = MAPPER.readValue(json, Map.class);
    Assertions.assertEquals(
        mapFromJson.get(VerifiablePresentation.VERIFIABLE_CREDENTIAL),
        vp.get(VerifiablePresentation.VERIFIABLE_CREDENTIAL));
  }

  /** Can serialize v pwith credential not as list. */
  @Test
  @SneakyThrows
  void canSerializeVPwithCredentialNotAsList() {
    final Map<String, Object> vpFromMap = TestResourceUtil.getAlumniVerifiablePresentation();
    var vp = new VerifiablePresentation(vpFromMap);
    vp.put("verifiableCredential", vp.getVerifiableCredentials().get(0));
    var json = vp.toJson();
    var mapFromJson = MAPPER.readValue(json, Map.class);
    Assertions.assertDoesNotThrow(() -> new VerifiablePresentation(mapFromJson));
  }
}
