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

package org.eclipse.tractusx.ssi.lib.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.tractusx.ssi.lib.util.SerializeUtil;

public class JsonLdObject extends HashMap<String, Object> {

  public static final String CONTEXT = "@context";

  public JsonLdObject(Map<String, Object> json) {
    super(json);

    try {
      // validate getters
      Objects.requireNonNull(this.getContext());
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid JsonLdObject: %s", SerializeUtil.toJson(json)), e);
    }
  }

  public List<String> getContext() {
    final Object context = this.get(CONTEXT);
    if (context instanceof String) {
      return List.of((String) context);
    }
    if (context instanceof List) {
      return (List<String>) context;
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Context must be of type string or list. Context Type: %s",
              context.getClass().getName()));
    }
  }
}
