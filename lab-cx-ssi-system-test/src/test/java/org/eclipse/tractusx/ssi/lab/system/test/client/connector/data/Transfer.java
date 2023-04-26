package org.eclipse.tractusx.ssi.lab.system.test.client.connector.data;

import static org.awaitility.Awaitility.await;

import java.io.IOException;
import java.time.Duration;
import lombok.Value;
import org.eclipse.tractusx.ssi.lab.system.test.client.connector.DataManagementAPI;
import org.eclipse.tractusx.ssi.lab.system.test.client.util.Timeouts;

@Value
public class Transfer {

  String id;

  public void waitUntilComplete(DataManagementAPI dataManagementAPI) {
    await()
        .pollDelay(Duration.ofMillis(2000))
        .atMost(Timeouts.FILE_TRANSFER)
        .until(() -> isComplete(dataManagementAPI));
  }

  public boolean isComplete(DataManagementAPI dataManagementAPI) throws IOException {
    var transferProcess = dataManagementAPI.getTransferProcess(id);
    if (transferProcess == null) return false;

    var state = transferProcess.getState();

    return state == TransferProcessState.COMPLETED || state == TransferProcessState.ERROR;
  }
}
