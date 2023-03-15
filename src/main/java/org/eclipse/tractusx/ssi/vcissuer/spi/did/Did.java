package org.eclipse.tractusx.ssi.vcissuer.spi.did;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

import java.net.URI;

@Value
@EqualsAndHashCode
public class Did {

  @EqualsAndHashCode.Include @NonNull DidMethod method;
  @EqualsAndHashCode.Include @NonNull DidMethodIdentifier methodIdentifier;

  public URI toUri() {
    return URI.create(toString());
  }

  @Override
  public String toString() {
    return String.format("did:%s:%s", method, methodIdentifier);
  }
}
