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

package org.eclipse.tractusx.ssi.lib.model.proof;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.eclipse.tractusx.ssi.lib.model.proof.ed25519.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;

/** The type Proof. */
public class Proof extends HashMap<String, Object> {

  /** The constant TYPE. */
  public static final String TYPE = "type";

  /**
   * Instantiates a new Proof.
   *
   * @param json the json
   */
  public Proof(Map<String, Object> json) {
    super(json);

    try {
      // verify getters
      Objects.requireNonNull(this.getType());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid Proof", e);
    }
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
   * Check if this Proof is Configuration or Signature
   *
   * @return true if Configuation or false if Signature
   */
  public boolean isConfiguration() {
    if (this instanceof Ed25519Signature2020 || this instanceof JWSSignature2020) return false;

    return true;
  }

  /**
   * Transform Proof Object to Configuration Object by removing any Proof Signature value
   *
   * @return Proof
   */
  public Proof toConfiguration() {
    this.remove(Ed25519Signature2020.PROOF_VALUE);
    this.remove(JWSSignature2020.JWS);
    return this;
  }
}
