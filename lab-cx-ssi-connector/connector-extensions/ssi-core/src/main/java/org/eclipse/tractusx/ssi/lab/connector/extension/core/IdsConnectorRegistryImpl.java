package org.eclipse.tractusx.ssi.lab.connector.extension.core;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import lombok.Value;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.registry.IdsConnector;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.registry.IdsConnectorRegistry;

public class IdsConnectorRegistryImpl implements IdsConnectorRegistry {

  private final Map<URI, IdsConnector> idsConnectorMap = new HashMap<>();

  @Override
  public void register(URI did, URI idsEndpoint) {
    idsConnectorMap.put(did, new IdsConnectorImpl(did, idsEndpoint));
  }

  @Override
  public IdsConnector get(URI did) {
    return idsConnectorMap.get(did);
  }

  @Value
  private static class IdsConnectorImpl implements IdsConnector {
    URI did;
    URI idsEndpoint;
  }
}
