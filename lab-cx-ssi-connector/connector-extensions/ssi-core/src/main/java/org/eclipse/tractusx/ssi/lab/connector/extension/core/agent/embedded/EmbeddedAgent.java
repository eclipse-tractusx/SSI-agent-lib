package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded;

import com.nimbusds.jwt.SignedJWT;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.agent.lib.WebAgent;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgent;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiableCredential;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiablePresentation;

@RequiredArgsConstructor
public class EmbeddedAgent implements SsiAgent {

  private final WebAgent webAgent;

  @Override
  public VerifiablePresentation check(
      SignedJWT jwtWithVerifiablePresentation, String expectedAudience) {
    return map(webAgent.readCredentials(jwtWithVerifiablePresentation, expectedAudience));
  }

  @Override
  public VerifiableCredential getByType(String verifiableCredentialType) {
    final var credentials = webAgent.getAllCredentials();
    return credentials.stream()
        .filter(c -> c.getTypes().contains(verifiableCredentialType))
        .findFirst()
        .map(this::map)
        .orElse(null);
  }

  @Override
  public SignedJWT createVerifiablePresentationAsJwt(
      List<VerifiableCredential> credentials, String audience) {
    return webAgent.issuePresentationAsJwt(
        credentials.stream().map(this::map).collect(Collectors.toList()), audience);
  }

  private VerifiablePresentation map(
      org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation
          presentation) {
    return VerifiablePresentation.builder()
        .id(presentation.getId())
        .types(presentation.getTypes())
        .verifiableCredentials(
            presentation.getVerifiableCredentials().stream()
                .map(this::map)
                .collect(Collectors.toList()))
        .build();
  }

  private org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential map(
      VerifiableCredential credential) {
    return new org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential(
        credential);
  }

  private VerifiableCredential map(
      org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential credential) {
    return new VerifiableCredential(credential);
  }

  private VerifiableCredentialSubject map(
      org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject
          credentialSubject) {
    return new VerifiableCredentialSubject(credentialSubject);
  }

  private org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject map(
      VerifiableCredentialSubject credentialSubject) {
    return new org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject(
        credentialSubject);
  }
}
