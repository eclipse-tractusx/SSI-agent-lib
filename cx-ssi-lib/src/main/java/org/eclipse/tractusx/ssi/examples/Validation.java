package org.eclipse.tractusx.ssi.examples;

import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtValidator;

import com.nimbusds.jwt.SignedJWT;

public class Validation {
    public static boolean validateJWT(SignedJWT signedJWT, String audience){
        SignedJwtValidator jwtValidator = new SignedJwtValidator();
        return jwtValidator.validate(signedJWT, audience);
    }
}
