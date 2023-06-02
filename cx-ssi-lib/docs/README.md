# Documentation



## Installation

To install and use the lib, you have two options: building from source or using the pre-built package from Maven Github Repository.

### Build from Source

To build the project from source, follow these steps:

1. Clone the repository: 
   ```
   git clone https://github.com/catenax-ng/product-lab-ssi.git 
   ```

2. Navigate to the project directory:
   ```
   cd cx-ssi-lib 
   ```

3. Build the project using Maven:
   ```
   mvn clean install
   ```

4. After a successful build, you can include the generated JAR file in your project's dependencies.

### Use Maven Dependency

Alternatively, you can use the pre-built package available on Maven Central Repository by adding the following Maven dependency to your project's `pom.xml` file:

```xml
<dependency>
  <groupId>org.eclipse.tractusx.ssi</groupId>
  <artifactId>cx-ssi-agent-lib</artifactId>
  <version>0.0.3</version>
</dependency>
```

Make sure to update the version number if a newer version is available.

Once you've added the dependency, your build tool (e.g., Maven or Gradle) will automatically download the library and include it in your project.


## Usage

To integrate this library into your SSI agent, follow these guidelines:

1. Import the required classes then Initialize SSiLibrary:

```java
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
public static void main(String[] args){
    SsiLibrary.initialize();
}
// ...
```

2. To bulid a DID Document:

```java
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.tractusx.ssi.lib.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519KeySet;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.VerificationMethod;


import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocumentBuilder;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationKey2020Builder;
public static DidDocument buildDidDocument(String hostName,byte[] privateKey,byte[] publicKey) {
    final Did did = DidWebFactory.fromHostname(hostName);
    
    //Extracting keys 
    final Ed25519KeySet keySet = new Ed25519KeySet(privateKey, publicKey);
    final MultibaseString publicKeyBase = MultibaseFactory.create(keySet.getPublicKey());
    
     

    //Building Verification Methods:
    final List<VerificationMethod> verificationMethods = new ArrayList<>();
    final Ed25519VerificationKey2020Builder builder = new Ed25519VerificationKey2020Builder();
    final Ed25519VerificationKey2020 key =
         builder
             .id(URI.create(did.toUri() + "#key-" + 1))
             .controller(did.toUri())
             .publicKeyMultiBase(publicKeyBase)
             .build();
    verificationMethods.add(key);
    
    final DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
    didDocumentBuilder.id(did.toUri());
    didDocumentBuilder.verificationMethods(verificationMethods);
    
    return didDocumentBuilder.build();
}


// ...
```

3. To Resolve DID document using DID Web: 

```java
import java.net.http.HttpClient;

import org.eclipse.tractusx.ssi.lib.did.web.DidWebDocumentResolver;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolverRegistryImpl;

public static DidDocument ResovleDocument(String didUrl) throws DidDocumentResolverNotRegisteredException {
        
    //DID Resolver Constracture params
    DidWebParser didParser = new DidWebParser();
    var httpClient = HttpClient.newHttpClient();
    var enforceHttps = false; 

    //DID Method
    DidMethod didWeb = new DidMethod("web");

    //DID
    Did did = DidWebFactory.fromHostname(didUrl);

    var didDocumentResolverRegistry = new DidDocumentResolverRegistryImpl();
    didDocumentResolverRegistry.register(new DidWebDocumentResolver(httpClient,didParser , enforceHttps) );
    return didDocumentResolverRegistry.get(didWeb).resolve(did);
}

```


4. To Generate VerifiableCredential:

```java
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;

public static VerifiableCredential createVCWithoutProof() { 
       

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

```

5. To Generate VerifiableCredential with proof:


```java
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.eclipse.tractusx.ssi.lib.model.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialType;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofGenerator;


public static VerifiableCredential createVCWithProof(VerifiableCredential credential, byte[] privateKey, Did issuer){

    //VC Builder
    final VerifiableCredentialBuilder builder =
    new VerifiableCredentialBuilder()
        .context(credential.getContext())
        .id(credential.getId())
        .issuer(issuer.toUri())
        .issuanceDate(Instant.now())
        .credentialSubject(credential.getCredentialSubject())
        .expirationDate(credential.getExpirationDate())
        .type(credential.getTypes());

         //Ed25519 Proof Builder
        final LinkedDataProofGenerator generator = LinkedDataProofGenerator.create();
        final Ed25519Signature2020 proof =  generator.createEd25519Signature2020(builder.build(), URI.create(issuer + "#key-1"), privateKey);
    
        //Adding Proof to VC
        builder.proof(proof);

        return builder.build();
}


```

6. To Generate Verifiable Presentation:

```java
import java.util.List;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

public static VerifiablePresentation createVP( Did issuer, List<VerifiableCredential> credentials){
    //VP Builder
    final VerifiablePresentationBuilder verifiablePresentationBuilder =
        new VerifiablePresentationBuilder();

    // Build VP
    final VerifiablePresentation verifiablePresentation =
        verifiablePresentationBuilder
            .id(issuer.toUri()) // NOTE: Provide unique ID number to each VP you create!!
            .type(List.of(VerifiablePresentationType.VERIFIABLE_PRESENTATION))
            .verifiableCredentials(credentials)
            .build();
    return verifiablePresentation;
 }

```

7. To Generate Signed Verifiable Presentation:
```java
import java.util.List;

import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519Key;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519KeySet;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentationType;
import org.eclipse.tractusx.ssi.lib.resolver.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedJwtPresentationFactory;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedJwtPresentationFactoryImpl;

import com.nimbusds.jwt.SignedJWT;


 public static SignedJWT createVPAsJWT(Did issuer,List<VerifiableCredential> credentials, String audience,byte[] privateKey,byte[] publicKey){
 
    //Extracting keys 
    final Ed25519KeySet keySet = new Ed25519KeySet(privateKey, publicKey);
    final Ed25519Key signingKey = new Ed25519Key(keySet.getPrivateKey()); 
    
    //JWT Factory
    final SerializedJwtPresentationFactory presentationFactory = new SerializedJwtPresentationFactoryImpl(
            new SignedJwtFactory(new OctetKeyPairFactory()), new JsonLdSerializerImpl(), issuer);

    //Build JWT
    return presentationFactory.createPresentation(
        issuer, credentials, audience, signingKey);


}

```

8. To Verfiy JWT (VC or VP) :
```java
package org.eclipse.tractusx.ssi.examples;

import java.net.http.HttpClient;

import org.eclipse.tractusx.ssi.lib.did.web.DidWebDocumentResolver;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.JwtException;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolverRegistryImpl;

import com.nimbusds.jwt.SignedJWT;

public static void verifyJWT(SignedJWT jwt) {
    // DID Resolver Constracture params
    DidWebParser didParser = new DidWebParser();
    var httpClient = HttpClient.newHttpClient();
    var enforceHttps = false;

    var didDocumentResolverRegistry = new DidDocumentResolverRegistryImpl();
    didDocumentResolverRegistry.register(
            new DidWebDocumentResolver(httpClient, didParser, enforceHttps));

    SignedJwtVerifier jwtVerifier = new SignedJwtVerifier(didDocumentResolverRegistry);
    try {
        jwtVerifier.verify(jwt);
    } catch (JwtException | DidDocumentResolverNotRegisteredException e) {
        // An ecxeption will be thrown here in case JWT verification failed or DID
        // Document Resolver not able to resolver.
        e.printStackTrace();
    }

}

```

9. To verify Json-LD:

```java

import com.nimbusds.jwt.SignedJWT;
import java.net.http.HttpClient;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebDocumentResolver;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofValidation;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolverRegistryImpl;

public static boolean verifyLD(VerifiableCredential verifiableCredential) {
     // DID Resolver Constracture params
     DidWebParser didParser = new DidWebParser();
     var httpClient = HttpClient.newHttpClient();
     var enforceHttps = false;
 
     var didDocumentResolverRegistry = new DidDocumentResolverRegistryImpl();
     didDocumentResolverRegistry.register(
         new DidWebDocumentResolver(httpClient, didParser, enforceHttps));

    LinkedDataProofValidation proofValidation = LinkedDataProofValidation.newInstance(didDocumentResolverRegistry);
    return proofValidation.checkProof(verifiableCredential);
   
}
```

10. To Validate JWT expiry date and audience:
```java
import com.nimbusds.jwt.SignedJWT;
import org.eclipse.tractusx.ssi.lib.exception.JwtAudienceCheckFailedException;
import org.eclipse.tractusx.ssi.lib.exception.JwtExpiredException;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtValidator;

public class Validation {
    public static void validateJWTDate(SignedJWT signedJWT, String audience)
            throws JwtAudienceCheckFailedException, JwtExpiredException {
        SignedJwtValidator jwtValidator = new SignedJwtValidator();
        jwtValidator.validateDate(signedJWT);
    }

    public static void validateJWTAudiences(SignedJWT signedJWT, String audience)
            throws JwtAudienceCheckFailedException, JwtExpiredException {
        SignedJwtValidator jwtValidator = new SignedJwtValidator();
        jwtValidator.validateAudiences(signedJWT, audience);
    }
}
```




