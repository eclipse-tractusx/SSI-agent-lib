package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.webagent.setting;

import lombok.Value;

@Value
public class WebAgentSettings {

  public static final String SETTING_DID_HOST_NAME = "edc.ssi.agent.embedded.host.name";
  public static final String SETTING_SSI_DID_WEB_RESOLVE_ENFORCE_HTTPS =
      "edc.ssi.agent.embedded.did.web.resolve.enforce.https";
  public static final String SETTING_SSI_SIGNING_PUBLIC_KEY_ALIAS =
      "edc.ssi.agent.embedded.signing.private.key.alias";
  public static final String SETTING_SSI_SIGNING_PRIVATE_KEY_ALIAS =
      "edc.ssi.agent.embedded.signing.public.key.alias";

  String hostName;
  String privateKeyAlias;
  String publicKeyAlias;
  boolean enforceHttpsToResolveDidWeb;
}
