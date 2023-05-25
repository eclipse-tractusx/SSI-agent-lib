package org.eclipse.tractusx.ssi.lab.system.test.client;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class Connector {

  String bpn;
  HostName hostName;
  DataManagementApi dataMgmtApi;
  DidDocumentApi didDocumentApi;
  SsiApi ssiApi;
  IdsApi idsApi;

  @Data
  @Builder
  @Jacksonized
  public static class HostName {
    String internal;
    String external;
  }

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
  public static class DidDocumentApi {
    int port;
  }

  @Data
  @Builder
  @Jacksonized
  public static class IdsApi {
    int port;
  }
}
