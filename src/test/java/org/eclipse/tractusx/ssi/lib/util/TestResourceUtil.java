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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.lib.util.identity.KeyResourceLoader;

public class TestResourceUtil {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final String DID_DOCUMENT_ED25519 = "did-document/document.ed25519.json";
  private static String VERIFIABLE_CREDENTIAL_ALUMNI =
      "verifiable-credential/alumni-credential.json";

  public static List<Map<String, Object>> getAllDidDocuments() {
    return Arrays.asList(readJsonResource(DID_DOCUMENT_ED25519));
  }

  public static Map<String, Object> getAlumniVerifiableCredential() {
    return readJsonResource(VERIFIABLE_CREDENTIAL_ALUMNI);
  }

  public static Map<String, Object> getDidDocument(String verificationKeyType) {
    if (Ed25519VerificationKey2020.DEFAULT_TYPE.equals(verificationKeyType)) {
      return readJsonResource(DID_DOCUMENT_ED25519);
    }

    throw new IllegalArgumentException("Unsupported verification key type: " + verificationKeyType);
  }

  @SneakyThrows
  private static Map<String, Object> readJsonResource(String resource) {
    try (final InputStream inputStream = readResource(resource)) {
      return MAPPER.readValue(inputStream, Map.class);
    }
  }

  private static InputStream readResource(String resource) {
    final InputStream inputStream =
        KeyResourceLoader.class.getClassLoader().getResourceAsStream(resource);

    return Objects.requireNonNull(inputStream, "Resource not found: " + resource);
  }
}
