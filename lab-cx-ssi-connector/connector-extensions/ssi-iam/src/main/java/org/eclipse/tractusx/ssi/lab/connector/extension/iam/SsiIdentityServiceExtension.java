package org.eclipse.tractusx.ssi.lab.connector.extension.iam;

import java.net.URI;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.iam.IdentityService;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgentRegistry;

// This class does not really require an SSI agent via ServiceExtensionContext,
// this is a workaround to prevent race conditions when the SsiAgentRegistry is being initialized.
// The extensions, that fill the registry, are "providing" an agent, the classes that use the
// registry are "requiring" an agent.
@Requires({SsiAgentRegistry.class})
@Provides({IdentityService.class})
public class SsiIdentityServiceExtension implements ServiceExtension {
  public static final String EXTENSION_NAME = "SSI Identity Service Extension";

  // TODO Move this setting to core
  public static final String SETTING_AGENT_IDENTIFIER = "edc.ssi.agent";
  // TODO Make it possible to configure a list of operators
  public static final String SETTING_DATASPACE_OPERATOR = "edc.ssi.dataspace.operator";

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    final Monitor monitor = context.getMonitor();
    final SsiAgentRegistry agentRegistry = context.getService(SsiAgentRegistry.class);

    final String membershipIssuer = context.getSetting(SETTING_DATASPACE_OPERATOR, null);
    final URI dataSpaceOperatorDid;
    if (membershipIssuer == null) {
      dataSpaceOperatorDid = URI.create("did:null:null");
      monitor.warning(
          String.format(
              "No membership credential issuer configured in setting '%s'. Using default issuer '%s'. The connector will not be able to communicate to other connectors without a configured trusted issuer of membership credentials.",
              SETTING_AGENT_IDENTIFIER, dataSpaceOperatorDid));
    } else {
      dataSpaceOperatorDid = URI.create(membershipIssuer);
    }

    monitor.info("Trusted Data Space Operator: " + dataSpaceOperatorDid);

    final IdentityService identityService =
        new SsiIdentityService(agentRegistry, dataSpaceOperatorDid, monitor);

    context.registerService(IdentityService.class, identityService);
  }
}
