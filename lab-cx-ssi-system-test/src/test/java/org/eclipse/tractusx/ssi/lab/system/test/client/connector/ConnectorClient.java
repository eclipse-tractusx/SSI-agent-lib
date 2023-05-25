package org.eclipse.tractusx.ssi.lab.system.test.client.connector;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lab.system.test.client.Connector;
import org.eclipse.tractusx.ssi.lab.system.test.client.connector.data.*;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

public class ConnectorClient {

  private final DataManagementAPI dataManagementAPI;
  private final SsiApi ssiApi;

  public ConnectorClient(Connector connector) {
    final String dataMgmtUrl =
        String.format(
            "http://%s:%s/data",
            connector.getHostName().getExternal(), connector.getDataMgmtApi().getPort());
    final String ssiUrl =
        String.format(
            "http://%s:%s/api/ssi/agent",
            connector.getHostName().getExternal(), connector.getSsiApi().getPort());
    dataManagementAPI = new DataManagementAPI(dataMgmtUrl, connector.getDataMgmtApi().getAuthKey());
    ssiApi = new SsiApi(ssiUrl);
  }

  public void storeCredential(VerifiableCredential credential) {
    ssiApi.storeCredential(credential);
  }

  @SneakyThrows(IOException.class)
  public List<ContractOffer> requestCatalog(Connector connector) {

    final String idsUrl =
        String.format(
            "http://%s:%s/api/v1/ids/data",
            connector.getHostName().getInternal(), connector.getIdsApi().getPort());
    return dataManagementAPI.requestCatalogFrom(idsUrl);
  }

  @SneakyThrows(IOException.class)
  public void createContractOffer(VerifiableCredentialConstraint constraint) {

    final String id = UUID.randomUUID().toString();

    final DataAddress address = NullDataAddress.INSTANCE;
    final String assetId = "asset-" + id;
    final Asset asset = new Asset(assetId, "Created by SSI System Test", address);
    dataManagementAPI.createAsset(asset);

    final String policyId = "policy-" + id;
    final Permission permission = new Permission("USE", null, List.of(constraint));
    final Policy policy = new Policy(policyId, List.of(permission));
    dataManagementAPI.createPolicy(policy);

    final String contractDefinitionId = "contract-definition-" + id;
    final ContractDefinition contractDefinition =
        new ContractDefinition(contractDefinitionId, policyId, policyId, List.of(assetId));
    dataManagementAPI.createContractDefinition(contractDefinition);
  }
}
