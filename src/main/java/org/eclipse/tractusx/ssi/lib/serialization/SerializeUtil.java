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

package org.eclipse.tractusx.ssi.lib.serialization;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.URI;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;

public final class SerializeUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @SneakyThrows
  public static String toJson(Map<String, Object> map) {
    return OBJECT_MAPPER
        .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        .writeValueAsString(map);
  }

  @SneakyThrows
  public static String toPrettyJson(Map<String, Object> map) {
    return OBJECT_MAPPER
        .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
        .configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true)
        .writerWithDefaultPrettyPrinter()
        .writeValueAsString(map);
  }

  @SneakyThrows
  public static Map<String, Object> fromJson(String json) {
    return OBJECT_MAPPER.readValue(json, Map.class);
  }

  /**
   * Sometimes SSI uri is serialized as string, sometimes as URI. If it starts with 'http://' it is
   * handled as URI, if it starts with 'did:<method>' it is handled as string.
   *
   * @param object string or URI
   * @return URI
   */
  public static URI asURI(Object object) {
    if (object instanceof URI) {
      return (URI) object;
    }
    if (object instanceof String) {
      return URI.create((String) object);
    }
    throw new IllegalArgumentException("Unsupported type: " + object.getClass());
  }

  public static List<String> asStringList(Object object) {
    if (object instanceof List) {
      return (List<String>) object;
    }
    if (object instanceof String) {
      return List.of((String) object);
    }
    throw new IllegalArgumentException("Unsupported type: " + object.getClass());
  }
}
