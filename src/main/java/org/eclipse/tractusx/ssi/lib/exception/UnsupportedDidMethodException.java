package org.eclipse.tractusx.ssi.lib.exception;

import org.eclipse.tractusx.ssi.lib.model.did.Did;

public class UnsupportedDidMethodException extends Exception {
  public UnsupportedDidMethodException(Did did, String contextMessage) {
    super(
        String.format(
            "No DID document resolver registered for DID method '%s'. %s",
            did.getMethod(), contextMessage));
  }
}
