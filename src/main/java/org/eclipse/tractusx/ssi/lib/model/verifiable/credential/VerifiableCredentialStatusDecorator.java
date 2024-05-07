package org.eclipse.tractusx.ssi.lib.model.verifiable.credential;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class VerifiableCredentialStatusDecorator {

  @NonNull
  @EqualsAndHashCode.Include
  private final String rawData;
}
