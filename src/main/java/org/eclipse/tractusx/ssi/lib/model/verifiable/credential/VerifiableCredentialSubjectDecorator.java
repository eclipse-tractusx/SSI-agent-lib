package org.eclipse.tractusx.ssi.lib.model.verifiable.credential;


import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import java.net.URI;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
public class VerifiableCredentialSubjectDecorator {

  @NonNull
  @EqualsAndHashCode.Include
  @ToString.Exclude
  private final String expanded;

  @EqualsAndHashCode.Exclude
  private final URI id;
}
