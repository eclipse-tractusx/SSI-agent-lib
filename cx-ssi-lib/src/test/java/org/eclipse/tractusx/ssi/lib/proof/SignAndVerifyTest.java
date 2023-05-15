package org.eclipse.tractusx.ssi.lib.proof;

import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataSigner;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataVerifier;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SignAndVerifyTest {

  @Test
  public void testSignAndVerify() {
    final TestDidDocumentResolver didDocumentResolver = new TestDidDocumentResolver();

    var testIdentity = TestIdentityFactory.newIdentity();

    didDocumentResolver.register(testIdentity);

    var data = "Hello World".getBytes();

    var signer = new LinkedDataSigner();
    var verifier = new LinkedDataVerifier(didDocumentResolver.withRegistry());

    var signature = signer.sign(new HashedLinkedData(data), testIdentity.getPrivateKey());
    var isSigned =
        verifier.verify(new HashedLinkedData(data), signature, testIdentity.getPublicKey());

    Assertions.assertTrue(isSigned);
  }
}
