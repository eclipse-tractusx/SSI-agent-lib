package org.eclipse.tractusx.ssi.lib.model.did;

import static org.junit.jupiter.api.Assertions.*;

import com.nimbusds.jose.jwk.Curve;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.ProofPurpose;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Test;

class DidDocumentBuilderTest {

  @Test
  @SneakyThrows
  void buildWithCapability() {
    TestIdentityFactory.VerificationMethodConfig verificationMethodConfig =
        TestIdentityFactory.generateVerificationMethod(
            Curve.SECP256K1,
            "secp256k1",
            new Did(new DidMethod("web"), new DidMethodIdentifier("localhost"), null));

    DidDocumentBuilder b = new DidDocumentBuilder();
    DidDocument didDocument =
        assertDoesNotThrow(
            () ->
                b.id(URI.create("did:web:localhost"))
                    .verificationMethod(verificationMethodConfig.getVerificationMethod())
                    .assertionMethod(
                        List.of(verificationMethodConfig.getVerificationMethod().getId()))
                    .authentication(
                        List.of(verificationMethodConfig.getVerificationMethod().getId()))
                    .capabilityDelegation(
                        List.of(verificationMethodConfig.getVerificationMethod().getId()))
                    .capabilityInvocation(
                        List.of(verificationMethodConfig.getVerificationMethod().getId()))
                    .build());
    assertTrue(didDocument.containsKey(ProofPurpose.AUTHENTICATION.purpose));
    assertTrue(didDocument.containsKey(ProofPurpose.ASSERTION_METHOD.purpose));
    assertTrue(didDocument.containsKey(ProofPurpose.CAPABILITY_INVOCATION.purpose));
    assertTrue(didDocument.containsKey(ProofPurpose.CAPABILITY_DELEGATION.purpose));
  }

  @Test
  void shouldThrowWhenProofPurposeListEmpty() {
    DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
    assertThrows(
        IllegalArgumentException.class,
        () -> didDocumentBuilder.assertionMethod(Collections.EMPTY_LIST));
  }

  @Test
  void shouldThrowWhenProofPurposeListDoesNotContainUriOrVerificationMethodType() {
    DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
    List<Object> list = List.of("yada");
    assertThrows(IllegalArgumentException.class, () -> didDocumentBuilder.assertionMethod(list));
  }
}
