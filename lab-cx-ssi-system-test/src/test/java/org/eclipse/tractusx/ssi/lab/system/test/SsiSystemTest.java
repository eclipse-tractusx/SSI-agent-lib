package org.eclipse.tractusx.ssi.lab.system.test;

import java.util.List;
import org.eclipse.tractusx.ssi.lab.system.test.client.Connector;
import org.eclipse.tractusx.ssi.lab.system.test.client.SsiAgent;
import org.eclipse.tractusx.ssi.lab.system.test.client.agent.AgentClient;
import org.eclipse.tractusx.ssi.lab.system.test.client.config.TestConfiguration;
import org.eclipse.tractusx.ssi.lab.system.test.client.connector.ConnectorClient;
import org.eclipse.tractusx.ssi.lab.system.test.client.connector.data.ContractOffer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SsiSystemTest {

  @Test
  public void runSystemTest() {
    final TestConfiguration testConfiguration = new TestConfiguration();
    final Connector alice = testConfiguration.readAliceConfig();
    final Connector bob = testConfiguration.readBobConfig();
    final SsiAgent operator = testConfiguration.readOperatorConfig();

    final AgentClient agentClient = new AgentClient(operator);
    final ConnectorClient aliceClient = new ConnectorClient(alice);
    final ConnectorClient bobClient = new ConnectorClient(bob);

    final org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential
        aliceMembership = agentClient.issueMembershipCredential(alice);
    aliceClient.storeCredential(aliceMembership);

    final org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential
        bobMembership = agentClient.issueMembershipCredential(bob);
    bobClient.storeCredential(bobMembership);

    final List<ContractOffer> aliceCatalog = bobClient.requestCatalog(alice);
    Assertions.assertNotNull(aliceCatalog);
  }
}
