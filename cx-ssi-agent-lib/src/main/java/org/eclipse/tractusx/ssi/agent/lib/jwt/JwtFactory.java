package org.eclipse.tractusx.ssi.agent.lib.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.util.List;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519Key;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.did.resolver.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedJwtPresentationFactory;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedJwtPresentationFactoryImpl;

public class JwtFactory {

  private final SerializedJwtPresentationFactory presentationFactory;
  private final Did issuer;
  private final Ed25519Key signingKey;

  public JwtFactory(Did issuer, Ed25519Key signingKey) {
    this.issuer = issuer;
    this.signingKey = signingKey;
    this.presentationFactory =
        new SerializedJwtPresentationFactoryImpl(
            new SignedJwtFactory(new OctetKeyPairFactory()), new JsonLdSerializerImpl(), issuer);
  }

  public SignedJWT createJwt(List<VerifiableCredential> credentials, String audience) {
    return presentationFactory.createPresentation(
        this.issuer, credentials, audience, this.signingKey);
  }
}
