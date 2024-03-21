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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.time.Instant;
import java.util.*;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.util.SignerUtil;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.JwtConfig;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;

/**
 * Convenience/helper class to generate * Convenience/helper class to generate and verify Signed
 * JSON Web Tokens (JWTs) for communicating between connector instances.
 */
public class SignedJwtFactory {

  private final SignatureType signatureType;

  public SignedJwtFactory() {
    this.signatureType = SignatureType.JWS; // EdDSA
  }

  public SignedJwtFactory(SignatureType signatureType) {
    this.signatureType = signatureType;
  }

  /**
   * Creates a signed JWT {@link SignedJWT} that contains a set of claims and an issuer. Although
   * all private key types are possible, in the context of Distributed Identity using an Elliptic
   * Curve key ({@code P-256}) is advisable.
   *
   * @param audience the value of the token audience claim, e.g. the IDS Webhook address.
   * @param keyId the id of the key, the kid of the jws-header will be constructed via
   *     <issuer>+"#"+<keyId>
   * @return a {@code SignedJWT} that is signed with the private key and contains all claims listed.
   */
  @SneakyThrows
  public SignedJWT create(
      URI id,
      Did didIssuer,
      String audience,
      SerializedVerifiablePresentation serializedPresentation,
      IPrivateKey privateKey,
      String keyId) {
    JwtConfig jwtConfig = JwtConfig.builder().expirationTime(60).build();
    return create(id, didIssuer, audience, serializedPresentation, privateKey, keyId, jwtConfig);
  }

  /**
   * Creates a signed JWT {@link SignedJWT} that contains a set of claims and an issuer. Although
   * all private key types are possible, in the context of Distributed Identity using an Elliptic
   * Curve key ({@code P-256}) is advisable.
   *
   * @param audience the value of the token audience claim, e.g. the IDS Webhook address.
   * @param keyId the id of the key, the kid of the jws-header will be constructed via
   *     <issuer>+"#"+<keyId>
   * @param config the custom configuration for the JWT to create, e.g. custom expiration time (exp)
   * @return a {@code SignedJWT} that is signed with the private key and contains all claims listed.
   */
  @SneakyThrows
  public SignedJWT create(
      URI id,
      Did didIssuer,
      String audience,
      SerializedVerifiablePresentation serializedPresentation,
      IPrivateKey privateKey,
      String keyId,
      JwtConfig config) {

    final String issuer = didIssuer.toString();
    final String subject = didIssuer.toString();

    TypeReference<HashMap<String, Object>> typeRef =
        new TypeReference<HashMap<String, Object>>() {};

    // make on object out of it so that it can get serialized again
    Map<String, Object> vp =
        new ObjectMapper().readValue(serializedPresentation.getJson(), typeRef);

    Date iat = new Date();

    var claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(subject)
            .audience(audience)
            .claim("vp", vp)
            .issueTime(iat)
            .expirationTime(new Date(iat.getTime() + config.getExpirationTime() * 1000))
            .jwtID(id.toString())
            .build();

    return createSignedES256Jwt(privateKey, claimsSet, issuer, keyId);
  }

  @SneakyThrows
  public SignedJWT create(
      URI id,
      Did didIssuer,
      Did holderIssuer,
      Date expDate,
      Map<String, Object> vc,
      IPrivateKey privateKey,
      String keyId) {
    final String issuer = didIssuer.toString();
    final String subject = holderIssuer.toString();

    // check if issuanceDate is presented in VC then use it, otherwise null
    final Date issueDate =
        vc.containsKey("issuanceDate")
            ? Date.from(Instant.parse((String) vc.get("issuanceDate")))
            : null;

    vc.remove(Verifiable.PROOF);

    var claimsSet =
        new JWTClaimsSet.Builder()
            .issuer(issuer)
            .subject(subject)
            .claim("vc", vc)
            .expirationTime(expDate)
            .issueTime(issueDate)
            .build();

    return createSignedES256Jwt(privateKey, claimsSet, issuer, keyId);
  }

  public SignedJWT createSignedES256Jwt(
      IPrivateKey privateKey, JWTClaimsSet claimsSet, String issuer, String keyId) {

    try {
      JWSSigner signer = SignerUtil.getSigner(signatureType, privateKey);
      var type = JOSEObjectType.JWT;
      var header =
          new JWSHeader.Builder(new JWSAlgorithm(signatureType.algorithm))
              .type(type)
              .keyID(issuer + "#" + keyId)
              .build();

      var vc = new SignedJWT(header, claimsSet);

      vc.sign(signer);
      return vc;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
