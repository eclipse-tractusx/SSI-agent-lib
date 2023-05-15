package org.eclipse.tractusx.ssi.lib.exception;

public class InvalidJsonLdException extends Exception {
  public InvalidJsonLdException(String message) {
    super(message);
  }

  public InvalidJsonLdException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidJsonLdException(Throwable cause) {
    super(cause);
  }
}
