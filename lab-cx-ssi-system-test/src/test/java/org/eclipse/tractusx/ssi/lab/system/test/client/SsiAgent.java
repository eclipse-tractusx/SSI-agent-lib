package org.eclipse.tractusx.ssi.lab.system.test.client;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class SsiAgent {
  String hostName;
  SsiAgentApi agentApi;

  @Data
  @Builder
  @Jacksonized
  public static class SsiAgentApi {
    int port;
  }
}
