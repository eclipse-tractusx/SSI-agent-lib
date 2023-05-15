package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import org.eclipse.tractusx.ssi.lib.exception.InvalidJsonLdException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;

public interface JsonLdSerializer {

  SerializedVerifiablePresentation serializePresentation(
      VerifiablePresentation verifiablePresentation);

  VerifiablePresentation deserializePresentation(
      SerializedVerifiablePresentation serializedPresentation, boolean validateJsonLd)
      throws InvalidJsonLdException;

  /**
   * Deserialize a presentation and validates the JSON-LD.
   *
   * @param serializedPresentation
   * @return
   * @throws InvalidJsonLdException
   */
  VerifiablePresentation deserializePresentation(
      SerializedVerifiablePresentation serializedPresentation) throws InvalidJsonLdException;
}
