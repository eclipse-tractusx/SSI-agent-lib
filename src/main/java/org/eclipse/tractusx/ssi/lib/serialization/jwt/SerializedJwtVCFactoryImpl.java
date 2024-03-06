package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.util.Date;
import java.util.LinkedHashMap;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

@RequiredArgsConstructor
public class SerializedJwtVCFactoryImpl implements SerializedJwtVCFactory {

  private final SignedJwtFactory signedJwtFactory;

  @Override
  public SignedJWT createVCJwt(
      URI id,
      Did issuer,
      Did holder,
      Date expDate,
      VerifiableCredential credentials,
      IPrivateKey privateKey,
      String keyId) {
    var clonedVC = new LinkedHashMap<String, Object>();
    clonedVC.putAll(credentials);
    return signedJwtFactory.create(id, issuer, holder, expDate, clonedVC, privateKey, keyId);
  }
}
