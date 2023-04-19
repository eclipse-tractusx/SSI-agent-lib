package org.eclipse.tractusx.ssi.lib.model.verifiable.credential;

import java.net.URI;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode
public class VerifiableCredentialStatus {
  URI id;
  String type;
}
