package org.eclipse.tractusx.ssi.lib.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.exception.JwtAudienceCheckFailedException;
import org.eclipse.tractusx.ssi.lib.exception.JwtExpiredException;

public class SignedJwtValidator {

  @SneakyThrows
  public void validate(SignedJWT jwt, String expectedAudience) {

    Date expiryDate = jwt.getJWTClaimsSet().getExpirationTime();
    boolean isExpired = expiryDate.before(new Date()); // Todo add Timezone
    if (isExpired) {
      throw new JwtExpiredException(expiryDate);
    }

    List<String> audiences = jwt.getJWTClaimsSet().getAudience();
    boolean isValidAudience = audiences.stream().anyMatch(x -> x.equals(expectedAudience));
    if (!isValidAudience) {
      throw new JwtAudienceCheckFailedException(expectedAudience, audiences);
    }
  }
}
