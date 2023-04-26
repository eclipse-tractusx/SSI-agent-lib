package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.SsiAgentAlreadyExistsException;
import org.eclipse.tractusx.ssi.lab.connector.extension.core.exceptions.SsiAgentNotFoundException;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgent;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgentFactory;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgentRegistry;

@RequiredArgsConstructor
public class SsiAgentRegistryImpl implements SsiAgentRegistry {

  private final String configuredAgentIdentifier;

  private final Map<String, SsiAgent> singletonAgentInstances = new HashMap<>();
  private final List<SsiAgentFactory> agentFactory = new ArrayList<>();

  @Override
  public SsiAgent getAgent(String agentIdentifier) {
    return resolveSingletonAgent(agentIdentifier);
  }

  @Override
  public SsiAgent getConfiguredAgent() {
    return resolveSingletonAgent(configuredAgentIdentifier);
  }

  @Override
  public void registerAgentFactory(SsiAgentFactory ssiAgentFactory) {
    if (ssiAgentFactory.getIdentifier() == null) {
      throw new IllegalArgumentException("Agent identifier must not be null");
    }

    agentFactory.stream()
        .filter(a -> a.getIdentifier().equals(ssiAgentFactory.getIdentifier()))
        .findAny()
        .ifPresentOrElse(
            a -> {
              throw new SsiAgentAlreadyExistsException(ssiAgentFactory.getIdentifier());
            },
            () -> this.agentFactory.add(ssiAgentFactory));
  }

  private SsiAgent resolveSingletonAgent(String identifier) {
    if (!singletonAgentInstances.containsKey(identifier)) {
      loadAgentSynchronized(identifier);
    }

    return singletonAgentInstances.get(identifier);
  }

  @Synchronized
  private void loadAgentSynchronized(String identifier) {
    final SsiAgent agent =
        agentFactory.stream()
            .filter(a -> a.getIdentifier().equals(identifier))
            .map(SsiAgentFactory::createAgent)
            .findFirst()
            .orElseThrow(
                () ->
                    new SsiAgentNotFoundException(
                        identifier,
                        agentFactory.stream()
                            .map(SsiAgentFactory::getIdentifier)
                            .collect(Collectors.toList())));

    singletonAgentInstances.put(identifier, agent);
  }
}
