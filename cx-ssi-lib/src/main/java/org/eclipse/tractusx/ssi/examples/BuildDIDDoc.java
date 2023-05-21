package org.eclipse.tractusx.ssi.examples;


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
public class BuildDIDDoc 
{
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

}

