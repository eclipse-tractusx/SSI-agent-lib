package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.keys.Ed25519Key;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationType;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdSerializer;

@RequiredArgsConstructor
public class SerializedJwtPresentationFactoryImpl implements SerializedJwtPresentationFactory {

  private final SignedJwtFactory signedJwtFactory;
  private final JsonLdSerializer jsonLdSerializer;
  private final Did agentDid;

  @Override
  public SignedJWT createPresentation(
      Did issuer, List<VerifiableCredential> credentials, String audience, Ed25519Key signingKey) {
    final VerifiablePresentation verifiablePresentation =
        VerifiablePresentation.builder()
            .id(
                URI.create(
                    agentDid.toUri()
                        + "#"
                        + UUID.randomUUID())) // TODO This is possible, but there are better ways to
            // generate this ID
            .holder(agentDid.toUri())
            .types(List.of(VerifiablePresentationType.VERIFIABLE_PRESENTATION))
            .verifiableCredentials(credentials)
            .build();

    final SerializedVerifiablePresentation serializedVerifiablePresentation =
        jsonLdSerializer.serializePresentation(verifiablePresentation);

    return signedJwtFactory.create(issuer, audience, serializedVerifiablePresentation, signingKey);
  }
}
