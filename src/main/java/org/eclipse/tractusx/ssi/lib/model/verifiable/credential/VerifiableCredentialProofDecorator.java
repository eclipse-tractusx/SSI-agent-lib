package org.eclipse.tractusx.ssi.lib.model.verifiable.credential;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class VerifiableCredentialProofDecorator {

  @NonNull
  @EqualsAndHashCode.Include
  private final String rawData;
}
