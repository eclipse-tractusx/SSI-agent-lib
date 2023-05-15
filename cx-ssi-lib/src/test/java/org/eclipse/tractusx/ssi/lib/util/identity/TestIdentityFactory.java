package org.eclipse.tractusx.ssi.lib.util.identity;

import java.net.URI;
import java.util.List;
import org.eclipse.tractusx.ssi.lib.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.did.*;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;

public class TestIdentityFactory {

  public static TestIdentity newIdentity() {

    final Did did = TestDidFactory.createRandom();
    final byte[] publicKey = TestResourceUtil.getPublicKeyEd25519();
    final byte[] privateKey = TestResourceUtil.getPrivateKeyEd25519();
    final MultibaseString publicKeyMultiBase = MultibaseFactory.create(publicKey);
    final Ed25519VerificationKey2020Builder ed25519VerificationKey2020Builder =
        new Ed25519VerificationKey2020Builder();
    final Ed25519VerificationKey2020 verificationMethod =
        ed25519VerificationKey2020Builder
            .id(URI.create(did + "#key-1"))
            .controller(URI.create(did + "#controller"))
            .publicKeyMultiBase(publicKeyMultiBase)
            .build();

    final DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
    final DidDocument didDocument =
        didDocumentBuilder.id(did.toUri()).verificationMethods(List.of(verificationMethod)).build();

    return new TestIdentity(did, didDocument, publicKey, privateKey);
  }
}
