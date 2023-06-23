package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import com.nimbusds.jwt.SignedJWT;
import java.net.URI;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
import org.eclipse.tractusx.ssi.lib.crypt.octet.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtVerifier;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofGenerator;
import org.eclipse.tractusx.ssi.lib.proof.SignatureType;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.ED21559ProofSigner;
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

    credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys();
    didDocumentResolver.register(credentialIssuer);
    jwtVerifier = new SignedJwtVerifier(didDocumentResolver.withRegistry());

    linkedDataProofGenerator =
        new LinkedDataProofGenerator(
            SignatureType.JWS,
            new LinkedDataHasher(),
            new LinkedDataTransformer(),
            new ED21559ProofSigner());

    // prepare key
    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();

    final VerifiableCredential credential =
        TestCredentialFactory.createCredential(credentialIssuer, null);

    final Proof proof =
        linkedDataProofGenerator.createProof(
            credential, verificationMethod, credentialIssuer.getPrivateKey());

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
            credentialIssuer.getPrivateKey());

    Assertions.assertNotNull(presentation);
    Assertions.assertDoesNotThrow(() -> jwtVerifier.verify(presentation));
  }
}
