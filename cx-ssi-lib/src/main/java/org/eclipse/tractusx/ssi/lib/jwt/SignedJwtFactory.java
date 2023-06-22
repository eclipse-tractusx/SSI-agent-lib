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

package org.eclipse.tractusx.ssi.lib.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.util.*;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.octet.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;

/**
 * Convenience/helper class to generate and verify Signed JSON Web Tokens (JWTs) for communicating
 * between connector instances.
 */
public class SignedJwtFactory {

  private final OctetKeyPairFactory octetKeyPairFactory;

  public SignedJwtFactory(OctetKeyPairFactory octetKeyPairFactory) {
    this.octetKeyPairFactory = Objects.requireNonNull(octetKeyPairFactory);
  }

  /**
   * Creates a signed JWT {@link SignedJWT} that contains a set of claims and an issuer. Although
   * all private key types are possible, in the context of Distributed Identity using an Elliptic
   * Curve key ({@code P-256}) is advisable.
   *
   * @param audience the value of the token audience claim, e.g. the IDS Webhook address.
   * @return a {@code SignedJWT} that is signed with the private key and contains all claims listed.
   */
  @SneakyThrows
  public SignedJWT create(
      Did didIssuer,
      String audience,
      SerializedVerifiablePresentation serializedPresentation,
      IPrivateKey privateKey) {

    final String issuer = didIssuer.toString();
    final String subject = didIssuer.toString();

    // make on object out of it so that it can get serialized again
    Map<String, Object> vp =
        new ObjectMapper().readValue(serializedPresentation.getJson(), HashMap.class);

    var claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(subject)
            .audience(audience)
            .claim("vp", vp)
            .expirationTime(new Date(new Date().getTime() + 60 * 1000))
            .jwtID(UUID.randomUUID().toString())
            .build();

    final OctetKeyPair octetKeyPair = octetKeyPairFactory.fromPrivateKey(privateKey);
    return createSignedES256Jwt(octetKeyPair, claimsSet, issuer);
  }

  private static SignedJWT createSignedES256Jwt(
      OctetKeyPair privateKey, JWTClaimsSet claimsSet, String issuer) {
    JWSSigner signer;
    try {

      signer = new Ed25519Signer(privateKey);
      if (!signer.supportedJWSAlgorithms().contains(JWSAlgorithm.EdDSA)) {
        throw new RuntimeException(
            String.format(
                "Invalid signing method. Supported signing methods: %s",
                signer.supportedJWSAlgorithms().stream()
                    .map(JWSAlgorithm::getName)
                    .collect(Collectors.joining(", "))));
      }

      var algorithm = JWSAlgorithm.EdDSA;
      var type = JOSEObjectType.JWT;
      var header =
          new JWSHeader(
              algorithm, type, null, null, null, null, null, null, null, null, issuer, true, null,
              null);
      var vc = new SignedJWT(header, claimsSet);

      vc.sign(signer);
      return vc;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
