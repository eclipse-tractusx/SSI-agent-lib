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

package org.eclipse.tractusx.ssi.lib.model.proof.ed25519;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;

/** The type Ed 25519 signature 2020 builder. */
@NoArgsConstructor
public class Ed25519Signature2020Builder {

  private String proofPurpose;
  private String proofValue;
  private URI verificationMethod;
  private Instant created;

  /**
   * Proof purpose ed 25519 signature 2020 builder.
   *
   * @param proofPurpose the proof purpose
   * @return the ed 25519 signature 2020 builder
   */
  public Ed25519Signature2020Builder proofPurpose(String proofPurpose) {
    this.proofPurpose = proofPurpose;
    return this;
  }

  /**
   * Proof value ed 25519 signature 2020 builder.
   *
   * @param proofValue the proof value
   * @return the ed 25519 signature 2020 builder
   */
  public Ed25519Signature2020Builder proofValue(String proofValue) {
    this.proofValue = proofValue;
    return this;
  }

  /**
   * Verification method ed 25519 signature 2020 builder.
   *
   * @param verificationMethod the verification method
   * @return the ed 25519 signature 2020 builder
   */
  public Ed25519Signature2020Builder verificationMethod(URI verificationMethod) {
    this.verificationMethod = verificationMethod;
    return this;
  }

  /**
   * Created ed 25519 signature 2020 builder.
   *
   * @param created the created
   * @return the ed 25519 signature 2020 builder
   */
  public Ed25519Signature2020Builder created(Instant created) {
    this.created = created;
    return this;
  }

  /**
   * Build ed 25519 signature 2020.
   *
   * @return the ed 25519 signature 2020
   */
  public Ed25519Signature2020 build() {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(Ed25519Signature2020.TIME_FORMAT).withZone(ZoneOffset.UTC);

    Map<String, Object> map =
        Map.of(
            Proof.TYPE,
            Ed25519Signature2020.ED25519_VERIFICATION_KEY_2018,
            Ed25519Signature2020.PROOF_PURPOSE,
            proofPurpose,
            Ed25519Signature2020.PROOF_VALUE,
            proofValue,
            Ed25519Signature2020.VERIFICATION_METHOD,
            verificationMethod.toString(),
            Ed25519Signature2020.CREATED,
            formatter.format(created));

    return new Ed25519Signature2020(map);
  }
}
