package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JwtConfig {

  private long expirationTime;
}
