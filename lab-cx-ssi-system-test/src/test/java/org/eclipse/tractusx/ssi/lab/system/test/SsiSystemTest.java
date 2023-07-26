package org.eclipse.tractusx.ssi.lab.system.test;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.ssi.lab.system.test.client.Connector;
import org.eclipse.tractusx.ssi.lab.system.test.client.SsiAgent;
import org.eclipse.tractusx.ssi.lab.system.test.client.agent.AgentClient;
import org.eclipse.tractusx.ssi.lab.system.test.client.config.TestConfiguration;
import org.eclipse.tractusx.ssi.lab.system.test.client.connector.ConnectorClient;
import org.eclipse.tractusx.ssi.lab.system.test.client.connector.data.ContractOffer;
import org.eclipse.tractusx.ssi.lab.system.test.client.connector.data.VerifiableCredentialConstraint;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class SsiSystemTest {
  TestConfiguration testConfiguration;
  Connector alice;
  Connector bob;
  SsiAgent operator;
  AgentClient agentClient;
  ConnectorClient aliceClient;
  ConnectorClient bobClient;

  @BeforeEach
  public void setup() {
    testConfiguration = new TestConfiguration();
    alice = testConfiguration.readAliceConfig();
    bob = testConfiguration.readBobConfig();
    operator = testConfiguration.readOperatorConfig();

    agentClient = new AgentClient(operator);
    aliceClient = new ConnectorClient(alice);
    bobClient = new ConnectorClient(bob);
  }

  @Test
  public void runSystemTest() {
    // issue membership credential for bob & alice
    final org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential
        aliceMembership = agentClient.issueMembershipCredential(alice);
    aliceClient.storeCredential(aliceMembership);

    final org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential
        bobMembership = agentClient.issueMembershipCredential(bob);
    bobClient.storeCredential(bobMembership);

    // request catalog with bob
    // assert: catalog is empty
    List<ContractOffer> aliceCatalog = bobClient.requestCatalog(alice);
    Assertions.assertNotNull(aliceCatalog);
    Assertions.assertEquals(0, aliceCatalog.size(), "Catalog should be empty");

    // create BPNFOO contract offer
    final VerifiableCredentialConstraint bpnFooConstraint =
        VerifiableCredentialConstraint.builder()
            .credentialType(VerifiableCredentialType.MEMBERSHIP_CREDENTIAL)
            .paths(
                List.of(
                    new VerifiableCredentialConstraint.JsonPathEntry(
                        "$.credentialSubject.bpn", "BPNFOO")))
            .build();
    aliceClient.createContractOffer(bpnFooConstraint);

    // create BPNBOB contract offer
    final VerifiableCredentialConstraint bpnBobConstraint =
        VerifiableCredentialConstraint.builder()
            .credentialType(VerifiableCredentialType.MEMBERSHIP_CREDENTIAL)
            .paths(
                List.of(
                    new VerifiableCredentialConstraint.JsonPathEntry(
                        "$.credentialSubject.bpn", "BPNBOB")))
            .build();
    aliceClient.createContractOffer(bpnBobConstraint);

    // create vehicle-dismantle-offer
    final VerifiableCredentialConstraint vehicleDismantleConstraint =
        VerifiableCredentialConstraint.builder()
            .credentialType("DismantlerCredential")
            .paths(
                List.of(
                    new VerifiableCredentialConstraint.JsonPathEntry(
                        "$.credentialSubject.activity.activityType", "vehicleDismantle")))
            .build();
    aliceClient.createContractOffer(vehicleDismantleConstraint);

    // create tire-dismantle-offer
    final VerifiableCredentialConstraint tireDismantleConstraint =
        VerifiableCredentialConstraint.builder()
            .credentialType("DismantlerCredential")
            .paths(
                List.of(
                    new VerifiableCredentialConstraint.JsonPathEntry(
                        "$.credentialSubject.activity.activityType", "tireDismantle")))
            .build();
    aliceClient.createContractOffer(tireDismantleConstraint);

    // request catalog with bob
    // assert: catalog size == 1
    aliceCatalog = bobClient.requestCatalog(alice);
    Assertions.assertNotNull(aliceCatalog);
    Assertions.assertEquals(
        1, aliceCatalog.size(), "Catalog should contain a single contract offer");

    // issue dismantler credential for bob
    final org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential
        bobVehicleDismantleVc = agentClient.issueDismantlerCredential(bob, "vehicleDismantle");
    bobClient.storeCredential(bobVehicleDismantleVc);

    // request catalog with bob
    // assert: catalog size == 2
    aliceCatalog = bobClient.requestCatalog(alice);
    Assertions.assertNotNull(aliceCatalog);
    Assertions.assertEquals(2, aliceCatalog.size(), "Catalog should contain two contract offers");
  }
}
