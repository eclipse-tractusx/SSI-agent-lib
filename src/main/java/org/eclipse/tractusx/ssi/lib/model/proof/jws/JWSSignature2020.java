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

package org.eclipse.tractusx.ssi.lib.model.proof.jws;

import java.net.URI;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

/**
 * E.g. "proof": {"type": "JsonWebSignature2020", "created": "2019-12-11T03:50:55Z", "jws":
 * "eyJhbGciOiJFZERTQSIsImI2NCI6ZmFsc2UsImNyaXQiOlsiYjY0Il19..MJ5GwWRMsadCyLNXU_flgJtsS32584MydBxBuygps_cM0sbU3abTEOMyUvmLNcKOwOBE1MfDoB1_YY425W3sAg",
 * "proofPurpose": "assertionMethod", "verificationMethod":
 * "https://example.com/issuer/123#ovsDKYBjFemIy8DVhc-w2LSi8CvXMw2AYDzHj04yxkc"}
 */
public class JWSSignature2020 extends Proof {

  public static final String JWS_VERIFICATION_KEY_2020 = "JsonWebSignature2020";
  public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  public static final String ASSERTION_METHOD = "assertionMethod";
  public static final String PROOF_PURPOSE = "proofPurpose";
  public static final String JWS = "jws";
  public static final String CREATED = "created";
  public static final String VERIFICATION_METHOD = "verificationMethod";

  public JWSSignature2020(Map<String, Object> json) {
    super(json);

    if (!JWS_VERIFICATION_KEY_2020.equals(json.get(TYPE))) {
      throw new IllegalArgumentException("Invalid JsonWebSignature2020 Type: " + json);
    }

    try {
      // verify getters
      Objects.requireNonNull(this.getProofPurpose());
      Objects.requireNonNull(this.getJws());
      Objects.requireNonNull(this.getVerificationMethod());
      Objects.requireNonNull(this.getCreated());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid JsonWebSignature2020", e);
    }
  }

  public String getProofPurpose() {
    return (String) this.get(PROOF_PURPOSE);
  }

  public String getJws() {
    return (String) this.get(JWS);
  }

  public URI getVerificationMethod() {
    return SerializeUtil.asURI(this.get(VERIFICATION_METHOD));
  }

  public Instant getCreated() {
    return Instant.parse((String) this.get(CREATED));
  }
}
