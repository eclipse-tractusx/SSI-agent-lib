package org.eclipse.tractusx.ssi.vcissuer.jsonLd;


import org.eclipse.tractusx.ssi.vcissuer.spi.verifiable.credential.VerifiableCredential;

public class JsonLdSerializerImpl implements JsonLdSerializer {

    @Override
    public String serializeVerifiableCredential(VerifiableCredential verifiableCredential) {

        final com.danubetech.verifiablecredentials.VerifiableCredential dtCredential =
                DanubTechMapper.map(verifiableCredential);
        return dtCredential.toJson(true);
    }
}
