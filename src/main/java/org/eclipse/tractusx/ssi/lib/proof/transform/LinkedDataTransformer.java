/*
 * ******************************************************************************
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
 * *******************************************************************************
 */

package org.eclipse.tractusx.ssi.lib.proof.transform;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.api.ToRdfApi;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.http.media.MediaType;
import com.apicatalog.rdf.RdfDataset;
import com.apicatalog.rdf.io.nquad.NQuadsWriter;
import io.setl.rdf.normalization.RdfNormalize;
import java.io.IOException;
import java.io.StringWriter;
import java.security.NoSuchAlgorithmException;
import lombok.SneakyThrows;
import org.apache.commons.lang3.SerializationUtils;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.model.RemoteDocumentLoader;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;

public class LinkedDataTransformer {

  @SneakyThrows
  public TransformedLinkedData transform(Verifiable document) {
    // Make a copy and remove proof, as it is not part of the linked data
    var copy = (JsonLdObject) SerializationUtils.clone(document);
    copy.remove(Verifiable.PROOF);
    return this.canocliztion(copy);
  }

  private TransformedLinkedData canocliztion(JsonLdObject document) {
    try {

      RdfDataset rdfDataset = toDataset(document);
      rdfDataset = RdfNormalize.normalize(rdfDataset, "urdna2015");
      StringWriter stringWriter = new StringWriter();
      NQuadsWriter nQuadsWriter = new NQuadsWriter(stringWriter);
      nQuadsWriter.write(rdfDataset);
      var normalized = stringWriter.getBuffer().toString();

      return new TransformedLinkedData(normalized);

    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private RdfDataset toDataset(JsonLdObject jsonLdObject) throws RuntimeException {

    var documentLoader = RemoteDocumentLoader.getInstance();
    documentLoader.setEnableHttps(true);
    documentLoader.setHttpsContexts(jsonLdObject.getContext());

    JsonLdOptions options = new JsonLdOptions();
    options.setDocumentLoader(documentLoader);
    options.setOrdered(true);

    JsonDocument jsonDocument = JsonDocument.of(MediaType.JSON_LD, jsonLdObject.toJsonObject());
    ToRdfApi toRdfApi = JsonLd.toRdf(jsonDocument);
    toRdfApi.options(options);
    try {
      return toRdfApi.get();
    } catch (JsonLdError ex) {
      throw new RuntimeException(ex);
    }
  }
}
