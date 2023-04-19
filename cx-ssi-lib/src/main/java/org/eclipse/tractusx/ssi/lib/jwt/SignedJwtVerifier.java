package org.eclipse.tractusx.ssi.lib.jwt;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedDidMethodException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidParser;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolverRegistry;

/**
 * Convenience/helper class to generate and verify Signed JSON Web Tokens (JWTs) for communicating
 * between connector instances.
 */
@RequiredArgsConstructor
public class SignedJwtVerifier {

  private final DidDocumentResolverRegistry didDocumentResolverRegistry;

  /**
   * Verifies a VerifiableCredential using the issuer's public key
   *
   * @param jwt a {@link SignedJWT} that was sent by the claiming party.
   * @return true if verified, false otherwise
   */
  @SneakyThrows
  public boolean verify(SignedJWT jwt) throws JOSEException, UnsupportedDidMethodException {

    JWTClaimsSet jwtClaimsSet;
    try {
      jwtClaimsSet = jwt.getJWTClaimsSet();
    } catch (ParseException e) {
      throw new JOSEException(e.getMessage());
    }

    final String issuer = jwtClaimsSet.getIssuer();
    final Did issuerDid = DidParser.parse(issuer);

    final DidDocumentResolver didDocumentResolver;
    didDocumentResolver = didDocumentResolverRegistry.get(issuerDid.getMethod());

    final DidDocument issuerDidDocument = didDocumentResolver.resolve(issuerDid);
    final List<Ed25519VerificationKey2020> verificationMethods =
        issuerDidDocument.getVerificationMethods();

    // verify JWT signature
    // TODO Don't try out each key. Better -> use key authorization key
    for (Ed25519VerificationKey2020 method : verificationMethods) {

      var length = method.getKey().getEncoded().length;
      byte[] b1 = Arrays.copyOfRange(method.getKey().getEncoded(), length - 32, length);
      var keyPair = new OctetKeyPair.Builder(Curve.Ed25519, Base64URL.encode(b1)).build();

      return jwt.verify(new Ed25519Verifier(keyPair));
    }

    return false;
  }
}
