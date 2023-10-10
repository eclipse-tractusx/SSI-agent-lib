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

package org.eclipse.tractusx.ssi.lib.util.vc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialStatusList2021Entry;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationType;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;

public class TestVerifiableFactory {

  @SneakyThrows
  public static VerifiableCredential createVerifiableCredential(TestIdentity issuer, Proof proof) {
    final VerifiableCredentialBuilder verifiableCredentialBuilder =
        new VerifiableCredentialBuilder();

    VerifiableCredentialSubject verifiableCredentialSubject =
        new VerifiableCredentialSubject(Map.of("foo", "bar"));

    // add VC status
    String validStatus =
        "{\n"
            + "    \"id\": \"https://example.com/credentials/status/3#94567\",\n"
            + "    \"type\": \"StatusList2021Entry\",\n"
            + "    \"statusPurpose\": \"revocation\",\n"
            + "    \"statusListIndex\": \"94567\",\n"
            + "    \"statusListCredential\": \"https://example.com/credentials/status/3\"\n"
            + "  }";

    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> statusMap = objectMapper.readValue(validStatus, Map.class);
    VerifiableCredentialStatusList2021Entry verifiableCredentialStatusList2021Entry =
        new VerifiableCredentialStatusList2021Entry(statusMap);

    return verifiableCredentialBuilder
        .id(URI.create("did:test:id"))
        .type(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
        .issuer(issuer.getDid().toUri())
        .expirationDate(Instant.parse("2025-02-15T17:21:42Z").plusSeconds(3600))
        .issuanceDate(Instant.parse("2023-02-15T17:21:42Z"))
        .proof(proof)
        .credentialSubject(verifiableCredentialSubject)
        .verifiableCredentialStatus(verifiableCredentialStatusList2021Entry)
        .build();
  }

  @SneakyThrows
  public static VerifiablePresentation createVerifiablePresentation(
      TestIdentity issuer, List<VerifiableCredential> vcs, Proof proof) {
    final VerifiablePresentationBuilder verifiableCredentialBuilder =
        new VerifiablePresentationBuilder();

    return verifiableCredentialBuilder
        .id(URI.create("did:test:id"))
        .type(List.of(VerifiablePresentationType.VERIFIABLE_PRESENTATION))
        .verifiableCredentials(vcs)
        .proof(proof)
        .build();
  }

  public static VerifiableCredential attachProof(
      VerifiableCredential verifiableCredential, Proof proof) {
    VerifiableCredentialBuilder verifiableCredentialBuilder = new VerifiableCredentialBuilder();

    return verifiableCredentialBuilder
        .id(verifiableCredential.getId())
        .type(verifiableCredential.getTypes())
        .issuer(verifiableCredential.getIssuer())
        .expirationDate(verifiableCredential.getExpirationDate())
        .issuanceDate(verifiableCredential.getIssuanceDate())
        .proof(proof)
        .credentialSubject(verifiableCredential.getCredentialSubject())
        .verifiableCredentialStatus(verifiableCredential.getVerifiableCredentialStatus())
        .build();
  }

  public static VerifiablePresentation attachProof(
      VerifiablePresentation verifiablePresentation, Proof proof) {

    final VerifiablePresentationBuilder verifiablePresentationBuilder =
        new VerifiablePresentationBuilder();

    return verifiablePresentationBuilder
        .id(URI.create("did:test:id"))
        .type(List.of(VerifiablePresentationType.VERIFIABLE_PRESENTATION))
        .verifiableCredentials(verifiablePresentation.getVerifiableCredentials())
        .proof(proof)
        .build();
  }
}
