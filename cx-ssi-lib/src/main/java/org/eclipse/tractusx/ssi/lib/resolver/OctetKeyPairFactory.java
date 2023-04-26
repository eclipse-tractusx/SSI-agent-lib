package org.eclipse.tractusx.ssi.lib.resolver;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.util.Base64URL;
import java.util.Arrays;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519Key;

public class OctetKeyPairFactory {

  public OctetKeyPair get(Ed25519Key signingKeyBytes) {

    var key = signingKeyBytes.getEncoded();
    var length = key.length;
    // TODO Document why last 32 bytes
    byte[] b1 = Arrays.copyOfRange(key, length - 32, length);

    return new OctetKeyPair.Builder(Curve.Ed25519, new Base64URL(""))
        .d(Base64URL.encode(b1))
        .build();
  }
}
