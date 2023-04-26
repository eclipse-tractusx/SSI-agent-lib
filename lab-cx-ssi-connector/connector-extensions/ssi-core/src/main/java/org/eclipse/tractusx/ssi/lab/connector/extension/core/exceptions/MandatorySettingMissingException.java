package org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions;

public class MandatorySettingMissingException extends Exception {

  public MandatorySettingMissingException(String settingName) {
    super(String.format("SSI Settings: Configuration of %s is mandatory", settingName));
  }
}
