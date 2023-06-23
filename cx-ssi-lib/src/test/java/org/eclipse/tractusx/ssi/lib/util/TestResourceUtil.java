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

package org.eclipse.tractusx.ssi.lib.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationMethod;

public class TestResourceUtil {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final String DID_DOCUMENT_ED25519 = "did-document/document.ed25519.json";
  private static final String DID_DOCUMENT_ED25519_BPN = "did-document/document.ed25519.bpn.json";
  private static final String PUBLIC_KEY_ED25519 = "keys/ed25519/public.pem";
  private static final String PRIVATE_KEY_ED25519 = "keys/ed25519/private.pem";
  private static String VERIFIABLE_CREDENTIAL_ALUMNI =
      "verifiable-credential/alumni-credential.json";
  private static String VERIFIABLE_PRESENTATION_ALUMNI =
      "verifiable-presentation/alumni-presentation.json";
  private static String VERIFIABLE_CREDENTIAL_BPN = "verifiable-credential/bpn-credential.json";

  public static List<Map<String, Object>> getAllDidDocuments() {
    return Arrays.asList(
        readJsonResource(DID_DOCUMENT_ED25519), readJsonResource(DID_DOCUMENT_ED25519_BPN));
  }

  public static Map<String, Object> getAlumniVerifiableCredential() {
    return readJsonResource(VERIFIABLE_CREDENTIAL_ALUMNI);
  }

  public static Map<String, Object> getAlumniVerifiablePresentation() {
    return readJsonResource(VERIFIABLE_PRESENTATION_ALUMNI);
  }

  public static Map<String, Object> getBPNVerifiableCredential() {
    return readJsonResource(VERIFIABLE_CREDENTIAL_BPN);
  }

  public static Map<String, Object> getDidDocument(String verificationKeyType) {
    if (Ed25519VerificationMethod.DEFAULT_TYPE.equals(verificationKeyType)) {
      return readJsonResource(DID_DOCUMENT_ED25519);
    }

    throw new IllegalArgumentException("Unsupported verification key type: " + verificationKeyType);
  }

  public static Map<String, Object> getBPNDidDocument() {
    return readJsonResource(DID_DOCUMENT_ED25519_BPN);
  }

  public static byte[] getPublicKeyEd25519() {
    return readPemResource(PUBLIC_KEY_ED25519);
  }

  public static byte[] getPrivateKeyEd25519() {
    return readPemResource(PRIVATE_KEY_ED25519);
  }

  public static String getPublicKeyEd25519AsString() {
    try {
      return new String(
          readResource(PUBLIC_KEY_ED25519).readAllBytes(), StandardCharsets.ISO_8859_1);
    } catch (IOException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  public static String getPrivateKeyEd25519AsString() {
    try {
      return new String(
          readResource(PRIVATE_KEY_ED25519).readAllBytes(), StandardCharsets.ISO_8859_1);
    } catch (IOException e) {
      throw new RuntimeException(e.getCause());
    }
  }

  @SneakyThrows
  private static Map<String, Object> readJsonResource(String resource) {
    try (final InputStream inputStream = readResource(resource)) {
      return MAPPER.readValue(inputStream, Map.class);
    }
  }

  @SneakyThrows
  private static byte[] readPemResource(String resource) {
    try (final InputStream inputStream = readResource(resource)) {
      final PemReader reader = new PemReader(new InputStreamReader(inputStream));
      return reader.readPemObject().getContent();
    }
  }

  private static InputStream readResource(String resource) {
    final InputStream inputStream =
        TestResourceUtil.class.getClassLoader().getResourceAsStream(resource);

    return Objects.requireNonNull(inputStream, "Resource not found: " + resource);
  }
}
