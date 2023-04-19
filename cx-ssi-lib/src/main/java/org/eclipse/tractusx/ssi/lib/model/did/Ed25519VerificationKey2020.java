package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;

@Value
@Builder
public class Ed25519VerificationKey2020 {
  public static final String TYPE = "Ed25519VerificationKey2020";

  // The id of the verification method SHOULD be the JWK thumbprint calculated from the
  // publicKeyMultibase property
  @NonNull URI id;

  // The controller of the verification method SHOULD be a URI.
  @NonNull URI controller;
  @Builder.Default @NonNull String didVerificationMethodType = TYPE;
  @NonNull MultibaseString multibase;

  @SneakyThrows
  public PublicKey getKey() {
    X509EncodedKeySpec spec = new X509EncodedKeySpec(multibase.getDecoded());
    KeyFactory kf = KeyFactory.getInstance("Ed25519");
    return kf.generatePublic(spec);
  }
}
