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
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

/**
 * The Verifiable credential status list 2021 entry. ref. <a
 * href="https://www.w3.org/TR/vc-status-list/">...</a>
 */
public class VerifiableCredentialStatusList2021Entry extends VerifiableCredentialStatus {

  /** The constant STATUS_LIST_2021_ENTRY. */
  public static final String STATUS_LIST_2021_ENTRY = "StatusList2021Entry";

  /** The constant STATUS_PURPOSE. */
  public static final String STATUS_PURPOSE = "statusPurpose";

  /** The constant STATUS_LIST_INDEX. */
  public static final String STATUS_LIST_INDEX = "statusListIndex";

  /** The constant STATUS_LIST_CREDENTIAL. */
  public static final String STATUS_LIST_CREDENTIAL = "statusListCredential";

  /** The constant VALID_STATUS_PURPOSES. */
  public static final Set<String> VALID_STATUS_PURPOSES = Set.of("revocation", "suspension");

  /**
   * Instantiates a new Verifiable credential status list 2021 entry.
   *
   * @param json the json
   */
  public VerifiableCredentialStatusList2021Entry(Map<String, Object> json) {
    super(json);
    try {
      getStatusPurpose();
      getStatusListIndex();
      getStatusListCredential();
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid VerifiableCredentialStatus: %s", SerializeUtil.toJson(json)), e);
    }
  }

  /**
   * Gets status list credential.
   *
   * @return the status list credential
   */
  public URI getStatusListCredential() {
    Object id = get(STATUS_LIST_CREDENTIAL);
    return id == null ? null : SerializeUtil.asURI(id);
  }

  /**
   * Gets status list index.
   *
   * @return the status list index
   */
  public int getStatusListIndex() {
    Object object = get(STATUS_LIST_INDEX);
    if (Objects.isNull(object)) {
      throw new IllegalArgumentException("Status index list type not found");
    } else {
      int index = Integer.parseInt(object.toString());
      if (index < 0) {
        throw new IllegalArgumentException(
            "invalid index, index must be greater than or equal to 0");
      }
      return index;
    }
  }

  /**
   * Gets status purpose.
   *
   * @return the status purpose
   */
  public String getStatusPurpose() {
    Object object = get(STATUS_PURPOSE);
    if (Objects.isNull(object)) {
      throw new IllegalArgumentException("Status purpose type not found");
    } else {
      String statusPurpose = object.toString();
      if (!VALID_STATUS_PURPOSES.contains(statusPurpose)) {
        throw new IllegalArgumentException(statusPurpose + " is not supported");
      }
      return statusPurpose;
    }
  }

  @Override
  public String getType() {
    Object object = get(VerifiableCredentialStatus.TYPE);
    if (Objects.isNull(object)) {
      throw new IllegalArgumentException("Status type not found");
    } else {
      return object.toString();
    }
  }
}
