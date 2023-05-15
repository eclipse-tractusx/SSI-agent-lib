package org.eclipse.tractusx.ssi.lib.exception;

public class JwtException extends Exception {

  public JwtException(String message) {
    super(message);
  }

  public JwtException(String message, Throwable cause) {
    super(message, cause);
  }

  public JwtException(Throwable cause) {
    super(cause);
  }
}
