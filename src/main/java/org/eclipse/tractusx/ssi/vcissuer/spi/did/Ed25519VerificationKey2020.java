package org.eclipse.tractusx.ssi.vcissuer.spi.did;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.eclipse.tractusx.ssi.vcissuer.spi.MultibaseString;

import java.net.URI;

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
}
