package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519Key;
import org.eclipse.tractusx.ssi.lib.did.resolver.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.lib.model.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofGenerator;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.verify.LinkedDataSigner;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidDocumentResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.eclipse.tractusx.ssi.lib.util.vc.TestCredentialFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SerializedJwtPresentationFactoryImplTest {

  private LinkedDataProofGenerator linkedDataProofGenerator;

  private TestIdentity credentialIssuer;
  private TestDidDocumentResolver didDocumentResolver;

  private SignedJwtVerifier jwtVerifier;

  @SneakyThrows
  @Test
  public void testJwtSerialization() {
    SsiLibrary.initialize();
    this.didDocumentResolver = new TestDidDocumentResolver();

    credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys(false);
    didDocumentResolver.register(credentialIssuer);
    jwtVerifier = new SignedJwtVerifier(didDocumentResolver.withRegistry());
    linkedDataProofGenerator =
        new LinkedDataProofGenerator(
            new LinkedDataHasher(), new LinkedDataTransformer(), new LinkedDataSigner());

    // prepare key
    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();
    final byte[] privateKey = credentialIssuer.getPrivateKey();

    final VerifiableCredential credential =
        TestCredentialFactory.createCredential(credentialIssuer, null);

    final Ed25519Signature2020 proof =
        linkedDataProofGenerator.createEd25519Signature2020(
            credential, verificationMethod, privateKey);

    final VerifiableCredential credentialWithProof =
        TestCredentialFactory.createCredential(credentialIssuer, proof);

    SerializedJwtPresentationFactory presentationFactory =
        new SerializedJwtPresentationFactoryImpl(
            new SignedJwtFactory(new OctetKeyPairFactory()),
            new JsonLdSerializerImpl(),
            credentialIssuer.getDid());

    // Build JWT
    SignedJWT presentation =
        presentationFactory.createPresentation(
            credentialIssuer.getDid(),
            List.of(credentialWithProof),
            "test-audience",
            Ed25519Key.asPrivateKey(credentialIssuer.getPrivateKey()));

    Assertions.assertNotNull(presentation);
    Assertions.assertDoesNotThrow(() -> jwtVerifier.verify(presentation));
  }
}
