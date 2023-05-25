package org.eclipse.tractusx.ssi.lab.connector.extension.spi.message;

import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;

@Getter
@RequiredArgsConstructor
public class VerifiableCredentialRequest implements RemoteMessage {
  // TODO Change to URI
  private final String connectorAddress;
  private final String protocol;
  private final List<String> requestedVerifiableCredentialTypes;
}
