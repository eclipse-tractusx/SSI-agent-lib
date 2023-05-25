package org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent;

public interface SsiAgentFactory {
  String getIdentifier();

  SsiAgent createAgent();
}
