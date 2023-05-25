package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.settings;

import java.net.URI;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.MandatorySettingMissingException;

@RequiredArgsConstructor
public class SsiSettingsFactory implements SettingsFactory<SsiSettings> {

  @Getter private final ServiceExtensionContext context;

  @Override
  public SsiSettings createSettings() throws MandatorySettingMissingException {

    final String agentType = getMandatorySettingOrThrow(SsiSettings.SETTING_AGENT_TYPE);
    final String operator = getMandatorySettingOrThrow(SsiSettings.SETTING_DATASPACE_OPERATOR);

    return SsiSettings.builder()
        .agentType(agentType)
        .dataspaceOperator(URI.create(operator))
        .build();
  }
}
