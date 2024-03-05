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

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

/** The type Did document. Spec: <a href="https://www.w3.org/TR/did-core/">...</a> */
@ToString
public class DidDocument extends JsonLdObject {

  /** The constant DEFAULT_CONTEXT. */
  public static final String DEFAULT_CONTEXT = "https://www.w3.org/ns/did/v1";
  /** The constant ID. */
  public static final String ID = "id";
  /** The constant VERIFICATION_METHOD. */
  public static final String VERIFICATION_METHOD = "verificationMethod";
  /** The constant SERVICE. */
  public static final String SERVICE = "service";
  /** The constant AUTHENTICATION. */
  public static final String AUTHENTICATION = "authentication";

  /**
   * Instantiates a new Did document.
   *
   * @param json the json
   */
  public DidDocument(Map<String, Object> json) {
    super(json);

    try {
      // validate getters
      Objects.requireNonNull(getContext(), "context is null");
      Objects.requireNonNull(getId(), "id is null");
      Objects.requireNonNull(getVerificationMethods(), "verificationMethod is null");
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid DidDocument: %s", SerializeUtil.toJson(json)), e);
    }
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public URI getId() {
    return SerializeUtil.asURI(get(ID));
  }

  /**
   * Gets verification methods.
   *
   * @return the verification methods
   */
  public List<VerificationMethod> getVerificationMethods() {

    List<VerificationMethod> result = new ArrayList<>();

    Object verificationMethod = get(VERIFICATION_METHOD);
    if (verificationMethod instanceof Map) {
      result.add(new VerificationMethod((Map<String, Object>) verificationMethod));
    }
    if (verificationMethod instanceof List) {
      ((List<Map<String, Object>>) verificationMethod)
          .forEach(vm -> result.add(new VerificationMethod(vm)));
    }

    return result;
  }

  /**
   * Gets services.
   *
   * @return the services
   */
  public List<Service> getServices() {

    List<Service> result = new ArrayList<>();

    Object service = get(SERVICE);
    if (service instanceof Map) {
      result.add(new Service((Map<String, Object>) service));
    }
    if (service instanceof List) {
      ((List<Map<String, Object>>) service).forEach(s -> result.add(new Service(s)));
    }

    return result;
  }

  /**
   * From json did document.
   *
   * @param json the json
   * @return the did document
   */
  public static DidDocument fromJson(String json) {
    var map = SerializeUtil.fromJson(json);
    return new DidDocument(map);
  }
}
