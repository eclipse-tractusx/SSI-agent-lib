package org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent;

import com.nimbusds.jwt.SignedJWT;
import java.util.List;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiableCredential;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiablePresentation;

public interface SsiAgent {

  VerifiablePresentation check(SignedJWT jwtWithVerifiablePresentation, String expectedAudience);

  VerifiableCredential getByType(String verifiableCredentialType);

  SignedJWT createVerifiablePresentationAsJwt(
      List<VerifiableCredential> credentials, String audience);
}
