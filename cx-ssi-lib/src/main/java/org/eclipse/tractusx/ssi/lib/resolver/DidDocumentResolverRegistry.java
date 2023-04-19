package org.eclipse.tractusx.ssi.lib.resolver;

import org.eclipse.tractusx.ssi.lib.exception.UnsupportedDidMethodException;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;

public interface DidDocumentResolverRegistry {
  DidDocumentResolver get(DidMethod did) throws UnsupportedDidMethodException;

  void register(DidDocumentResolver resolver);

  void unregister(DidDocumentResolver resolver);
}
