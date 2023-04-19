package org.eclipse.tractusx.ssi.lib.crypt.ed25519;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Ed25519KeySet {
  byte[] privateKey;
  byte[] publicKey;
}
