/********************************************************************************
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
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.util.SignerUtil;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.JwtConfig;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;

/**
 * Convenience/helper class to generate and verify Signed JSON Web Tokens (JWTs) for communicating
 * between connector instances.
 */
public class SignedJwtFactory {

  private final SignatureType signatureType;

  /** Instantiates a new Signed jwt factory. */
  public SignedJwtFactory() {
    this.signatureType = SignatureType.JWS; // EdDSA
  }

  /**
   * Instantiates a new Signed jwt factory.
   *
   * @param signatureType the signature type
   */
  public SignedJwtFactory(SignatureType signatureType) {
    this.signatureType = signatureType;
  }

  /**
   * Creates a signed JWT {@link SignedJWT} that contains a set of claims and an issuer with a
   * default configuration (60 second expiration time) {@link JwtConfig} for VP
   *
   * @param didIssuer the did issuer
   * @param audience the audience
   * @param serializedPresentation the serialized presentation
   * @param privateKey the private key
   * @param keyId the key id
   * @return signed jwt
   */
  @SneakyThrows
  public SignedJWT create(
      Did didIssuer,
      String audience,
      SerializedVerifiablePresentation serializedPresentation,
      IPrivateKey privateKey,
      String keyId) {
    JwtConfig jwtConfig = JwtConfig.builder().expirationTime(60).build();
    return create(didIssuer, audience, serializedPresentation, privateKey, keyId, jwtConfig);
  }

  /**
   * Create signed jwt.
   *
   * @param id the id
   * @param didIssuer the did issuer
   * @param holderIssuer the holder issuer
   * @param expDate the exp date
   * @param vc the vc
   * @param privateKey the private key
   * @param keyId the key id
   * @return the signed jwt
   */
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

  /**
   * Creates a signed JWT {@link SignedJWT} that contains a set of claims and an issuer with a
   * specific configuration {@link JwtConfig} for VP
   *
   * @param didIssuer the did issuer
   * @param audience the audience
   * @param serializedPresentation the serialized presentation
   * @param privateKey the private key
   * @param keyId the key id
   * @param config the config
   * @return signed jwt
   */
  @SneakyThrows
  public SignedJWT create(
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
            .jwtID(UUID.randomUUID().toString())
            .build();

    return createSignedES256Jwt(privateKey, claimsSet, issuer, keyId);
  }

  /**
   * Creates a signed JWT {@link SignedJWT} from a Verifiable Credential
   *
   * @param didIssuer the did issuer
   * @param holderDid the holder did
   * @param vc the vc
   * @param privateKey the private key
   * @param keyId the key id
   * @return signed jwt
   */
  @SneakyThrows
  public SignedJWT create(
      Did didIssuer,
      Did holderDid,
      LinkedHashMap<String, Object> vc,
      IPrivateKey privateKey,
      String keyId) {
    final String issuer = didIssuer.toString();
    final String subject = holderDid.toString();

    // check if expirationDate is presented in VC then use it, otherwise null
    final Date expireDateAsDate =
        vc.containsKey("expirationDate")
            ? Date.from(Instant.parse((String) vc.get("expirationDate")))
            : null;

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
            .expirationTime(expireDateAsDate)
            .issueTime(issueDate)
            .build();

    return createSignedES256Jwt(privateKey, claimsSet, issuer, keyId);
  }

  /**
   * Create a signedJwt for ES256 JWT {@link SignedJWT} with a set of claims
   *
   * @param privateKey the private key
   * @param claimsSet the claims set
   * @param issuer the issuer
   * @param keyId the key id
   * @return signed jwt
   */
  public SignedJWT createSignedES256Jwt(
      IPrivateKey privateKey, JWTClaimsSet claimsSet, String issuer, String keyId) {

    try {
      JWSSigner signer = SignerUtil.getSigner(signatureType, privateKey);
      var type = JOSEObjectType.JWT;
      var header =
          new JWSHeader.Builder(new JWSAlgorithm(signatureType.getAlgorithm()))
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
