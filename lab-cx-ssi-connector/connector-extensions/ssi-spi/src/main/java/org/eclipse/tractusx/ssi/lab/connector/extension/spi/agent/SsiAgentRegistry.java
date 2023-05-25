package org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent;

public interface SsiAgentRegistry {

  SsiAgent getConfiguredAgent();

  void registerAgentFactory(SsiAgentFactory agentProvider);
}
