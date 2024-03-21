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

package org.eclipse.tractusx.ssi.lib.model.proof.jws;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;

/** The type Jws signature 2020 builder. */
@NoArgsConstructor
public class JWSSignature2020Builder {

  private String proofPurpose;
  private String jws;
  private URI verificationMethod;
  private Instant created;

  /**
   * Proof purpose jws signature 2020 builder.
   *
   * @param proofPurpose the proof purpose
   * @return the jws signature 2020 builder
   */
  public JWSSignature2020Builder proofPurpose(String proofPurpose) {
    this.proofPurpose = proofPurpose;
    return this;
  }

  /**
   * Proof value jws signature 2020 builder.
   *
   * @param proofValue the proof value
   * @return the jws signature 2020 builder
   */
  public JWSSignature2020Builder proofValue(String proofValue) {
    this.jws = proofValue;
    return this;
  }

  /**
   * Verification method jws signature 2020 builder.
   *
   * @param verificationMethod the verification method
   * @return the jws signature 2020 builder
   */
  public JWSSignature2020Builder verificationMethod(URI verificationMethod) {
    this.verificationMethod = verificationMethod;
    return this;
  }

  /**
   * Created jws signature 2020 builder.
   *
   * @param created the created
   * @return the jws signature 2020 builder
   */
  public JWSSignature2020Builder created(Instant created) {
    this.created = created;
    return this;
  }

  /**
   * Build jws signature 2020.
   *
   * @return the jws signature 2020
   */
  public JWSSignature2020 build() {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(JWSSignature2020.TIME_FORMAT).withZone(ZoneOffset.UTC);

    Map<String, Object> standardEntries =
        Map.of(
            Proof.TYPE,
            JWSSignature2020.JWS_VERIFICATION_KEY_2020,
            JWSSignature2020.JWS,
            jws,
            JWSSignature2020.VERIFICATION_METHOD,
            verificationMethod.toString(),
            JWSSignature2020.CREATED,
            formatter.format(created),
            JWSSignature2020.PROOF_PURPOSE,
            proofPurpose);

    HashMap<String, Object> entries = new HashMap<>(standardEntries);

    return new JWSSignature2020(entries);
  }
}
