package org.eclipse.tractusx.ssi.lab.system.test.client;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class Connector {

  String hostName;
  DataManagementApi dataMgmtApi;
  SsiApi ssiApi;
  IdsApi idsApi;

  @Data
  @Builder
  @Jacksonized
  public static class DataManagementApi {
    int port;
    String authKey;
  }

  @Data
  @Builder
  @Jacksonized
  public static class SsiApi {
    int port;
  }

  @Data
  @Builder
  @Jacksonized
  public static class IdsApi {
    int port;
    String host;
  }
}
