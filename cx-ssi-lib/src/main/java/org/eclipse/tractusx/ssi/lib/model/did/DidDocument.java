package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class DidDocument {
  @NonNull URI id;

  List<Ed25519VerificationKey2020> verificationMethods;
}
