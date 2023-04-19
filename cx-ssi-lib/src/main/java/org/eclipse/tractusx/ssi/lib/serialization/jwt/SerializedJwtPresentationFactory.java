package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.util.List;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.keys.Ed25519Key;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

public interface SerializedJwtPresentationFactory {
  SignedJWT createPresentation(
      Did issuer, List<VerifiableCredential> credentials, String audience, Ed25519Key signingKey);
}
