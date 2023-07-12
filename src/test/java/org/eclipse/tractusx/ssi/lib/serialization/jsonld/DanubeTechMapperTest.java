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

package org.eclipse.tractusx.ssi.lib.serialization.jsonld;

import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.DanubeTechMapper;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DanubeTechMapperTest {

  @SneakyThrows
  @Test
  public void canMapVerifiableCredential() {

    VerifiableCredential expected =
        new VerifiableCredential(TestResourceUtil.getAlumniVerifiableCredential());

    com.danubetech.verifiablecredentials.VerifiableCredential dtCredential =
        DanubeTechMapper.map(expected);
    VerifiableCredential result = DanubeTechMapper.map(dtCredential);

    Assertions.assertEquals(expected.toPrettyJson(), result.toPrettyJson());
  }

  @SneakyThrows
  @Test
  public void canMapVerifiablePresentation() {

    VerifiablePresentation expected =
        new VerifiablePresentation(TestResourceUtil.getAlumniVerifiablePresentation());

    com.danubetech.verifiablecredentials.VerifiablePresentation dtCredential =
        DanubeTechMapper.map(expected);
    VerifiablePresentation result = DanubeTechMapper.map(dtCredential);

    Assertions.assertEquals(expected.toPrettyJson(), result.toPrettyJson());
  }
}
