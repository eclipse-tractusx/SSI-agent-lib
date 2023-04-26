package org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent;

public interface SsiAgentRegistry {

  SsiAgent getAgent(String agentIdentifier);

  SsiAgent getConfiguredAgent();

  void registerAgentFactory(SsiAgentFactory agentProvider);
}
