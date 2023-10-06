/*
 * *******************************************************************************
 *  * Copyright (c) 2021,2023 Contributors to the Eclipse Foundation
 *  *
 *  * See the NOTICE file(s) distributed with this work for additional
 *  * information regarding copyright ownership.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *  * License for the specific language governing permissions and limitations
 *  * under the License.
 *  *
 *  * SPDX-License-Identifier: Apache-2.0
 *  *******************************************************************************
 */

package org.eclipse.tractusx.ssi.lib.model.verifiable.credential;

import static org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialStatus.ID;
import static org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialStatus.TYPE;
import static org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredentialStatusList2021Entry.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class VerifiableCredentialStatusTest {

  @Test
  void testCredentialStatusSerialization() throws JsonProcessingException {
    // test valid parsing from json object
    String validStatus =
        "{\n"
            + "    \"id\": \"https://example.com/credentials/status/3#94567\",\n"
            + "    \"type\": \"StatusList2021Entry\",\n"
            + "    \"statusPurpose\": \"revocation\",\n"
            + "    \"statusListIndex\": \"94567\",\n"
            + "    \"statusListCredential\": \"https://example.com/credentials/status/3\"\n"
            + "  }";

    AtomicReference<VerifiableCredentialStatusList2021Entry> atomicCredentialStatus =
        new AtomicReference<>();
    ObjectMapper objectMapper = new ObjectMapper();
    Map<String, Object> statusMap = objectMapper.readValue(validStatus, Map.class);
    Assertions.assertDoesNotThrow(
        () -> {
          atomicCredentialStatus.set(new VerifiableCredentialStatusList2021Entry(statusMap));
        });
    Assertions.assertNotNull(atomicCredentialStatus.get());

    VerifiableCredentialStatusList2021Entry credentialStatus = atomicCredentialStatus.get();

    System.out.println(credentialStatus.toJson());

    Assertions.assertEquals("revocation", credentialStatus.getStatusPurpose());
    Assertions.assertEquals(STATUS_LIST_2021_ENTRY, credentialStatus.getType());
    Assertions.assertEquals("94567", String.valueOf(credentialStatus.getStatusListIndex()));
    Assertions.assertEquals(
        "https://example.com/credentials/status/3#94567", credentialStatus.getId().toString());
    Assertions.assertEquals(
        "https://example.com/credentials/status/3",
        credentialStatus.getStatusListCredential().toString());

    // test by setting in vc
    Assertions.assertDoesNotThrow(
        () -> {
          VerifiableCredential vc = TestResourceUtil.getAlumniVerifiableCredential();
          vc.put(VerifiableCredential.CREDENTIAL_STATUS, statusMap);

          VerifiableCredentialStatusList2021Entry statusList2021Entry =
              (VerifiableCredentialStatusList2021Entry) vc.getVerifiableCredentialStatus();
          Assertions.assertEquals("revocation", statusList2021Entry.getStatusPurpose());
          Assertions.assertEquals(STATUS_LIST_2021_ENTRY, statusList2021Entry.getType());
          Assertions.assertEquals(
              "94567", String.valueOf(statusList2021Entry.getStatusListIndex()));
          Assertions.assertEquals(
              "https://example.com/credentials/status/3#94567",
              statusList2021Entry.getId().toString());
          Assertions.assertEquals(
              "https://example.com/credentials/status/3",
              credentialStatus.getStatusListCredential().toString());
        });

    // with invalid type
    statusMap.put(TYPE, "CredentialStatusList2017");
    Assertions.assertDoesNotThrow(() -> new VerifiableCredentialStatusList2021Entry(statusMap));

    // without type
    statusMap.remove(TYPE);
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new VerifiableCredentialStatusList2021Entry(statusMap));

    // with invalid statusPurpose
    statusMap.put(TYPE, STATUS_LIST_2021_ENTRY);
    statusMap.put(STATUS_PURPOSE, "invalidPurpose");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new VerifiableCredentialStatusList2021Entry(statusMap));

    // none integer index
    statusMap.put(STATUS_PURPOSE, "revocation");
    statusMap.put(STATUS_LIST_INDEX, "index");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new VerifiableCredentialStatusList2021Entry(statusMap));

    // index less than 0
    statusMap.put(STATUS_LIST_INDEX, "-1");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new VerifiableCredentialStatusList2021Entry(statusMap));

    // id is not valid uri
    statusMap.put(STATUS_LIST_INDEX, "1");
    statusMap.put(ID, "some random String");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new VerifiableCredentialStatusList2021Entry(statusMap));

    // statusListCredential is not valid uri
    statusMap.put(ID, "https://test.com");
    statusMap.put(STATUS_LIST_CREDENTIAL, "some random String");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new VerifiableCredentialStatusList2021Entry(statusMap));

    // status list index not found
    statusMap.remove(STATUS_LIST_INDEX);
    statusMap.put(ID, "https://test.com");
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new VerifiableCredentialStatusList2021Entry(statusMap));

    // status purpose not found
    statusMap.put(STATUS_LIST_INDEX, "1");
    statusMap.remove(STATUS_PURPOSE);
    Assertions.assertThrows(
        IllegalArgumentException.class,
        () -> new VerifiableCredentialStatusList2021Entry(statusMap));
  }
}
