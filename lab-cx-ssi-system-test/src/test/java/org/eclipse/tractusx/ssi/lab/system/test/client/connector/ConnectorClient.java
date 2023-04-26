package org.eclipse.tractusx.ssi.lab.system.test.client.connector;

import java.io.IOException;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lab.system.test.client.Connector;
import org.eclipse.tractusx.ssi.lab.system.test.client.connector.data.ContractOffer;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

public class ConnectorClient {

  private final DataManagementAPI dataManagementAPI;
  private final SsiApi ssiApi;

  public ConnectorClient(Connector connector) {
    final String dataMgmtUrl =
        String.format(
            "http://%s:%s/data", connector.getHostName(), connector.getDataMgmtApi().getPort());
    final String ssiUrl =
        String.format(
            "http://%s:%s/api/ssi/agent", connector.getHostName(), connector.getSsiApi().getPort());
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
            connector.getIdsApi().getHost(), connector.getIdsApi().getPort());
    return dataManagementAPI.requestCatalogFrom(idsUrl);
  }
}
