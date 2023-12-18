/********************************************************************************
 * Copyright (c) 2021,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.ssi.lib.proof;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.nimbusds.jose.jwk.Curve;
import java.io.IOException;
import java.net.URI;
import java.security.InvalidAlgorithmParameterException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.exception.DidDocumentResolverNotRegisteredException;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePublicKeyFormat;
import org.eclipse.tractusx.ssi.lib.exception.KeyGenerationException;
import org.eclipse.tractusx.ssi.lib.exception.NoVerificationKeyFoundExcpetion;
import org.eclipse.tractusx.ssi.lib.exception.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.did.VerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialBuilder;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialSubject;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.hash.LinkedDataHasher;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.proof.transform.TransformedLinkedData;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofSigner;
import org.eclipse.tractusx.ssi.lib.proof.types.ed25519.Ed25519ProofVerifier;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofSigner;
import org.eclipse.tractusx.ssi.lib.proof.types.jws.JWSProofVerifier;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/** The type Sign and verify test. */
public class SignAndVerifyTest {

  /**
   * Test sign and verify ed 201559.
   *
   * @throws IOException the io exception
   * @throws InvalidePrivateKeyFormat the invalide private key format
   * @throws InvalidePublicKeyFormat the invalide public key format
   * @throws KeyGenerationException the key generation exception
   */
  @Test
  public void testSignAndVerify_ED201559() throws IOException, KeyGenerationException {
    final TestDidResolver didResolver = new TestDidResolver();

    var testIdentity = TestIdentityFactory.newIdentityWithED25519Keys();

    didResolver.register(testIdentity);

    var data = "Hello World".getBytes();
    var signer = new Ed25519ProofSigner();
    var verifier = new Ed25519ProofVerifier(didResolver);

    var signature = signer.sign(new HashedLinkedData(data), testIdentity.getPrivateKey());
    var isSigned =
        verifier.verify(new HashedLinkedData(data), signature, testIdentity.getPublicKey());

    Assertions.assertTrue(isSigned);
  }

  /**
   * Test sign and verify jws.
   *
   * @throws IOException the io exception
   * @throws JOSEException the jose exception
   * @throws NoSuchAlgorithmException the no such algorithm exception
   * @throws InvalidePrivateKeyFormat the invalide private key format
   * @throws InvalidePublicKeyFormat the invalide public key format
   * @throws KeyGenerationException the key generation exception
   */
  @Test
  public void testSignAndVerify_JWS_ED()
      throws IOException, NoSuchAlgorithmException, InvalidePrivateKeyFormat,
          KeyGenerationException {
    var testIdentity = TestIdentityFactory.newIdentityWithED25519Keys();
    verifyJws(testIdentity, SignatureType.JWS);
  }

  @Test
  public void testSignAndVerify_JWS_RSA()
      throws NoSuchAlgorithmException, InvalidePrivateKeyFormat {
    var testIdentity = TestIdentityFactory.newIdentityWithRSAKeys();
    verifyJws(testIdentity, SignatureType.JWS_RSA);
  }

  @Test
  public void testSignAndVerify_JWS_EC_P256()
      throws NoSuchAlgorithmException, InvalidePrivateKeyFormat,
          InvalidAlgorithmParameterException {
    var testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256r1", Curve.P_256);
    verifyJws(testIdentity, SignatureType.JWS_P256);
  }

  @Test
  public void testSignAndVerify_JWS_EC_P384()
      throws NoSuchAlgorithmException, InvalidePrivateKeyFormat,
          InvalidAlgorithmParameterException {
    var testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp384r1", Curve.P_384);
    verifyJws(testIdentity, SignatureType.JWS_P384);
  }

  @Test
  public void testSignAndVerify_JWS_EC_256K1()
      throws NoSuchAlgorithmException, InvalidePrivateKeyFormat,
          InvalidAlgorithmParameterException {
    var testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256k1", Curve.SECP256K1);
    verifyJws(testIdentity, SignatureType.JWS_SEC_P_256K1);
  }

  @Test
  void verifyCredential_JWS_RSA()
      throws UnsupportedSignatureTypeException, InvalidePrivateKeyFormat, JsonProcessingException,
          NoSuchAlgorithmException, DidDocumentResolverNotRegisteredException,
          NoVerificationKeyFoundExcpetion, InvalidePublicKeyFormat {
    TestIdentity testIdentity = TestIdentityFactory.newIdentityWithRSAKeys();
    verifyCredential(testIdentity, SignatureType.JWS_RSA);
  }

  @Test
  void verifyCredential_JWS_P256()
      throws UnsupportedSignatureTypeException, InvalidePrivateKeyFormat, JsonProcessingException,
          NoSuchAlgorithmException, DidDocumentResolverNotRegisteredException,
          NoVerificationKeyFoundExcpetion, InvalidePublicKeyFormat,
          InvalidAlgorithmParameterException {
    var testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256r1", Curve.P_256);
    verifyCredential(testIdentity, SignatureType.JWS_P256);
  }

  @Test
  void verifyCredential_JWS_P384()
      throws UnsupportedSignatureTypeException, InvalidePrivateKeyFormat, JsonProcessingException,
          NoSuchAlgorithmException, DidDocumentResolverNotRegisteredException,
          NoVerificationKeyFoundExcpetion, InvalidePublicKeyFormat,
          InvalidAlgorithmParameterException {
    var testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp384r1", Curve.P_384);
    verifyCredential(testIdentity, SignatureType.JWS_P384);
  }

  @Test
  void verifyCredential_JWS_SECP_256K1()
      throws UnsupportedSignatureTypeException, InvalidePrivateKeyFormat, JsonProcessingException,
          NoSuchAlgorithmException, DidDocumentResolverNotRegisteredException,
          NoVerificationKeyFoundExcpetion, InvalidePublicKeyFormat,
          InvalidAlgorithmParameterException {
    var testIdentity = TestIdentityFactory.newIdentityWithECKeys("secp256k1", Curve.SECP256K1);
    verifyCredential(testIdentity, SignatureType.JWS_SEC_P_256K1);
  }

  void verifyCredential(TestIdentity testIdentity, SignatureType signatureType)
      throws UnsupportedSignatureTypeException, InvalidePrivateKeyFormat,
          DidDocumentResolverNotRegisteredException, NoVerificationKeyFoundExcpetion,
          InvalidePublicKeyFormat {
    final TestDidResolver didResolver = new TestDidResolver();
    didResolver.register(testIdentity);

    VerificationMethod verificationMethod =
        testIdentity.getDidDocument().getVerificationMethods().get(0);

    VerifiableCredentialBuilder verifiableCredentialBuilder = new VerifiableCredentialBuilder();
    verifiableCredentialBuilder.context(
        List.of(
            URI.create("https://www.w3.org/2018/credentials/v1"),
            URI.create("https://www.w3.org/2018/credentials/examples/v1"),
            URI.create(
                "https://catenax-ng.github.io/product-core-schemas/businessPartnerData.json"),
            URI.create("https://w3id.org/security/suites/jws-2020/v1")));

    verifiableCredentialBuilder.id(URI.create("http://example.edu/credentials/1872"));
    verifiableCredentialBuilder.type(List.of("VerifiableCredential", "AlumniCredential"));
    verifiableCredentialBuilder.issuer(URI.create("https://example.edu/issuers/565049"));
    verifiableCredentialBuilder.issuanceDate(Instant.now().minus(Duration.ofDays(20)));
    verifiableCredentialBuilder.expirationDate(Instant.now().plus(Duration.ofDays(20)));

    Map<String, Object> alumniOf = Map.of("id", "did:example:c276e12ec21ebfeb1f712ebc6f1");

    Map<String, Object> subjProps =
        Map.of("id", "did:example:ebfeb1f712ebc6f1c276e12ec21", "alumniOf", alumniOf);

    Map<String, Object> subject = Map.of("MembershipCredential", subjProps);

    verifiableCredentialBuilder.credentialSubject(new VerifiableCredentialSubject(subject));
    VerifiableCredential cred = verifiableCredentialBuilder.build();

    LinkedDataProofGenerator proofGenerator = LinkedDataProofGenerator.newInstance(signatureType);
    Proof proof =
        proofGenerator.createProof(cred, verificationMethod.getId(), testIdentity.getPrivateKey());

    VerifiableCredential withProof = verifiableCredentialBuilder.proof(proof).build();

    LinkedDataHasher hasher = new LinkedDataHasher();
    LinkedDataTransformer transformer = new LinkedDataTransformer();
    final TransformedLinkedData transformedData = transformer.transform(withProof);
    final HashedLinkedData hashedData = hasher.hash(transformedData);

    JWSProofVerifier verifier = new JWSProofVerifier(didResolver);
    Assertions.assertTrue(verifier.verify(hashedData, withProof));
  }

  void verifyJws(TestIdentity testIdentity, SignatureType type)
      throws NoSuchAlgorithmException, InvalidePrivateKeyFormat {
    final TestDidResolver didResolver = new TestDidResolver();
    didResolver.register(testIdentity);
    var data = "Hello World".getBytes();
    MessageDigest digest = MessageDigest.getInstance("SHA-256");
    var value = digest.digest(data);

    var signer = new JWSProofSigner(type);
    var verifier = new JWSProofVerifier(didResolver);

    var signature = signer.sign(new HashedLinkedData(value), testIdentity.getPrivateKey());
    var isSigned =
        verifier.verify(new HashedLinkedData(value), signature, testIdentity.getPublicKey(), type);

    Assertions.assertTrue(isSigned);
  }
}
