package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.settings;

import java.net.URI;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class SsiSettings {
  public static final String SETTING_AGENT_TYPE = "edc.ssi.agent";
  public static final String SETTING_DATASPACE_OPERATOR = "edc.ssi.dataspace.operator";

  String agentType;
  URI dataspaceOperator;
}
