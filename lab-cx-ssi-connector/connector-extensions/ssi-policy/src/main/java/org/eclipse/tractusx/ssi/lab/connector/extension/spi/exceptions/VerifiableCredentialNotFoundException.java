package org.eclipse.tractusx.ssi.lab.connector.extension.spi.exceptions;

import java.util.List;
import lombok.Getter;

public class VerifiableCredentialNotFoundException extends Exception {

  @Getter private final List<String> notFoundCredentials;

  public VerifiableCredentialNotFoundException(String type) {
    super("VerifiableCredential of type " + type + " not found");
    notFoundCredentials = List.of(type);
  }

  public VerifiableCredentialNotFoundException(List<String> types) {
    super("VerifiableCredential of type [" + String.join(", ", types) + "] not found");
    notFoundCredentials = types;
  }
}
