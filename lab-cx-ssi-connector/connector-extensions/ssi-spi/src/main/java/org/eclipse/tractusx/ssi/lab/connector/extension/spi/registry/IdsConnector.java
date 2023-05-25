package org.eclipse.tractusx.ssi.lab.connector.extension.spi.registry;

import java.net.URI;

public interface IdsConnector {
  URI getDid();

  URI getIdsEndpoint();
}
