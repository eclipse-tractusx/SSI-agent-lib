clipse.tractusx.ssi.examples;


import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;

public class Crypto 
{
    public static boolean verifyMessage() { 
       

        //VC Bulider
        final VerifiableCredentialBuilder verifiableCredentialBuilder =
        new VerifiableCredentialBuilder();

        //VC Subject
        final VerifiableCredentialSubject verifiableCredentialSubject =
        new VerifiableCredentialSubject(Map.of("test", "test"));
       
        //Using Builder
        final VerifiableCredential credentialWithoutProof =
        verifiableCredentialBuilder
            .id(URI.create("did:test:id"))
            .type(List.of(VerifiableCredentialType.VERIFIABLE_CREDENTIAL))
            .issuer(URI.create("did:test:isser"))
            .expirationDate(Instant.now().plusSeconds(3600))
            .issuanceDate(Instant.now())
            .credentialSubject(verifiableCredentialSubject)
            .build();

        return credentialWithoutProof;

    }

}



