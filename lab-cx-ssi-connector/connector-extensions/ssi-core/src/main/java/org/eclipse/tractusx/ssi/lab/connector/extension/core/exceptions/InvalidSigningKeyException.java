package org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions;

public class InvalidSigningKeyException extends Exception {

  public InvalidSigningKeyException(Exception e) {
    super("SSI Extension: Invalid signing key", e);
  }
}
