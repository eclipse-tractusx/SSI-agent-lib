package org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions;

public class SigningKeyNotFoundException extends Exception {
  public SigningKeyNotFoundException(String alias) {
    super(String.format("Could not resolve private or public key from vault: %s", alias));
  }
}
