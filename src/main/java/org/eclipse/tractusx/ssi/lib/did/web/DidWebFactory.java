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

package org.eclipse.tractusx.ssi.lib.did.web;

import java.util.Objects;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;

public class DidWebFactory {

  public static Did fromHostname(String hostName) {
    return fromHostnameAndPath(hostName, "");
  }

  public static Did fromHostnameAndPath(String hostName, String path) {
    Objects.requireNonNull(hostName, "Hostname must not be null");
    Objects.requireNonNull(path, "Path must not be null");

    if (hostName.startsWith("http"))
      throw new IllegalArgumentException("Hostname should not contain http(s)://");

    String cleanedPath = path;
    if (!cleanedPath.startsWith("/")) {
      cleanedPath = "/" + cleanedPath;
    }
    if (cleanedPath.endsWith("/")) {
      cleanedPath = cleanedPath.substring(0, cleanedPath.length() - 1);
    }

    if (hostName.startsWith("http"))
      throw new IllegalArgumentException("Hostname should not contain http(s)://");

    final DidMethod didMethod = new DidMethod("web");
    final DidMethodIdentifier methodIdentifier =
        new DidMethodIdentifier(
            hostName.concat(cleanedPath).replaceAll(":", "%3A").replaceAll("/", ":"));

    return new Did(didMethod, methodIdentifier, null);
  }
}
