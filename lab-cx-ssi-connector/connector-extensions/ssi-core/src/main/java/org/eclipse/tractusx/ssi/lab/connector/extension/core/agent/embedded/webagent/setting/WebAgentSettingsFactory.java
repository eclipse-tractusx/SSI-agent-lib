package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.webagent.setting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.settings.SettingsFactory;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.MandatorySettingMissingException;

@RequiredArgsConstructor
public class WebAgentSettingsFactory implements SettingsFactory<WebAgentSettings> {

  @Getter private final ServiceExtensionContext context;

  @Override
  public WebAgentSettings createSettings() throws MandatorySettingMissingException {

    final String hostName = getMandatorySettingOrThrow(WebAgentSettings.SETTING_DID_HOST_NAME);
    final String privateKeyAlias =
        getMandatorySettingOrThrow(WebAgentSettings.SETTING_SSI_SIGNING_PRIVATE_KEY_ALIAS);
    final String publicKeyAlias =
        getMandatorySettingOrThrow(WebAgentSettings.SETTING_SSI_SIGNING_PUBLIC_KEY_ALIAS);

    final boolean enforceHttps =
        context.getSetting(WebAgentSettings.SETTING_SSI_DID_WEB_RESOLVE_ENFORCE_HTTPS, true);

    return new WebAgentSettings(hostName, privateKeyAlias, publicKeyAlias, enforceHttps);
  }
}
