package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.settings;

import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.MandatorySettingMissingException;

public interface SettingsFactory<T> {
  T createSettings() throws MandatorySettingMissingException;

  ServiceExtensionContext getContext();

  default String getMandatorySettingOrThrow(String settingName)
      throws MandatorySettingMissingException {

    final ServiceExtensionContext context = getContext();

    final String settingValue = context.getSetting(settingName, null);
    if (settingValue == null) {
      throw new MandatorySettingMissingException(settingName);
    }
    return settingValue;
  }
}
