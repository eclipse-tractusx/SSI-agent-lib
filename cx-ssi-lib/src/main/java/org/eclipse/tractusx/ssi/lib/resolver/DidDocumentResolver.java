package org.eclipse.tractusx.ssi.lib.resolver;

import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;

public interface DidDocumentResolver {

  DidMethod getSupportedMethod();

  DidDocument resolve(Did did);
}
