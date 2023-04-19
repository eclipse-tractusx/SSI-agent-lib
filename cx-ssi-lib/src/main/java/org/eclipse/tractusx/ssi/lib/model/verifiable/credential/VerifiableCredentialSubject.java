package org.eclipse.tractusx.ssi.lib.model.verifiable.credential;

import java.util.HashMap;
import java.util.Map;

public class VerifiableCredentialSubject extends HashMap<String, Object> {

  public VerifiableCredentialSubject() {
    super();
  }

  public VerifiableCredentialSubject(Map<String, Object> json) {
    super(json);
  }
}
