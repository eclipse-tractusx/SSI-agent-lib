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

package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import com.danubetech.verifiablecredentials.CredentialSubject;
import info.weboftrust.ldsignatures.LdProof;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.PackagePrivate;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationBuilder;

@PackagePrivate
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DanubeTechMapper {
  @NonNull
  public static com.danubetech.verifiablecredentials.VerifiablePresentation map(
      VerifiablePresentation presentation) {

    final List<com.danubetech.verifiablecredentials.VerifiableCredential> dtCredentials =
        presentation.getVerifiableCredentials().stream()
            .map(DanubeTechMapper::map)
            .collect(Collectors.toList());

    // TODO Throw Exception if more or less than one

    if (!presentation.getTypes().get(0).equals("VerifiablePresentation")) {
      throw new SsiException("Type: VerifiablePresentation missing");
    }
    // VerifiablePresentation Type is automatically added in Builder
    List<String> types = new ArrayList<>(presentation.getTypes());
    types.remove(0); // TODO remove default type differently

    com.danubetech.verifiablecredentials.VerifiablePresentation.Builder<
            ? extends com.danubetech.verifiablecredentials.VerifiablePresentation.Builder<?>>
        builder = com.danubetech.verifiablecredentials.VerifiablePresentation.builder();

    builder
        .defaultContexts(true)
        .forceContextsArray(true)
        .forceTypesArray(true)
        .id(presentation.getId())
        .types(types)
        // .holder(presentation.getHolder())
        .ldProof(null); // set to null, as presentation will be used within JWT

    // TODO handle more than one verifiable credential per presentation
    if (dtCredentials.size() > 1) {
      throw new RuntimeException(
          "More than one verifiable credential per presentation is not supported"); // TODO
    }
    if (dtCredentials.size() == 1) {
      builder.verifiableCredential(dtCredentials.get(0));
    }

    return builder.build();
  }

  @NonNull
  @SneakyThrows
  public static VerifiablePresentation map(
      com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation) {

    Objects.requireNonNull(dtPresentation);
    Objects.requireNonNull(dtPresentation.getVerifiableCredential());

    List<VerifiableCredential> credentials = List.of(map(dtPresentation.getVerifiableCredential()));

    final VerifiablePresentationBuilder verifiablePresentationBuilder =
        new VerifiablePresentationBuilder();
    return verifiablePresentationBuilder
        .id(dtPresentation.getId())
        .type(dtPresentation.getTypes())
        .verifiableCredentials(credentials)
        .build();
  }

  @NonNull
  @SneakyThrows
  public static com.danubetech.verifiablecredentials.VerifiableCredential map(
      VerifiableCredential credential) {

    if (credential.getTypes().stream()
        .noneMatch(x -> x.equals(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))) {
      throw new SsiException("Type: VerifiableCredential missing");
    }
    // Verifiable Credential Type is automatically added in Builder
    final List<String> types =
        credential.getTypes().stream()
            .filter(t -> !t.equals(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .collect(Collectors.toList());

    if (credential.getCredentialSubject().size() != 1) {
      throw new SsiException(
          "Only one credential subject is supported by the Danubetech library used.");
    }

    final VerifiableCredentialSubject subject = credential.getCredentialSubject().get(0);
    final HashMap<String, Object> properties = new HashMap<>(subject);
    properties.remove("id");
    final com.danubetech.verifiablecredentials.CredentialSubject dtSubject =
        CredentialSubject.builder()
            .id(credential.getCredentialSubject().get(0).getId())
            .properties(properties)
            .build();

    return com.danubetech.verifiablecredentials.VerifiableCredential.builder()
        .defaultContexts(true)
        .forceContextsArray(true)
        .forceTypesArray(true)
        .id(credential.getId())
        .types(types)
        .issuer(credential.getIssuer())
        .issuanceDate(Date.from(credential.getIssuanceDate()))
        .expirationDate(Date.from(credential.getExpirationDate()))
        .credentialSubject(dtSubject)
        .ldProof(map(credential.getProof()))
        .build();
    // .credentialStatus(credential.getStatus())
  }

  @NonNull
  @SneakyThrows
  public static VerifiableCredential map(
      com.danubetech.verifiablecredentials.VerifiableCredential dtCredential) throws SsiException {
    return new VerifiableCredential(dtCredential.toMap());
  }

  private static VerifiableCredentialSubject map(CredentialSubject dtSubject) {
    if (dtSubject == null) return null;

    return new VerifiableCredentialSubject(dtSubject.getClaims());
  }

  private static Proof map(LdProof dtProof) {
    if (dtProof == null) return null;

    return new Proof(dtProof.toMap());
  }

  private static LdProof map(Proof proof) {
    if (proof == null) return null;

    return LdProof.fromMap(proof);
  }
}
