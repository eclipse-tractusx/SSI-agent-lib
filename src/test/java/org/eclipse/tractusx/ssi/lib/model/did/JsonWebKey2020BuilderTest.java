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

package org.eclipse.tractusx.ssi.lib.model.did;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.crypt.jwk.JsonWebKey;
import org.eclipse.tractusx.ssi.lib.crypt.x25519.X25519PrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.x25519.X25519PublicKey;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.junit.jupiter.api.Test;

/** The type Json web key 2020 builder test. */
class JsonWebKey2020BuilderTest {

  /** Test json web key 2020 verification method. */
  @SneakyThrows
  @Test
  void testJsonWebKey2020VerificationMethod() {
    final Did did = DidWebFactory.fromHostname("localhost");
    String keyId = "1";
    OctetKeyPair octetKeyPair =
        new OctetKeyPairGenerator(Curve.Ed25519).keyID(keyId).keyUse(KeyUse.SIGNATURE).generate();

    IPrivateKey privateKey = new X25519PrivateKey(octetKeyPair.getDecodedD());
    IPublicKey publicKey = new X25519PublicKey(octetKeyPair.getDecodedX());

    // JWK
    JsonWebKey jwk = new JsonWebKey(keyId, publicKey, privateKey);

    final JWKVerificationMethodBuilder builder = new JWKVerificationMethodBuilder();
    final JWKVerificationMethod jwk2020VerificationMethod =
        builder.did(did).jwk(octetKeyPair).build();

    assertNotNull(jwk2020VerificationMethod);
    assertEquals("JsonWebKey2020", jwk2020VerificationMethod.getType());
    assertEquals(jwk2020VerificationMethod.getId().toString(), "did:web:localhost#" + keyId);
    assertEquals("did:web:localhost", jwk2020VerificationMethod.getController().toString());

    assertEquals(
        "OKP", ((OctetKeyPair) jwk2020VerificationMethod.getJwk()).getKeyType().getValue());
    assertEquals(
        "Ed25519", ((OctetKeyPair) jwk2020VerificationMethod.getJwk()).getCurve().getName());
    assertNotNull(((OctetKeyPair) jwk2020VerificationMethod.getJwk()).getX());
  }
}
