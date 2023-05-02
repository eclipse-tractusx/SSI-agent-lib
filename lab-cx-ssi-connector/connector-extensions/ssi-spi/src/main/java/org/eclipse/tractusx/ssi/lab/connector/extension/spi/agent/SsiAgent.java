package org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent;

import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.util.List;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiableCredential;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiablePresentation;

public interface SsiAgent {

  URI getConnectorDid();

  VerifiablePresentation check(SignedJWT jwtWithVerifiablePresentation, String expectedAudience);

  VerifiableCredential getByType(String verifiableCredentialType);

  SignedJWT createVerifiablePresentationAsJwt(
      List<VerifiableCredential> credentials, String audience);
}

/**
 * @param jwtWithVerifiablePresentation json web token with 'vp' claim
 * @return verifiable presentation
 */
//        VerifiablePresentation parseJwt(SignedJWT jwtWithVerifiablePresentation);
//
//        void verifyAndValidate(VerifiablePresentation verifiablePresentation) throws Exception; //
// TODO
//
//        VerifiableCredential getVerifiableCredentialByType(String verifiableCredentialType);
//
//        SignedJWT createVerifiablePresentationAsJwt(
//        List<VerifiableCredential> credentials, String audience);
