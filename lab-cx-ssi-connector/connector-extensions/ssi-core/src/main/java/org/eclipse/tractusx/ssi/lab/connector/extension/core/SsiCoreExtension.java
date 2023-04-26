package org.eclipse.tractusx.ssi.lab.connector.extension.core;

import lombok.SneakyThrows;
import org.eclipse.edc.runtime.metamodel.annotation.Provides;
import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.security.Vault;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.tractusx.ssi.agent.lib.WebAgent;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.EmbeddedAgentFactory;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.controller.DidWebDocumentController;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.controller.EmbeddedAgentController;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded.webagent.WebAgentFactory;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.registry.SsiAgentRegistryImpl;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.settings.SsiSettings;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.settings.SsiSettingsFactory;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.InvalidSigningKeyException;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.MandatorySettingMissingException;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.SigningKeyNotFoundException;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgentRegistry;

@Provides({SsiAgentRegistry.class})
@Requires({WebService.class, Vault.class})
public class SsiCoreExtension implements ServiceExtension {
  public static final String EXTENSION_NAME = "SSI Core Extension";
  public static final String DOCUMENT_CONTEXT = "wellknown";
  public static final String AGENT_CONTEXT = "ssi";

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  private SsiAgentRegistry registry;
  private WebAgent agent;

  @Override
  public void start() {
    final EmbeddedAgentFactory factory = new EmbeddedAgentFactory(agent);
    registry.registerAgentFactory(factory);
  }

  @Override
  @SneakyThrows({
    MandatorySettingMissingException.class,
    InvalidSigningKeyException.class,
    SigningKeyNotFoundException.class
  })
  public void initialize(ServiceExtensionContext context) {

    //        HashicorpVaultVaultExtension vaultExtension = new HashicorpVaultVaultExtension();
    //        vaultExtension.initialize(context);

    final Monitor monitor = context.getMonitor();

    // ssi agent
    final Vault vault = context.getService(Vault.class);
    final WebAgentFactory factory = new WebAgentFactory(context, vault);
    this.agent = factory.createAgent();

    // web service
    final WebService webService = context.getService(WebService.class);
    final DidWebDocumentController didWebDocumentController = new DidWebDocumentController(agent);
    final EmbeddedAgentController embeddedAgentController =
        new EmbeddedAgentController(agent, monitor);
    webService.registerResource(DOCUMENT_CONTEXT, didWebDocumentController);
    webService.registerResource(AGENT_CONTEXT, embeddedAgentController);

    // ssi agent registry
    final SsiSettingsFactory settingsFactory = new SsiSettingsFactory(context);
    final SsiSettings settings = settingsFactory.createSettings();

    this.registry = new SsiAgentRegistryImpl(settings.getAgentType());
    context.registerService(SsiAgentRegistry.class, this.registry);
  }
}
