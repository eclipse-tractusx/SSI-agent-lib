package org.eclipse.tractusx.ssi.lib.exception;

import java.util.List;

public class JwtAudienceCheckFailedException extends JwtException {
  public JwtAudienceCheckFailedException(String expectedAudience, List<String> actualAudience) {
    super(
        "JWT audience check failed. Expected audience: "
            + expectedAudience
            + ", actual audience: "
            + String.join(",  ", actualAudience));
  }
}
