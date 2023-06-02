package org.eclipse.tractusx.ssi.examples;

import com.nimbusds.jwt.SignedJWT;
import org.eclipse.tractusx.ssi.lib.exception.JwtAudienceCheckFailedException;
import org.eclipse.tractusx.ssi.lib.exception.JwtExpiredException;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtValidator;

public class Validation {
  public static void validateJWT(SignedJWT signedJWT, String audience)
      throws JwtAudienceCheckFailedException, JwtExpiredException {
    SignedJwtValidator jwtValidator = new SignedJwtValidator();
    jwtValidator.validate(signedJWT, audience);
  }
}
