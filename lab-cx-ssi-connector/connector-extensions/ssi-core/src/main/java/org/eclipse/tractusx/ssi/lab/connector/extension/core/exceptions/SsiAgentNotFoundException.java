package org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions;

import java.util.List;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.exceptions.SsiException;

public class SsiAgentNotFoundException extends SsiException {

  public SsiAgentNotFoundException(
      String notFoundWalletIdentifier, List<String> foundWalletIdentifier) {

    super(
        String.format(
            "Agent not found. Requested identifier: %s. Supported identifier: [%s]",
            notFoundWalletIdentifier, String.join(", ", foundWalletIdentifier)));
  }
}
