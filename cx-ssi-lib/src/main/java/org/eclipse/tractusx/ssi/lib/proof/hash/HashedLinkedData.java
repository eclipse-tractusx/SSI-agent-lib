package org.eclipse.tractusx.ssi.lib.proof.hash;

import lombok.NonNull;
import lombok.Value;

@Value
public class HashedLinkedData {
  @NonNull byte[] value;
}
