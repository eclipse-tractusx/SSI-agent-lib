<<<<<<<< HEAD:lab-cx-ssi-connector/connector-extensions/ssi-spi/src/main/java/org/eclipse/tractusx/ssi/lab/connector/extension/spi/exceptions/SsiException.java
package org.eclipse.tractusx.ssi.lab.connector.extension.spi.exceptions;
========
package org.eclipse.tractusx.ssi.lib.exception;
>>>>>>>> main:cx-ssi-lib/src/main/java/org/eclipse/tractusx/ssi/lib/exception/SsiException.java

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
