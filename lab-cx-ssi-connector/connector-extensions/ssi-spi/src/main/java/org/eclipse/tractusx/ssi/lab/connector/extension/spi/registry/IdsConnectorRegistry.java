package org.eclipse.tractusx.ssi.lab.connector.extension.spi.registry;

import java.net.URI;

public interface IdsConnectorRegistry {

  void register(URI did, URI idsEndpoint);

  IdsConnector get(URI did);
}
