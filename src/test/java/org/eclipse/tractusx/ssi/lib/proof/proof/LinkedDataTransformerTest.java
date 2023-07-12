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

package org.eclipse.tractusx.ssi.lib.proof.proof;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

public class LinkedDataTransformerTest {

  private static final String SummaryVerifiableCredential =
      "{\n"
          + "            \"credentialSubject\": [\n"
          + "                {\n"
          + "                    \"contractTemplate\": \"https://public.catena-x.org/contracts/\",\n"
          + "                    \"holderIdentifier\": \"BPN12345678\",\n"
          + "                    \"id\": \"did:web:localhost%3A8080:BPN12345678\",\n"
          + "                    \"items\": [\n"
          + "                        \"BpnCredential\"\n"
          + "                    ],\n"
          + "                    \"type\": \"SummaryCredential\"\n"
          + "                }\n"
          + "            ],\n"
          + "            \"issuanceDate\": \"2023-07-03T13:11:58Z\",\n"
          + "            \"id\": \"did:web:localhost%3A8080:BPNOPERATOR#f7626dd8-4d29-400a-b697-93e6c98cce02\",\n"
          + "            \"proof\": {\n"
          + "                \"created\": \"2023-07-03T13:11:58Z\",\n"
          + "                \"jws\": \"eyJhbGciOiJFZERTQSJ9..6wkM45xOiRizy-BSot8LNMmczxR8s5j3pzs3l3REtdqK831XwoIv-yglKxcl69JFNwmijT75u7_5SYJptPMqDA\",\n"
          + "                \"proofPurpose\": \"proofPurpose\",\n"
          + "                \"type\": \"JsonWebSignature2020\",\n"
          + "                \"verificationMethod\": \"did:web:localhost%3A8080:BPNOPERATOR#\"\n"
          + "            },\n"
          + "            \"type\": [\n"
          + "                \"VerifiableCredential\",\n"
          + "                \"SummaryCredential\"\n"
          + "            ],\n"
          + "            \"@context\": [\n"
          + "                \"https://www.w3.org/2018/credentials/v1\",\n"
          + "                \"https://catenax-ng.github.io/product-core-schemas/SummaryVC.json\",\n"
          + "                \"https://w3id.org/security/suites/jws-2020/v1\"\n"
          + "            ],\n"
          + "            \"issuer\": \"did:web:localhost%3A8080:BPNOPERATOR\",\n"
          + "            \"expirationDate\": \"2025-09-30T22:00:00Z\"\n"
          + "        }";

  private final LinkedDataTransformer transformer = new LinkedDataTransformer();

  @Test
  public void testTwoTransformationsEqual() {
    final VerifiableCredential credential1 = deserializeCredential(SummaryVerifiableCredential);
    final var data1 = transformer.transform(credential1);
    final var data2 = transformer.transform(credential1);

    Assertions.assertEquals(data1, data2);
  }

  @ParameterizedTest()
  @CsvSource(
      value = {
        "issuer\": \"did:web:localhost%3A8080:BPNOPERATOR, issuer\": \"did:web:localhost%3A8080:BPNATTACKER"
      })
  public void testTwoTransformationDifference(String original, String replace) {
    final VerifiableCredential credential1 = deserializeCredential(SummaryVerifiableCredential);
    final VerifiableCredential credential2 =
        deserializeCredential(SummaryVerifiableCredential.replace(original, replace));
    final var data1 = transformer.transform(credential1);
    final var data2 = transformer.transform(credential2);

    Assertions.assertNotEquals(data1, data2);
  }

  @SneakyThrows
  private static VerifiableCredential deserializeCredential(String json) {
    return new VerifiableCredential(new ObjectMapper().readValue(json, Map.class));
  }
}
