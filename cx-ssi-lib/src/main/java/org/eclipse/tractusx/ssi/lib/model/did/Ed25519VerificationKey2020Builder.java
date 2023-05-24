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

package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;

@NoArgsConstructor
public class Ed25519VerificationKey2020Builder {
  private URI id;
  private URI controller;
  private String publicKeyMultiBase;

  public Ed25519VerificationKey2020Builder id(URI id) {
    this.id = id;
    return this;
  }

  public Ed25519VerificationKey2020Builder controller(URI controller) {
    this.controller = controller;
    return this;
  }

  public Ed25519VerificationKey2020Builder publicKeyMultiBase(MultibaseString multibaseString) {
    this.publicKeyMultiBase = multibaseString.getEncoded();
    return this;
  }

  public Ed25519VerificationKey2020 build() {
    return new Ed25519VerificationKey2020(
        Map.of(
            Ed25519VerificationKey2020.ID, id,
            Ed25519VerificationKey2020.TYPE, Ed25519VerificationKey2020.DEFAULT_TYPE,
            Ed25519VerificationKey2020.CONTROLLER, controller,
            Ed25519VerificationKey2020.PUBLIC_KEY_BASE_58, publicKeyMultiBase));
  }
}
