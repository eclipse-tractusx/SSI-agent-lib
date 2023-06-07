package org.eclipse.tractusx.ssi.lib.base;

import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

public interface ISigner {
    public byte[] sign(HashedLinkedData hashedLinkedData, byte[] signingKey) throws SsiException ;
}
