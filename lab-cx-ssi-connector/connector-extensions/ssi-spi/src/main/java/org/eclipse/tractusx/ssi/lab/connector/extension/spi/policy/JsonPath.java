package org.eclipse.tractusx.ssi.lab.connector.extension.spi.policy;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Getter
@ToString
@Jacksonized
public class JsonPath {
  @NonNull String jsonPath;
  @NonNull String value;
}
