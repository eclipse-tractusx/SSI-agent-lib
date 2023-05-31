package org.eclipse.tractusx.ssi.examples;

import com.nimbusds.jwt.SignedJWT;
import java.net.http.HttpClient;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebDocumentResolver;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.JwtException;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofValidation;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolverRegistryImpl;

public class Verification {

  public static void verifyJWT(SignedJWT jwt) {
    // DID Resolver Constracture params
    DidWebParser didParser = new DidWebParser();
    var httpClient = HttpClient.newHttpClient();
    var enforceHttps = false;

    var didDocumentResolverRegistry = new DidDocumentResolverRegistryImpl();
    didDocumentResolverRegistry.register(
        new DidWebDocumentResolver(httpClient, didParser, enforceHttps));

    SignedJwtVerifier jwtVerifier = new SignedJwtVerifier(didDocumentResolverRegistry);
    try {
      jwtVerifier.verify(jwt);
    } catch (JwtException | DidDocumentResolverNotRegisteredException e) {
      // An ecxeption will be thrown here in case JWT verification failed or DID
      // Document Resolver not able to resolve.
      e.printStackTrace();
    }
  }

  public static boolean verifyLD(VerifiableCredential verifiableCredential) {
    // DID Resolver Constracture params
    DidWebParser didParser = new DidWebParser();
    var httpClient = HttpClient.newHttpClient();
    var enforceHttps = false;

    var didDocumentResolverRegistry = new DidDocumentResolverRegistryImpl();
    didDocumentResolverRegistry.register(
        new DidWebDocumentResolver(httpClient, didParser, enforceHttps));

    LinkedDataProofValidation proofValidation =
        LinkedDataProofValidation.newInstance(didDocumentResolverRegistry);
    return proofValidation.checkProof(verifiableCredential);
  }
}
