package org.eclipse.tractusx.ssi.lib.model.verifiable.credential;

import lombok.*;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@ToString
public class VerifiableCredentialDecorator {

  @NonNull
  @ToString.Exclude
  private final String rawData;

  @NonNull
  @EqualsAndHashCode.Exclude
  private final RawFormat rawFormat;

  @NonNull
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private final String expandedData;

  @NonNull
  @EqualsAndHashCode.Exclude
  private final URI id;

  @NonNull
  @EqualsAndHashCode.Exclude
  private final List<String> verifiableCredentialTypes = List.of();

  @NonNull
  @EqualsAndHashCode.Exclude
  private final URI issuer;

  @NonNull
  @EqualsAndHashCode.Exclude
  private final OffsetDateTime issuanceDate;

  @NonNull
  @EqualsAndHashCode.Exclude
  private final List<VerifiableCredentialSubjectDecorator> subjects;

  @EqualsAndHashCode.Exclude
  private final List<VerifiableCredentialProofDecorator> proofs;

  @EqualsAndHashCode.Exclude
  private final VerifiableCredentialStatusDecorator status;

  @EqualsAndHashCode.Exclude
  private final OffsetDateTime expirationDate;

  public enum RawFormat {
    JSON_LD,
    JWT,
    JWT_BEARER
  }
}
