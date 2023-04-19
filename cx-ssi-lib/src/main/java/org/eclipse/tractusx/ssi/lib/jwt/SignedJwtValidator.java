package org.eclipse.tractusx.ssi.lib.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;

public class SignedJwtValidator {

  @SneakyThrows
  public boolean validate(SignedJWT jwt, String expectedAudience) {

    Date expiryDate = jwt.getJWTClaimsSet().getExpirationTime();
    boolean isNotExpired = expiryDate.after(new Date()); // Todo add Timezone

    List<String> audiences = jwt.getJWTClaimsSet().getAudience();
    boolean isValidAudience = audiences.stream().anyMatch(x -> x.equals(expectedAudience));

    return isNotExpired && isValidAudience;
  }
}
