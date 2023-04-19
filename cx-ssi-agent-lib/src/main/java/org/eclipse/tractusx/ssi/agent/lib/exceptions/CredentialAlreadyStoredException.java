package org.eclipse.tractusx.ssi.agent.lib.exceptions;

import java.net.URI;
import lombok.Getter;

public class CredentialAlreadyStoredException extends Exception {

  private static final String MESSAGE = "Credential with id %s already stored in wallet";

  @Getter private final URI credentialId;

  public CredentialAlreadyStoredException(URI credentialId) {
    super(String.format(MESSAGE, credentialId));
    this.credentialId = credentialId;
  }
}
