package org.eclipse.tractusx.ssi.lib.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.keys.Ed25519Key;
import org.eclipse.tractusx.ssi.lib.resolver.SigningKeyResolver;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;

/**
 * Convenience/helper class to generate and verify Signed JSON Web Tokens (JWTs) for communicating
 * between connector instances.
 */
public class SignedJwtFactory {

  private final SigningKeyResolver ocketKeyPairFactory;

  public SignedJwtFactory(SigningKeyResolver ocketKeyPairFactory) {
    this.ocketKeyPairFactory = Objects.requireNonNull(ocketKeyPairFactory);
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
      Ed25519Key signingKey) {

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

    final OctetKeyPair octetKeyPair = ocketKeyPairFactory.get(signingKey);
    return createSignedES256Jwt(octetKeyPair, claimsSet);
  }

  private static SignedJWT createSignedES256Jwt(OctetKeyPair privateKey, JWTClaimsSet claimsSet) {
    JWSSigner signer = null;
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
      var algorithm = JWSAlgorithm.EdDSA; // TODO Shouldn't this be ES256?
      var header = new JWSHeader(algorithm);
      var vc = new SignedJWT(header, claimsSet);

      vc.sign(signer);
      return vc;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
