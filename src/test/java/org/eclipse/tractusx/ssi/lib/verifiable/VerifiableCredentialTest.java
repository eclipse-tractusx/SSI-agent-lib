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

package org.eclipse.tractusx.ssi.lib.verifiable;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** The type Verifiable credential test. */
class VerifiableCredentialTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  /**
   * Can serialize vc.
   *
   * @throws JsonMappingException the json mapping exception
   * @throws JsonProcessingException the json processing exception
   */
  @Test
  @SneakyThrows
  void canSerializeVC() {
    final Map<String, Object> vpFromMap = TestResourceUtil.getAlumniVerifiableCredential();
    var vp = new VerifiableCredential(vpFromMap);
    var json = vp.toJson();
    var mapFromJson = MAPPER.readValue(json, Map.class);
    Assertions.assertEquals(
        mapFromJson.get(VerifiableCredential.ISSUER), vp.get(VerifiableCredential.ISSUER));
  }

  /** Should load cached context. */
  @Test
  void shouldLoadCachedContext() {
    var vcFromMap = TestResourceUtil.getAlumniVerifiableCredential();
    var vc = new VerifiableCredential(vcFromMap);

    var transform = new LinkedDataTransformer();
    Assertions.assertDoesNotThrow(
        () -> {
          transform.transform(vc);
        });
  }
}
