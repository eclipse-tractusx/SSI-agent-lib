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

package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jwt.SignedJWT;
import java.util.LinkedHashMap;
import org.eclipse.tractusx.ssi.lib.SsiLibrary;
import org.eclipse.tractusx.ssi.lib.crypt.octet.OctetKeyPairFactory;
import org.eclipse.tractusx.ssi.lib.jwt.SignedJwtFactory;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentity;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class SignedJwtFactoryTest {

  private static TestIdentity credentialIssuer;

  @BeforeAll
  public static void beforeAll() {
    SsiLibrary.initialize();
    credentialIssuer = TestIdentityFactory.newIdentityWithED25519Keys();
  }

  @Test
  @DisplayName("Should return a signed Jwt with the given Map of claims and the given private key.")
  void shouldReturnSignedJWT() {

    LinkedHashMap<String, Object> claims = new LinkedHashMap<>();
    SignedJwtFactory signedJwtFactory = new SignedJwtFactory(new OctetKeyPairFactory());
    String keyId = "key-1";
    // When

    SignedJWT signedJWT =
        signedJwtFactory.create(
            credentialIssuer.getDid(),
            credentialIssuer.getDid(),
            claims,
            credentialIssuer.getPrivateKey(),
            keyId);

    // Then
    assertNotNull(signedJWT);
    // check jwt is not empty
    assertNotNull(signedJWT.serialize());
    // check jwt header
    assertEquals(JWSAlgorithm.EdDSA, signedJWT.getHeader().getAlgorithm());
  }
}
