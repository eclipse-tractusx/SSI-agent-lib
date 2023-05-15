package org.eclipse.tractusx.ssi.lib.exception;

import java.net.URI;
import lombok.Getter;
import org.eclipse.tractusx.ssi.lib.model.did.Did;

@Getter
public class JwtSignatureCheckFailedException extends JwtException {
  private final Did issuerDid;
  private final URI verificationKey;

  public JwtSignatureCheckFailedException(Did issuerDid, URI verificationKey) {
    super(
        "JWT signature check failed for issuer "
            + issuerDid
            + " and verification key "
            + verificationKey);
    this.issuerDid = issuerDid;
    this.verificationKey = verificationKey;
  }
}
