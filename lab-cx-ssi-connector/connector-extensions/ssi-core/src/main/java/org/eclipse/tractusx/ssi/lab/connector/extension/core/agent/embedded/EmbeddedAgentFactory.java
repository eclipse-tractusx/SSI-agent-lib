package org.eclipse.tractusx.ssi.lab.connector.extension.core.agent.embedded;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.agent.lib.WebAgent;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgent;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgentFactory;

@RequiredArgsConstructor
public class EmbeddedAgentFactory implements SsiAgentFactory {

  /**
   * Unique identifier of the Agent. Changing the Identifier of the Agent is a breaking change and
   * must be documented accordingly.
   */
  @Getter public final String Identifier = "embedded";

  private final WebAgent agent;

  @Override
  public SsiAgent createAgent() {
    return new EmbeddedAgent(agent);
  }
}
