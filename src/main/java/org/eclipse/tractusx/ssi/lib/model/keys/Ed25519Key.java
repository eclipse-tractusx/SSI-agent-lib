package org.eclipse.tractusx.ssi.lib.model.keys;

import lombok.Value;

@Value
public class Ed25519Key {
  byte[] encoded;
}
