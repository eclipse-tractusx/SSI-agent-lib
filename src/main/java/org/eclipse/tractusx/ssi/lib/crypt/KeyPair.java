package org.eclipse.tractusx.ssi.lib.crypt;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class KeyPair {
  private final IPublicKey publicKey;
  private final IPrivateKey privateKey;
}
