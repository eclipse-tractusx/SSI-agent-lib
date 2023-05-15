package org.eclipse.tractusx.ssi.lib.exception;

import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;

public class DidDocumentResolverNotRegisteredException extends Exception {

  public DidDocumentResolverNotRegisteredException(DidMethod didMethod) {
    super(String.format("No DID document resolver registered for DID method '%s'", didMethod));
  }
}
