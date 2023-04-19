package org.eclipse.tractusx.ssi.lib.did.web;

import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;

public class DidWebFactory {

  public static Did fromHostname(String hostName) {
    if (hostName.contains("http"))
      throw new IllegalArgumentException("Hostname should not contain http(s)://");

    final DidMethod didMethod = new DidMethod("web");
    final DidMethodIdentifier methodIdentifier =
        new DidMethodIdentifier(hostName.replace(":", "%3A").replace("/", ":"));

    return new Did(didMethod, methodIdentifier);
  }
}
