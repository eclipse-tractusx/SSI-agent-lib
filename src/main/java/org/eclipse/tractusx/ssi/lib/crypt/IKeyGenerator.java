package org.eclipse.tractusx.ssi.lib.crypt;

import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;

public interface IKeyGenerator {
  KeyPair generateKey() throws KeyGenerationException;
}
