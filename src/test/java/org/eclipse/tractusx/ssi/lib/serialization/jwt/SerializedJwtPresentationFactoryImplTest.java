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
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofSigner;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.JsonLdSerializerImpl;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.eclipse.tractusx.ssi.lib.util.vc.TestVerifiableFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SerializedJwtPresentationFactoryImplTest {

  private LinkedDataProofGenerator linkedDataProofGenerator;

  private TestIdentity credentialIssuer;
  private TestDidResolver didResolver;

  private SignedJwtVerifier jwtVerifier;

  @SneakyThrows
  @Test
  public void testJwtSerialization() {
    SsiLibrary.initialize();
    this.didResolver = new TestDidResolver();

    credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys();
    didResolver.register(credentialIssuer);
    jwtVerifier = new SignedJwtVerifier(didResolver);

    linkedDataProofGenerator =
        new LinkedDataProofGenerator(
            SignatureType.JWS,
            new LinkedDataHasher(),
            new LinkedDataTransformer(),
            new Ed25519ProofSigner());

    // prepare key
    final URI verificationMethod =
        credentialIssuer.getDidDocument().getVerificationMethods().get(0).getId();

    final VerifiableCredential credential =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, null);

    final Proof proof =
        linkedDataProofGenerator.createProof(
            credential, verificationMethod, credentialIssuer.getPrivateKey());

    final VerifiableCredential credentialWithProof =
        TestVerifiableFactory.createVerifiableCredential(credentialIssuer, proof);

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
