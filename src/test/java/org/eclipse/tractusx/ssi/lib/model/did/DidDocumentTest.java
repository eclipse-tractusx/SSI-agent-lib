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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DidDocumentTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  public void canCreateDidDocument() {
    final List<Map<String, Object>> documents = TestResourceUtil.getAllDidDocuments();

    for (Map<String, Object> document : documents) {
      Assertions.assertDoesNotThrow(() -> new DidDocument(document));
    }
  }

  @Test
  @SneakyThrows
  public void canSerializeDidDocument() {
    final List<Map<String, Object>> documents = TestResourceUtil.getAllDidDocuments();
    for (Map<String, Object> document : documents) {
      var doc = new DidDocument(document);
      var json = doc.toJson();
      var mapFromJson = MAPPER.readValue(json, Map.class);
      Assertions.assertEquals(mapFromJson.get(DidDocument.ID), doc.get(DidDocument.ID));
    }
  }

  @Test
  @SneakyThrows
  public void canDeserializeDidDocument() {
    final List<Map<String, Object>> documents = TestResourceUtil.getAllDidDocuments();
    for (Map<String, Object> document : documents) {
      var docFromMap = new DidDocument(document);
      var json = docFromMap.toJson();
      var docFromJson = DidDocument.fromJson(json);
      Assertions.assertEquals(docFromJson.get(DidDocument.ID), docFromMap.get(DidDocument.ID));
    }
  }
}
