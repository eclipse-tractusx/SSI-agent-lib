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

package org.eclipse.tractusx.ssi.lib.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialStatus;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialStatusList2021Entry;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;

/** The type Serialize util. */
public final class SerializeUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private SerializeUtil() {
    throw new IllegalStateException("Utility class");
  }

  static {
    OBJECT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  /**
   * Specify oder of properties while creating JSON string from object like VerifiableCredential,
   * DidDocument etc.
   */
  public static final Map<Class<?>, List<String>> ORDER_MAP_LIST =
      Map.of(
          VerifiableCredential.class,
          List.of(
              JsonLdObject.CONTEXT,
              Verifiable.ID,
              Verifiable.TYPE,
              VerifiableCredential.CREDENTIAL_SCHEMA,
              VerifiableCredential.ISSUER,
              VerifiableCredential.ISSUANCE_DATE,
              VerifiableCredential.REFERENCE_NUMBER,
              VerifiableCredential.EXPIRATION_DATE,
              VerifiableCredential.CREDENTIAL_SUBJECT,
              VerifiableCredential.EVIDENCE,
              VerifiableCredential.TERMS_OF_USE,
              VerifiableCredential.REFRESH_SERVICE,
              VerifiableCredential.CREDENTIAL_STATUS,
              Verifiable.PROOF),
          DidDocument.class,
          List.of(
              JsonLdObject.CONTEXT,
              DidDocument.ID,
              DidDocument.SERVICE,
              DidDocument.VERIFICATION_METHOD,
              DidDocument.AUTHENTICATION),
          VerifiableCredentialStatusList2021Entry.class,
          List.of(
              VerifiableCredentialStatus.ID,
              VerifiableCredentialStatus.TYPE,
              VerifiableCredentialStatusList2021Entry.STATUS_PURPOSE,
              VerifiableCredentialStatusList2021Entry.STATUS_LIST_INDEX,
              VerifiableCredentialStatusList2021Entry.STATUS_LIST_CREDENTIAL),
          VerifiablePresentation.class,
          List.of(
              JsonLdObject.CONTEXT,
              Verifiable.ID,
              Verifiable.TYPE,
              VerifiablePresentation.VERIFIABLE_CREDENTIAL,
              Verifiable.PROOF));

  /**
   * To json string.
   *
   * @param map the map
   * @return the string
   */
  @SneakyThrows
  public static String toJson(Map<String, Object> map) {
    return OBJECT_MAPPER.writeValueAsString(getLinkedHashMap(map));
  }

  /**
   * To pretty json string.
   *
   * @param map the map
   * @return the string
   */
  @SneakyThrows
  public static String toPrettyJson(Map<String, Object> map) {
    return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(getLinkedHashMap(map));
  }

  /**
   * From json map.
   *
   * @param json the json
   * @return the map
   */
  @SneakyThrows
  public static Map<String, Object> fromJson(String json) {
    return OBJECT_MAPPER.readValue(json, Map.class);
  }

  /**
   * Sometimes SSI uri is serialized as string, sometimes as URI. If it starts with 'http://' it is
   * handled as URI, if it starts with 'did:method' it is handled as string.
   *
   * @param object string or URI
   * @return URI uri
   */
  public static URI asURI(Object object) {
    if (object instanceof URI uri) {
      return uri;
    }
    if (object instanceof String uriString) {
      return URI.create(uriString);
    }
    throw new IllegalArgumentException("Unsupported type: " + object.getClass());
  }

  /**
   * As string list.
   *
   * @param object the object
   * @return the list
   */
  public static List<String> asStringList(Object object) {
    if (object instanceof List<?> rawList) {
      return rawList.stream().map(String.class::cast).toList();
    }
    if (object instanceof String string) {
      return List.of(string);
    }
    throw new IllegalArgumentException("Unsupported type: " + object.getClass());
  }

  /**
   * As list.
   *
   * @param object the object
   * @return the list
   */
  public static <T> List<T> asList(Object object) {
    if (object instanceof List<?> rawList) {
      return rawList.stream().map(obj -> (T) obj).toList();
    } else {
      T typed = (T) object;
      return List.of(typed);
    }
  }

  private static Map<String, Object> getLinkedHashMap(Map<String, Object> map) {
    // convert Map to LinkedHashMap to preserve property order
    Class<? extends Map> aClass = map.getClass();
    if (ORDER_MAP_LIST.containsKey(aClass)) {
      Map<String, Object> linkedHashMap = new LinkedHashMap<>(map.size());
      ORDER_MAP_LIST
          .get(aClass)
          .forEach(
              string -> {
                if (map.containsKey(string)) {
                  linkedHashMap.put(string, map.get(string));
                }
              });
      // add other keys
      map.forEach(linkedHashMap::putIfAbsent);
      return linkedHashMap;
    } else {
      return map;
    }
  }
}
