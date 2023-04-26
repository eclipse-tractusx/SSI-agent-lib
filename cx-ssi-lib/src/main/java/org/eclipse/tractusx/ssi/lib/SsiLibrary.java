package org.eclipse.tractusx.ssi.lib;

import java.security.Security;
import net.i2p.crypto.eddsa.EdDSASecurityProvider;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public final class SsiLibrary {
  public static void initialize() {
    Security.addProvider(new EdDSASecurityProvider());
    Security.addProvider(new BouncyCastleProvider());
  }
}
