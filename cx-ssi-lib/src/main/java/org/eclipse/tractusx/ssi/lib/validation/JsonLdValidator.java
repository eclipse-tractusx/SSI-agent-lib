package org.eclipse.tractusx.ssi.lib.validation;

import org.eclipse.tractusx.ssi.lib.exception.InvalidJsonLdException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;

public interface JsonLdValidator {

  void validate(VerifiablePresentation verifiablePresentation) throws InvalidJsonLdException;

  void validate(VerifiableCredential verifiableCredential) throws InvalidJsonLdException;
}
