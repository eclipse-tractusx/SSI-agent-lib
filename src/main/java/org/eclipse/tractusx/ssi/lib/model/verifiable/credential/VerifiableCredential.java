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

package org.eclipse.tractusx.ssi.lib.model.verifiable.credential;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

// @formatter:off
/**
 * The type of VerifiableCredential Spec: <a href="https://www.w3.org/TR/vc-data-model/">...</a>
 *
 * <p>Verifiable Credential e.g. from
 * https://www.w3.org/TR/vc-data-model/#example-usage-of-the-credentialsubject-property {
 * "@context": [ "https://www.w3.org/2018/credentials/v1",
 * "https://www.w3.org/2018/credentials/examples/v1", "https://w3id.org/secu
 * rity/suites/ed25519-2020/v1" ], "id": "http://example.edu/credentials/3732", "type": [
 * "VerifiableCredential", "UniversityDegreeCredential" ], "issuer":
 * "https://example.edu/issuers/565049", "issuanceDate": "2010-01-01T00:00:00Z",
 * "credentialSubject": { "id": "did:example:ebfeb1f712ebc6f1c276e12ec21", "degree": { "type":
 * "BachelorDegree", "name": "Bachelor of Science and Arts" } }, "proof": { "type":
 * "Ed25519Signature2020", "created": "2022-02-25T14:58:43Z", "verificationMethod":
 * "https://example.edu/issuers/565049#key-1", "proofPurpose": "assertionMethod", "proofValue":
 * "zeEdUoM7m9cY8ZyTpey83yBKeBcmcvbyrEQzJ19rD2UXArU2U1jPGoEt rRvGYppdiK37GU4NBeoPakxpWhAvsVSt" } }
 */
// @formatter:on
@ToString(callSuper = true)
public class VerifiableCredential extends Verifiable {

  /** The constant DEFAULT_CONTEXT. */
  public static final URI DEFAULT_CONTEXT = URI.create("https://www.w3.org/2018/credentials/v1");

  /** The constant TIME_FORMAT. */
  public static final String TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  /** The constant ISSUER. */
  public static final String ISSUER = "issuer";

  /** The constant ISSUANCE_DATE. */
  public static final String ISSUANCE_DATE = "issuanceDate";

  /** The constant EXPIRATION_DATE. */
  public static final String EXPIRATION_DATE = "expirationDate";

  /** The constant CREDENTIAL_SUBJECT. */
  public static final String CREDENTIAL_SUBJECT = "credentialSubject";

  /** The constant CREDENTIAL_STATUS. */
  public static final String CREDENTIAL_STATUS = "credentialStatus";

  /** The constant CREDENTIAL_SCHEMA. */
  public static final String CREDENTIAL_SCHEMA = "credentialSchema";

  /** The constant REFERENCE_NUMBER. */
  public static final String REFERENCE_NUMBER = "referenceNumber";

  /** The constant EVIDENCE. */
  public static final String EVIDENCE = "evidence";

  /** The constant TERMS_OF_USE. */
  public static final String TERMS_OF_USE = "termsOfUse";

  /** The constant REFRESH_SERVICE. */
  public static final String REFRESH_SERVICE = "refreshService";

  /**
   * Instantiates a new Verifiable credential.
   *
   * @param json the json
   */
  @JsonCreator
  public VerifiableCredential(Map<String, Object> json) {
    super(json, VerifiableType.VC);

    try {
      // validate getters
      Objects.requireNonNull(this.getIssuer());
      Objects.requireNonNull(this.getIssuanceDate());
      Objects.requireNonNull(this.getCredentialSubject());
      this.getExpirationDate();
      this.getProof();

    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format("Invalid VerifiableCredential: %s", SerializeUtil.toJson(json)), e);
    }

    if (getCredentialSubject().isEmpty()) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid VerifiableCredential. CredentialSubject must not be empty: %s",
              SerializeUtil.toJson(json)));
    }

    // validate status list if provided
  }

  @NonNull
  @Override
  public URI getId() {
    return SerializeUtil.asURI(get(ID));
  }

  @NonNull
  @Override
  public List<String> getTypes() {
    return SerializeUtil.asStringList(get(TYPE));
  }

  /**
   * Gets issuer.
   *
   * @return the issuer
   */
  @NonNull
  public URI getIssuer() {
    return SerializeUtil.asURI(get(ISSUER));
  }

  /**
   * Gets issuance date.
   *
   * @return the issuance date
   */
  @NonNull
  public Instant getIssuanceDate() {
    return Instant.parse((String) get(ISSUANCE_DATE));
  }

  /**
   * Gets expiration date.
   *
   * @return the expiration date
   */
  public Instant getExpirationDate() {
    if (!containsKey(EXPIRATION_DATE)) {
      return null;
    }
    return Instant.parse((String) get(EXPIRATION_DATE));
  }

  /**
   * Gets credential subject.
   *
   * @return the credential subject
   */
  @NonNull
  public List<VerifiableCredentialSubject> getCredentialSubject() {
    Object subject = get(CREDENTIAL_SUBJECT);

    if (subject instanceof List) {
      return ((List<Map<String, Object>>) subject)
          .stream().map(VerifiableCredentialSubject::new).toList();
    } else if (subject instanceof Map) {
      return List.of(new VerifiableCredentialSubject((Map<String, Object>) subject));
    } else {
      throw new IllegalArgumentException(
          "Invalid credential subject type. "
              + subject.getClass().getName()
              + " is not supported.");
    }
  }

  /**
   * Gets verifiable credential status.
   *
   * @return the verifiable credential status
   */
  public Optional<VerifiableCredentialStatus> getVerifiableCredentialStatus() {
    Object data = get(CREDENTIAL_STATUS);
    if (data == null) {
      return Optional.empty();
    }
    Object type = ((Map<String, Object>) data).get(TYPE);
    if (Objects.isNull(type)) {
      throw new IllegalArgumentException("Status type not found");
    }
    if (type.toString().equals(VerifiableCredentialStatusList2021Entry.STATUS_LIST_2021_ENTRY)) {
      return Optional.of(new VerifiableCredentialStatusList2021Entry((Map<String, Object>) data));
    } else {
      Map<String, Object> map = (Map<String, Object>) data;
      return Optional.of(
          new VerifiableCredentialStatus(map) {
            @Override
            public String getType() {
              if (map.containsKey(TYPE)) {
                return map.get(TYPE).toString();
              } else {
                throw new IllegalArgumentException("Status type not found");
              }
            }
          });
    }
  }
}
