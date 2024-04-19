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

import java.util.Map;
import java.util.Objects;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;

/**
 * The type of {@link Ed25519Signature2020}
 *
 * <p>E.g. "proof": { "type": "Ed25519Signature2020", "created": "2021-11-13T18:19:39Z",
 * "verificationMethod": "https://example.edu/issuers/14#key-1", "proofPurpose": "assertionMethod",
 * "proofValue": "z58DAdFfa9SkqZMVPxAQpic7ndSayn1PzZs6ZjWp1CktyGesjuTSwRdo
 * WhAfGFCF5bppETSTojQCrfFPP2oumHKtz" }
 */
public class Ed25519Signature2020 extends Ed25519ProofConfiguration {

  /** The constant PROOF_VALUE. */
  public static final String PROOF_VALUE = "proofValue";

  /** The constant ED25519_VERIFICATION_KEY_2018. */
  public static final String ED25519_VERIFICATION_KEY_2018 = "Ed25519Signature2020";

  /** The constant TIME_FORMAT. */
  public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  /** The constant ASSERTION_METHOD. */
  public static final String ASSERTION_METHOD = "assertionMethod";

  /** The constant PROOF_PURPOSE. */
  public static final String PROOF_PURPOSE = "proofPurpose";

  /** The constant CREATED. */
  public static final String CREATED = "created";

  /** The constant VERIFICATION_METHOD. */
  public static final String VERIFICATION_METHOD = "verificationMethod";

  /**
   * Instantiates a new Ed25519 signature 2020.
   *
   * @param json the json
   */
  public Ed25519Signature2020(Map<String, Object> json) {
    super(json);

    if (!ED25519_VERIFICATION_KEY_2018.equals(json.get(TYPE))) {
      throw new IllegalArgumentException("Invalid Ed25519Signature2020 Type: " + json);
    }

    try {
      // verify getters
      Objects.requireNonNull(this.getProofValue());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid Ed25519Signature2020", e);
    }
  }

  /**
   * Gets proof value.
   *
   * @return the proof value
   */
  public MultibaseString getProofValue() {
    return MultibaseFactory.create((String) this.get(PROOF_VALUE).toString());
  }
}
