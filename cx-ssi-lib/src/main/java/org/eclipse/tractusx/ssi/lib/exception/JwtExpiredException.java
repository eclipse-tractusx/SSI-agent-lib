package org.eclipse.tractusx.ssi.lib.exception;

import java.util.Date;

public class JwtExpiredException extends JwtException {

  public JwtExpiredException(Date expiryDate) {
    super("JWT expired at " + expiryDate);
  }
}
