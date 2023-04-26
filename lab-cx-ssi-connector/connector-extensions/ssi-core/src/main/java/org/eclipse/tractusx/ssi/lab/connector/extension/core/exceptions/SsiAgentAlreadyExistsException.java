package org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions;

import org.eclipse.tractusx.ssi.lab.connector.extension.spi.exceptions.SsiException;

public class SsiAgentAlreadyExistsException extends SsiException {

  public SsiAgentAlreadyExistsException(String walletIdentifier) {
    super(String.format("Agent with identifier %s is already registered", walletIdentifier));
  }
}
