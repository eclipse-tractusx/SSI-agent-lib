
package org.eclipse.tractusx.ssi.lab.connector.extension.spi.exceptions;

public class SsiException extends RuntimeException {

  public SsiException() {}

  public SsiException(String message) {
    super(message);
  }

  public SsiException(String message, Throwable cause) {
    super(message, cause);
  }

  public SsiException(Throwable cause) {
    super(cause);
  }

  public SsiException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
