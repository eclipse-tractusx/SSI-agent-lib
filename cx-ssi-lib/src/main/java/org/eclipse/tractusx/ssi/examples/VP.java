 package org.eclipse.tractusx.ssi.examples;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519Key;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519KeySet;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationType;
import org.eclipse.tractusx.ssi.lib.resolver.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedJwtPresentationFactory;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedJwtPresentationFactoryImpl;

import com.nimbusds.jwt.SignedJWT;

class VP {

 public static VerifiablePresentation createVP( Did issuer, List<VerifiableCredential> credentials){
    final VerifiablePresentationBuilder verifiablePresentationBuilder =
        new VerifiablePresentationBuilder();
    final VerifiablePresentation verifiablePresentation =
        verifiablePresentationBuilder
            .id(issuer.toUri()) // NOTE: Provide unique ID number to each VP you create!!
            .type(List.of(VerifiablePresentationType.VERIFIABLE_PRESENTATION))
            .verifiableCredentials(credentials)
            .build();
    return verifiablePresentation;
 }

 public static SignedJWT createVPAsJWT(Did issuer,List<VerifiableCredential> credentials, String audience,byte[] privateKey,byte[] publicKey){
 
    //Extracting keys 
    final Ed25519KeySet keySet = new Ed25519KeySet(privateKey, publicKey);
    final Ed25519Key signingKey = new Ed25519Key(keySet.getPrivateKey()); 

    final SerializedJwtPresentationFactory presentationFactory = new SerializedJwtPresentationFactoryImpl(
            new SignedJwtFactory(new OctetKeyPairFactory()), new JsonLdSerializerImpl(), issuer);


    return presentationFactory.createPresentation(
        issuer, credentials, audience, signingKey);


   }
}
 
