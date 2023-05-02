package org.eclipse.tractusx.ssi.lab.connector.extension.spi.util;

import com.jayway.jsonpath.JsonPath;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiableCredential;

@RequiredArgsConstructor
public class JsonPathReader {

  private final VerifiableCredential verifiableCredential;

  public List<String> read(String jsonPath) {

    // json path may be ether _definite_ or _indefinite_
    // _definite_ path is a path that points to a single value
    // _indefinite_ path is a path that points to a list of values
    final boolean isDefinite = JsonPath.isPathDefinite(jsonPath);
    if (isDefinite) {
      String result = JsonPath.read(verifiableCredential, jsonPath);
      return List.of(result);
    } else {
      return JsonPath.read(verifiableCredential, jsonPath);
    }
  }
}
