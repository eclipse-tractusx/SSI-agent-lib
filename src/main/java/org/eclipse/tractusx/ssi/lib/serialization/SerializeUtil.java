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

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialStatus;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialStatusList2021Entry;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;

/** The type Serialize util. */
public final class SerializeUtil {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Specify oder of properties while creating JSON string from object like VerifiableCredential,
   * DidDocument etc.
   */
  public static final Map<Class, List<String>> ORDER_MAP_LIST =
      Map.of(
          VerifiableCredential.class,
          List.of(
              JsonLdObject.CONTEXT,
              VerifiableCredential.ID,
              VerifiableCredential.TYPE,
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
              VerifiableCredential.PROOF),
          DidDocument.class,
          List.of(
              JsonLdObject.CONTEXT,
              DidDocument.ID,
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
              VerifiablePresentation.ID,
              VerifiablePresentation.TYPE,
              VerifiablePresentation.VERIFIABLE_CREDENTIAL,
              VerifiableCredential.PROOF));

  @SneakyThrows
  public static String toJson(Map<String, Object> map) {
    return OBJECT_MAPPER.writeValueAsString(getLinkedHashMap(map));
  }

  @SneakyThrows
  public static String toPrettyJson(Map<String, Object> map) {
    return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(getLinkedHashMap(map));
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

  public static List asList(Object object) {
    if (object instanceof List) {
      return (List) object;
    } else return List.of(object);
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
