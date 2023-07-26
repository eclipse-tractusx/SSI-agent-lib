package org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable;

import java.util.HashMap;
import java.util.Map;

public class VerifiableCredentialSubject extends HashMap<String, Object> {

  public VerifiableCredentialSubject(Map<String, Object> json) {
    super(json);
  }
}
