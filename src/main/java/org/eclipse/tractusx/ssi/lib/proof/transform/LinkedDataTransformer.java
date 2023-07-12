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

import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.io.nquad.NQuadsWriter;
import foundation.identity.jsonld.ConfigurableDocumentLoader;
import foundation.identity.jsonld.JsonLDException;
import foundation.identity.jsonld.JsonLDObject;
import io.setl.rdf.normalization.RdfNormalize;
import java.io.IOException;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

public class LinkedDataTransformer {
  @SneakyThrows
  public TransformedLinkedData transform(VerifiableCredential credential) {
    // make a copy and remove proof from credential, as it is not part of the linked data
    VerifiableCredential copyCredential = new VerifiableCredential(credential);
    copyCredential.remove(VerifiableCredential.PROOF);

    try {

      var properties = new HashMap<>(copyCredential);
      properties.remove(VerifiableCredential.CONTEXT);
      properties.remove(VerifiableCredential.TYPE);
      properties.remove(VerifiableCredential.ID);
      var jsonLdCredential =
          JsonLDObject.builder()
              .id(credential.getId())
              .contexts(credential.getContext())
              .types(credential.getTypes())
              .properties(properties)
              .build();

      var documentLoader = new ConfigurableDocumentLoader();
      documentLoader.setEnableHttps(true);
      documentLoader.setEnableLocalCache(true);
      documentLoader.setHttpsContexts(credential.getContext());
      jsonLdCredential.setDocumentLoader(documentLoader);

      RdfDataset rdfDataset = jsonLdCredential.toDataset();
      rdfDataset = RdfNormalize.normalize(rdfDataset, "urdna2015");
      StringWriter stringWriter = new StringWriter();
      NQuadsWriter nQuadsWriter = new NQuadsWriter(stringWriter);
      nQuadsWriter.write(rdfDataset);
      var normalized = stringWriter.getBuffer().toString();

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
