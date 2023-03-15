package org.eclipse.tractusx.ssi.vcissuer.proof.hash;

import lombok.NonNull;
import lombok.Value;

@Value
public class HashedLinkedData {
  @NonNull byte[] value;
}
