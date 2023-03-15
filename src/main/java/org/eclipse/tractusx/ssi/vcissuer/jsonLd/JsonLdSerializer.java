package org.eclipse.tractusx.ssi.vcissuer.jsonLd;


import org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential.VerifiableCredential;

public interface JsonLdSerializer {

    String serializeVerifiableCredential(
            VerifiableCredential verifiableCredential);
}
