package org.eclipse.tractusx.ssi.agent.lib.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.SignedJWT;
import java.text.ParseException;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.InvalidJsonLdException;
import org.eclipse.tractusx.ssi.lib.exception.JwtException;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtValidator;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofValidation;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidDocumentResolverRegistry;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdSerializer;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;

@RequiredArgsConstructor
public class JwtReader {

  public static JwtReader newInstance(DidDocumentResolverRegistry didDocumentResolverRegistry) {

    return new JwtReader(
        new SignedJwtVerifier(didDocumentResolverRegistry),
        new SignedJwtValidator(),
        new JsonLdSerializerImpl(),
        LinkedDataProofValidation.newInstance(didDocumentResolverRegistry));
  }

  private final SignedJwtVerifier jwtVerifier;
  private final SignedJwtValidator jwtValidator;
  private final JsonLdSerializer jsonLdSerializer;
  private final LinkedDataProofValidation linkedDataProofValidation;

  public VerifiablePresentation read(SignedJWT jwt, String audience, boolean validateJsonLd)
      throws ParseException, JsonProcessingException, JwtException,
          DidDocumentResolverNotRegisteredException, InvalidJsonLdException {

    jwtVerifier.verify(jwt);
    jwtValidator.validate(jwt, audience);

    final Object vpClaimValue = jwt.getJWTClaimsSet().getClaim("vp");
    var vpClaimValueSerialized = new ObjectMapper().writeValueAsString(vpClaimValue);
    final SerializedVerifiablePresentation vpSerialized =
        new SerializedVerifiablePresentation(vpClaimValueSerialized);

    final VerifiablePresentation verifiablePresentation =
        jsonLdSerializer.deserializePresentation(vpSerialized, validateJsonLd);

    for (final VerifiableCredential credential :
        verifiablePresentation.getVerifiableCredentials()) {
      if (credential.getProof() == null) {
        throw new RuntimeException("No proof"); // TODO
      }

      // TODO Proof check withs because our domain model contains all properties, but this should
      // not be implicit
      var isValid = linkedDataProofValidation.checkProof(credential);
      if (!isValid) {
        throw new RuntimeException("Invalid proof"); // TODO
      }
    }

    return verifiablePresentation;
  }
}
