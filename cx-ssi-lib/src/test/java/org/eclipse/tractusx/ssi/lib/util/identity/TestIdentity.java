package org.eclipse.tractusx.ssi.lib.util.identity;

import lombok.Value;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;

@Value
public class TestIdentity {
  Did did;
  DidDocument didDocument;
  byte[] publicKey;
  byte[] privateKey;
}
