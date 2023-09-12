package org.eclipse.tractusx.ssi.lib.exception;

public class InvalidePublicKeyFormat extends Exception {

  public InvalidePublicKeyFormat(int correctLength, int providedLength) {
    super(
        String.format(
            "Invalide Publice Key Format, this key should have '%s' as lenght but we got %s",
            correctLength, providedLength));
  }

  public InvalidePublicKeyFormat(Throwable cause) {}
}
