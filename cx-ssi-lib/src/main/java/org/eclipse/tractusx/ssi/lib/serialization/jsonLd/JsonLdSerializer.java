package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;

public interface JsonLdSerializer {

  SerializedVerifiablePresentation serializePresentation(
      VerifiablePresentation verifiablePresentation);

  VerifiablePresentation deserializePresentation(
      SerializedVerifiablePresentation serializedPresentation);
}
