package org.eclipse.tractusx.ssi.lib.util.identity;

import java.util.UUID;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;

public class TestDidFactory {
  public static final DidMethod DID_METHOD = new DidMethod("test");

  public static Did createRandom() {
    return new Did(DID_METHOD, new DidMethodIdentifier(UUID.randomUUID().toString()));
  }
}
