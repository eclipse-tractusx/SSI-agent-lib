package org.eclipse.tractusx.ssi.lab.system.test.client.connector.data;

import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class VerifiableCredentialConstraint implements Constraint {

  @NonNull String credentialType;
  @NonNull List<JsonPathEntry> paths;

  @Value
  public static class JsonPathEntry {
    @NonNull String jsonPath;
    @NonNull String value;
  }
}
