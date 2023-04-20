package org.eclipse.tractusx.ssi.agent.lib.exceptions;

import java.util.List;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;

public class SsiWalletNotFoundException extends SsiException {

  public SsiWalletNotFoundException(
      String notFoundWalletIdentifier, List<String> foundWalletIdentifier) {

    super(
        String.format(
            "Wallet not found. Requested identifier: %s. Supported identifier: %s",
            notFoundWalletIdentifier, String.join(", ", foundWalletIdentifier)));
  }
}
