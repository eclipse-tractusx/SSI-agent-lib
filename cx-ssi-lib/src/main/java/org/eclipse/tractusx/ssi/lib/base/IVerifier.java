package org.eclipse.tractusx.ssi.lib.base;

import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

public interface IVerifier {
    public boolean verify(HashedLinkedData hashedLinkedData, VerifiableCredential credential)
    throws UnsupportedSignatureTypeException, DidDocumentResolverNotRegisteredException;
}
