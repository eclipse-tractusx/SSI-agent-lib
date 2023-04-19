package org.eclipse.tractusx.ssi.lib.model;

import lombok.NonNull;

public interface MultibaseString {
  byte[] getDecoded();

  @NonNull
  String getEncoded();
}
