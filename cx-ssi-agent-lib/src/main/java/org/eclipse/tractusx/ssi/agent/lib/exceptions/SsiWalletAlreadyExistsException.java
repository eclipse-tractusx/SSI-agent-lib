package org.eclipse.tractusx.ssi.agent.lib.exceptions;

import org.eclipse.tractusx.ssi.lib.exception.SsiException;

public class SsiWalletAlreadyExistsException extends SsiException {

  public SsiWalletAlreadyExistsException(String walletIdentifier) {
    super(String.format("Wallet with identifier %s is already registered", walletIdentifier));
  }
}
