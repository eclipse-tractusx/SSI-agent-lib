package org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.net.URI;

@Value
@Builder
@EqualsAndHashCode
public class VerifiableCredentialStatus {
  URI id;
  String type;
}
