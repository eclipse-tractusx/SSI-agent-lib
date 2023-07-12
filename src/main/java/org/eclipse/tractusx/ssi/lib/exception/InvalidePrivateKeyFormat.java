package org.eclipse.tractusx.ssi.lib.exception;

public class InvalidePrivateKeyFormat extends Exception {
  public InvalidePrivateKeyFormat(int correctLength, int providedLength) {
    super(
        String.format(
            "Invalide Private Key Format, this key should have '%s' as lenght but we got %s",
            correctLength, providedLength));
  }

  public InvalidePrivateKeyFormat(Throwable cause) {}
}
