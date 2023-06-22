package org.eclipse.tractusx.ssi.lib.crypt;

import java.io.IOException;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;

public interface IKey {
  int getKeyLength();

  String asStringForStoring() throws IOException;

  String asStringForExchange(EncodeType encodeType) throws IOException;

  byte[] asByte();
}
