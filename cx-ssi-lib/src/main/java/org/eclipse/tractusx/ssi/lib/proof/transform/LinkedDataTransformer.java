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

package org.eclipse.tractusx.ssi.lib.proof.transform;

import foundation.identity.jsonld.JsonLDException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.DanubeTechMapper;

public class LinkedDataTransformer {
  public TransformedLinkedData transform(VerifiableCredential credential) {
    // make a copy and remove proof from credential, as it is not part of the linked data
    VerifiableCredential copyCredential = new VerifiableCredential(credential);
    copyCredential.remove(VerifiableCredential.PROOF);

    var dtCredential = DanubeTechMapper.map(copyCredential);
    try {

      var normalized = dtCredential.normalize("urdna2015");
      return new TransformedLinkedData(normalized);

    } catch (JsonLDException e) {
      throw new RuntimeException(e); // TODO
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e); // TODO
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO
    }
  }
}
