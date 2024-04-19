/*
 * ******************************************************************************
 * Copyright (c) 2021,2024 Contributors to the Eclipse Foundation
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
 * *******************************************************************************
 */

package org.eclipse.tractusx.ssi.lib.proof.types.ed25519;

import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.Signer;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.crypto.signers.Ed25519Signer;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.crypt.x25519.X25519PublicKey;
import org.eclipse.tractusx.ssi.lib.did.resolver.DidResolver;
import org.eclipse.tractusx.ssi.lib.exception.did.DidParseException;
import org.eclipse.tractusx.ssi.lib.exception.did.DidResolverException;
import org.eclipse.tractusx.ssi.lib.exception.key.InvalidPublicKeyFormatException;
import org.eclipse.tractusx.ssi.lib.exception.proof.NoVerificationKeyFoundException;
import org.eclipse.tractusx.ssi.lib.exception.proof.UnsupportedSignatureTypeException;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidParser;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.model.proof.ed25519.Ed25519Signature2020;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.proof.IVerifier;
import org.eclipse.tractusx.ssi.lib.proof.hash.HashedLinkedData;

/** The type Ed 25519 proof verifier. */
@RequiredArgsConstructor
public class Ed25519ProofVerifier implements IVerifier {

  private final DidResolver didResolver;

  @SneakyThrows({DidResolverException.class})
  public boolean verify(HashedLinkedData hashedLinkedData, Verifiable verifiable)
      throws UnsupportedSignatureTypeException,
          InvalidPublicKeyFormatException,
          NoVerificationKeyFoundException,
          DidParseException {

    final Proof proof =
        verifiable
            .getProof()
            .orElseThrow(() -> new DidParseException("no proof found for verification"));
    final Ed25519Signature2020 ed25519Signature2020 = new Ed25519Signature2020(proof);

    if (!proof.getType().equals(Ed25519Signature2020.ED25519_VERIFICATION_KEY_2018)) {
      throw new UnsupportedSignatureTypeException(proof.getType());
    }

    IPublicKey publicKey = this.discoverPublicKey(ed25519Signature2020);

    final MultibaseString signature = ed25519Signature2020.getProofValue();

    return verify(hashedLinkedData, signature.getDecoded(), publicKey);
  }

  private IPublicKey discoverPublicKey(Ed25519Signature2020 signature)
      throws InvalidPublicKeyFormatException,
          NoVerificationKeyFoundException,
          DidResolverException,
          DidParseException {

    final Did issuer = DidParser.parse(signature.getVerificationMethod());

    final DidDocument document =
        this.didResolver
            .resolve(issuer)
            .orElseThrow(() -> new IllegalStateException("document could not be resolved"));

    final URI verificationMethodId = signature.getVerificationMethod();

    final Ed25519VerificationMethod key =
        document.getVerificationMethods().stream()
            .filter(v -> v.getId().equals(verificationMethodId))
            .filter(Ed25519VerificationMethod::isInstance)
            .map(Ed25519VerificationMethod::new)
            .findFirst()
            .orElseThrow(
                () ->
                    new NoVerificationKeyFoundException(
                        "No Ed25519 verification key found in DID Document"));

    IPublicKey publicKey;
    try {
      publicKey = new X25519PublicKey(key.getPublicKeyBase58().getEncoded(), false);
    } catch (IOException e) {
      throw new InvalidPublicKeyFormatException(e.getCause());
    }

    return publicKey;
  }

  /**
   * Verify hashedLinkedData.
   *
   * @param hashedLinkedData the hashed linked data
   * @param signature the signature
   * @param publicKey the public key
   * @return the boolean the verification result
   */
  @SneakyThrows
  public boolean verify(HashedLinkedData hashedLinkedData, byte[] signature, IPublicKey publicKey) {

    final byte[] message = hashedLinkedData.getValue();

    Signer verifier = new Ed25519Signer();
    Ed25519PublicKeyParameters publicKeyParameters =
        new Ed25519PublicKeyParameters(publicKey.asByte());

    verifier.init(false, publicKeyParameters);
    verifier.update(message, 0, message.length);

    return verifier.verifySignature(signature);
  }
}
