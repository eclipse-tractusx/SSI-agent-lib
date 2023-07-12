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

import java.net.URI;
import java.util.*;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class JsonLdObject extends HashMap<String, Object> {

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

  public List<URI> getContext() {
    final Object context = this.get(CONTEXT);
    if (context instanceof String || context instanceof URI) {
      return List.of(SerializeUtil.asURI(context));
    } else if (context instanceof List) {
      final List<URI> contexts = new ArrayList<>();
      for (Object o : (List<?>) context) {
        if (o instanceof String || o instanceof URI) {
          contexts.add(SerializeUtil.asURI(o));
        } else {
          throw new IllegalArgumentException(
              String.format(
                  "Context must be of type string or URI. Context Type: %s",
                  context.getClass().getName()));
        }
      }
      return contexts;
    } else {
      throw new IllegalArgumentException(
          String.format(
              "Context must be of type string or list. Context Type: %s",
              context.getClass().getName()));
    }
  }

  public String toJson() {
    return SerializeUtil.toJson(this);
  }

  public String toPrettyJson() {
    return SerializeUtil.toPrettyJson(this);
  }
}
