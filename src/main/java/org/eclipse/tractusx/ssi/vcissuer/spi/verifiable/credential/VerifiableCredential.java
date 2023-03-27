package org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential;

import lombok.*;
import org.eclipse.tractusx.ssi.vcissuer.spi.Ed25519Proof;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@Value
@EqualsAndHashCode
@Builder
@ToString
public class VerifiableCredential {

  @NonNull URI id;

  @NonNull List<String> types;

  @NonNull URI issuer;

  @NonNull Instant issuanceDate;

  @NonNull Instant expirationDate;

  @NonNull @Builder.Default
  public VerifiableCredentialSubject credentialSubject = new VerifiableCredentialSubject();

  Ed25519Proof proof;
  VerifiableCredentialStatus credentialStatus;
}
