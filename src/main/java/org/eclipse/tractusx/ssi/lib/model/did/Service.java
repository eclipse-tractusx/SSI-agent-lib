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

package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

@ToString
public class Service extends HashMap<String, Object> {
  /** The constant ID. */
  public static final String ID = "id";
  /** The constant TYPE. */
  public static final String TYPE = "type";
  /** The constant SERVICE_ENDPOINT. */
  public static final String SERVICE_ENDPOINT = "serviceEndpoint";

  /**
   * Instantiates a new Service.
   *
   * @param json the json
   */
  public Service(Map<String, Object> json) {
    super(json);

    try {
      // validate getters
      Objects.requireNonNull(this.getId(), "id is null");
      Objects.requireNonNull(this.getType(), "type is null");
      Objects.requireNonNull(this.getServiceEndpoint(), "serviceEndpoint is null");
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid Service: %s", SerializeUtil.toJson(json)), e);
    }
  }

  /**
   * Gets id.
   *
   * @return the id
   */
  public URI getId() {
    return SerializeUtil.asURI(this.get(ID));
  }

  /**
   * Gets type.
   *
   * @return the type
   */
  public String getType() {
    return (String) this.get(TYPE);
  }

  /**
   * Gets controller.
   *
   * @return the controller
   */
  public URI getServiceEndpoint() {
    return SerializeUtil.asURI(this.get(SERVICE_ENDPOINT));
  }
}
