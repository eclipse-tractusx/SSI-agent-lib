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

package org.eclipse.tractusx.ssi.lib.proof.types.jws;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.crypto.bc.BouncyCastleProviderSingleton;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jose.crypto.impl.ECDSAProvider;
import com.nimbusds.jose.crypto.impl.EdDSAProvider;
import com.nimbusds.jose.crypto.impl.RSASSAProvider;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.proc.JWSVerifierFactory;
import com.nimbusds.jose.util.Base64URL;
import java.net.URI;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.crypt.ec.ECPublicKeyWrapper;
import org.eclipse.tractusx.ssi.lib.crypt.rsa.RSAPublicKeyWrapper;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PublicKey;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolver;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolverException;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePublicKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.NoVerificationKeyFoundExcpetion;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidParser;
import org.eclipse.tractusx.ssi.lib.model.did.JWKVerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.jws.JWSSignature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.proof.IVerifier;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

/** The type Jws proof verifier. */
@RequiredArgsConstructor
public class JWSProofVerifier implements IVerifier {

  private final DidResolver didResolver;

  public boolean verify(HashedLinkedData hashedLinkedData, Verifiable document)
      throws UnsupportedSignatureTypeException, DidDocumentResolverNotRegisteredException,
          NoVerificationKeyFoundExcpetion, InvalidePublicKeyFormat {

    final Proof proof = document.getProof();
    if (!proof.getType().equals(JWSSignature2020.JWS_VERIFICATION_KEY_2020)) {
      throw new UnsupportedSignatureTypeException(proof.getType());
    }

    final JWSSignature2020 jwsSignature2020 = new JWSSignature2020(proof);

    Payload payload = new Payload(hashedLinkedData.getValue());

    JWSObject jws;
    try {
      jws = JWSObject.parse(jwsSignature2020.getJws(), payload);
    } catch (ParseException e) {
      throw new SsiException(e.getMessage());
    }

    JWK jwk = getJWK(jws.getHeader(), jwsSignature2020);
    try {
      JWSVerifier verifier = getVerifier(jws.getHeader(), jwk);
      return jws.verify(verifier);
    } catch (JOSEException e) {
      throw new SsiException(e.getMessage());
    }
  }

  private JWK getJWK(JWSHeader header, JWSSignature2020 signature)
      throws NoVerificationKeyFoundExcpetion {
    if (EdDSAProvider.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {
      return discoverOctetKey(signature);
    } else {
      if (RSASSAProvider.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()))
        return discoverRSAKey(signature);
      if (ECDSAProvider.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()))
        return discoverECKey(signature);
    }
    throw new IllegalArgumentException(
        String.format("algorithm %s is not supported", header.getAlgorithm().getName()));
  }

  private JWSVerifier getVerifier(JWSHeader header, JWK jwk) throws JOSEException {
    if (EdDSAProvider.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {
      return new Ed25519Verifier((OctetKeyPair) jwk);
    } else {
      JWSVerifierFactory verifierFactory = new DefaultJWSVerifierFactory();
      if (RSASSAProvider.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm()))
        return verifierFactory.createJWSVerifier(header, ((RSAKey) jwk).toRSAPublicKey());
      if (ECDSAProvider.SUPPORTED_ALGORITHMS.contains(header.getAlgorithm())) {
        ECDSAVerifier verifier =
            (ECDSAVerifier)
                verifierFactory.createJWSVerifier(header, ((ECKey) jwk).toECPublicKey());
        // this is necessary because of issue:
        // https://bitbucket.org/connect2id/nimbus-jose-jwt/issues/458/comnimbusdsjosejoseexception-curve-not
        verifier.getJCAContext().setProvider(BouncyCastleProviderSingleton.getInstance());
        return verifier;
      }
    }
    throw new IllegalArgumentException(
        String.format("algorithm %s is not supported", header.getAlgorithm().getName()));
  }

  private RSAKey discoverRSAKey(JWSSignature2020 signature) throws NoVerificationKeyFoundExcpetion {
    JWKVerificationMethod key = discoverKey(signature);
    return (RSAKey) key.getJwk();
  }

  private ECKey discoverECKey(JWSSignature2020 signature) throws NoVerificationKeyFoundExcpetion {
    JWKVerificationMethod key = discoverKey(signature);
    return (ECKey) key.getJwk();
  }

  private JWKVerificationMethod discoverKey(JWSSignature2020 signature)
      throws NoVerificationKeyFoundExcpetion {
    final Did issuer = DidParser.parse(signature.getVerificationMethod());

    final DidDocument document;
    try {
      document = this.didResolver.resolve(issuer);
    } catch (DidResolverException e) {
      throw new RuntimeException(e);
    }

    final URI verificationMethodId = signature.getVerificationMethod();
    List<Object> verificationRelationShip =
        (List<Object>) document.get(signature.getProofPurpose());

    if (verificationRelationShip != null) {
      var verificationRelationShipValid =
          validateVerificationRelationShip(verificationRelationShip, verificationMethodId);

      if (!verificationRelationShipValid.valid) {
        throw new IllegalStateException("verification relation ship is not valid");
      }

      if (Optional.ofNullable(verificationRelationShipValid.verificationMethod).isPresent()) {
        return verificationRelationShipValid.verificationMethod;
      }
    }

    // document and loop
    return document.getVerificationMethods().stream()
        .filter(v -> v.getId().equals(verificationMethodId))
        .filter(JWKVerificationMethod::isInstance)
        .map(JWKVerificationMethod::new)
        .findFirst()
        .orElseThrow(
            () ->
                new NoVerificationKeyFoundExcpetion(
                    "No JWS verification Key found in DID Document"));
  }

  private OctetKeyPair discoverOctetKey(JWSSignature2020 signature)
      throws NoVerificationKeyFoundExcpetion {
    JWKVerificationMethod key = discoverKey(signature);
    var x = ((OctetKeyPair) key.getJwk()).getX();
    return new OctetKeyPair.Builder(Curve.Ed25519, x).build();
  }

  // check if verificationMethodId is in verificationRelationship(proofPurpose) and return embedded
  // verificationMethod if is embedded
  private VerificationRelationShipResult validateVerificationRelationShip(
      List<Object> verificationRelationShip, URI verificationMethodId) {
    for (Object o : verificationRelationShip) {
      if (o instanceof URI) {
        URI relationShip = (URI) o;
        if (relationShip.equals(verificationMethodId)) {
          return new VerificationRelationShipResult(true);
        }
      } else if (o instanceof String) {
        URI relationShip = URI.create((String) o);
        if (relationShip.equals(verificationMethodId)) {
          return new VerificationRelationShipResult(true);
        }
      } else if (o instanceof Map) {
        // embedded relationship, only usable by proofPurpose == relationShip
        Map<String, Object> mapRelationShip = (Map<String, Object>) o;
        URI relationShip = (URI) mapRelationShip.get("id");
        if (relationShip.equals(verificationMethodId)) {
          return new VerificationRelationShipResult(
              true, new JWKVerificationMethod(mapRelationShip));
        }
      }
    }
    return new VerificationRelationShipResult(false);
  }

  private static class VerificationRelationShipResult {

    final boolean valid;

    JWKVerificationMethod verificationMethod;

    public VerificationRelationShipResult(boolean valid) {
      this.valid = valid;
    }

    public VerificationRelationShipResult(boolean valid, JWKVerificationMethod verificationMethod) {
      this.valid = valid;
      this.verificationMethod = verificationMethod;
    }
  }

  /**
   * Verify hashedLinkedData.
   *
   * @param hashedLinkedData the hashed linked data
   * @param signature the signature
   * @param publicKey the public key
   * @return the boolean
   */
  @SneakyThrows
  public boolean verify(
      HashedLinkedData hashedLinkedData,
      byte[] signature,
      IPublicKey publicKey,
      SignatureType type) {
    JWK jwk = null;
    switch (type) {
      case JWS:
        jwk = ((x21559PublicKey) publicKey).toJwk();
        break;
      case JWS_P256:
      case JWS_P384:
      case JWS_SEC_P_256K1:
        jwk = ((ECPublicKeyWrapper) publicKey).toJwk();
        break;
      case JWS_RSA:
        jwk = ((RSAPublicKeyWrapper) publicKey).toJwk();
        break;
      default:
        throw new IllegalArgumentException(
            String.format("algorithm %s is not supported", type.algorithm));
    }

    JWSVerifier verifier = getVerifier(new JWSHeader(new JWSAlgorithm(type.algorithm)), jwk);

    Payload payload = new Payload(hashedLinkedData.getValue());
    JWSObject jws;
    try {

      jws = JWSObject.parse(new String(signature), payload);
    } catch (ParseException e) {
      throw new SsiException(e.getMessage());
    }
    return jws.verify(verifier);
  }

  private OctetKeyPair getOctet(byte[] keyBytes) {
    return new OctetKeyPair.Builder(Curve.Ed25519, Base64URL.encode(keyBytes)).build();
  }
}
