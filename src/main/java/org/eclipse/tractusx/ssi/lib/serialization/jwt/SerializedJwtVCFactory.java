package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.util.Date;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

public interface SerializedJwtVCFactory {
  SignedJWT createVCJwt(
      Did issuer,
      Did holder,
      Date expDate,
      VerifiableCredential credentials,
      IPrivateKey privateKey,
      String keyId);
}
