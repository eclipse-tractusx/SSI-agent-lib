package org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable;

import java.net.URI;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class VerifiablePresentation {
  @NonNull URI id;
  @NonNull List<String> types;
  @NonNull List<VerifiableCredential> verifiableCredentials;
}
