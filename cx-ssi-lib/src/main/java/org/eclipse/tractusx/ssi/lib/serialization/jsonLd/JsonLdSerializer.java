package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import org.eclipse.tractusx.ssi.lib.exception.InvalidJsonLdException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;

public interface JsonLdSerializer {

  /**
   * Serlizer a presentation to a Json string
   *
   * @param verifiablePresentation
   * @return {@link SerializedVerifiablePresentation}
   */
  SerializedVerifiablePresentation serializePresentation(
      VerifiablePresentation verifiablePresentation);

  /**
   * Deserialize a presentation with options to validate JSON-LD or not.
   *
   * @param serializedPresentation
   * @param validateJsonLd
   * @return VerifiablePresentation
   * @throws InvalidJsonLdException
   */
  VerifiablePresentation deserializePresentation(
      SerializedVerifiablePresentation serializedPresentation, boolean validateJsonLd)
      throws InvalidJsonLdException;

  /**
   * Deserialize a presentation and validates the JSON-LD.
   *
   * @param serializedPresentation
   * @return VerifiablePresentation
   * @throws InvalidJsonLdException
   */
  VerifiablePresentation deserializePresentation(
      SerializedVerifiablePresentation serializedPresentation) throws InvalidJsonLdException;
}
