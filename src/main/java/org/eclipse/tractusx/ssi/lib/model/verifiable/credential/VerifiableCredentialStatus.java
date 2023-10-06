/*
 * *******************************************************************************
 *  * Copyright (c) 2021,2023 Contributors to the Eclipse Foundation
 *  *
 *  * See the NOTICE file(s) distributed with this work for additional
 *  * information regarding copyright ownership.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  *******************************************************************************
 */

package org.eclipse.tractusx.ssi.lib.model.verifiable.credential;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

/**
 * The type Verifiable credential status. ref.<a
 * href="https://www.w3.org/TR/vc-data-model/#status">...</a>
 */
public abstract class VerifiableCredentialStatus extends LinkedHashMap<String, Object> {

  /** The constant ID. */
  public static final String ID = "id";

  /** The constant TYPE. */
  public static final String TYPE = "type";

  /**
   * Instantiates a new Verifiable credential status.
   *
   * @param json the json
   */
  protected VerifiableCredentialStatus(Map<String, Object> json) {
    super(json);
    try {
      // validate
      getId();
      getType();
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid VerifiableCredentialStatus: %s", SerializeUtil.toJson(json)), e);
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
   * Convert credential status into json.
   *
   * @return the string
   */
  public String toJson() {
    return SerializeUtil.toJson(this);
  }

  /**
   * Get status type.
   *
   * @return the type
   */
  public abstract String getType();
}
