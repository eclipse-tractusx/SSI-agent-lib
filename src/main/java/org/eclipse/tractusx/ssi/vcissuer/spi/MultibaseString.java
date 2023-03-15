package org.eclipse.tractusx.ssi.vcissuer.spi;

import lombok.NonNull;

public interface MultibaseString {
  byte[] getDecoded();

  @NonNull
  String getEncoded();
}
