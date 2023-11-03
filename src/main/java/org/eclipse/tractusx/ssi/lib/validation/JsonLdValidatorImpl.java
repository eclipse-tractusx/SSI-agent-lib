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

package org.eclipse.tractusx.ssi.lib.validation;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.http.media.MediaType;
import com.apicatalog.jsonld.processor.ExpansionProcessor;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.Map;
import java.util.logging.Logger;
import org.eclipse.tractusx.ssi.lib.exception.InvalidJsonLdException;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.model.RemoteDocumentLoader;
import org.eclipse.tractusx.ssi.lib.model.verifiable.Verifiable;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;

public class JsonLdValidatorImpl implements JsonLdValidator {
  private static final String UNDEFINED_TERM_URI = "urn:UNDEFINEDTERM";
  static final Logger LOG = Logger.getLogger(JsonLdValidatorImpl.class.getName());

  private static void findUndefinedTerms(JsonArray jsonArray) {
    for (JsonValue entry : jsonArray) {
      if (entry instanceof JsonObject) {
        findUndefinedTerms((JsonObject) entry);
      }
    }
  }

  private static void findUndefinedTerms(JsonObject jsonObject) {
    for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
      if (entry.getKey().startsWith(UNDEFINED_TERM_URI)) {

        throw new RuntimeException(
            "Undefined JSON-LD term: " + entry.getKey().substring(UNDEFINED_TERM_URI.length()));
      }

      if (entry.getValue() instanceof JsonArray) {
        findUndefinedTerms((JsonArray) entry.getValue());
      }
      if (entry.getValue() instanceof JsonObject) {
        findUndefinedTerms((JsonObject) entry.getValue());
      }
    }
  }

  private void validateJsonLd(JsonLdObject jsonLdObject) throws InvalidJsonLdException {
    try {

      var documentLoader = RemoteDocumentLoader.getInstance();
      documentLoader.setEnableHttps(true);
      documentLoader.setHttpsContexts(jsonLdObject.getContext());
      documentLoader.setEnableFile(true);

      final JsonObject expandContext =
          Json.createObjectBuilder().add("@vocab", Json.createValue(UNDEFINED_TERM_URI)).build();

      final JsonDocument jsonDocument =
          JsonDocument.of(MediaType.JSON_LD, jsonLdObject.toJsonObject());

      final JsonLdOptions jsonLdOptions = new JsonLdOptions();
      jsonLdOptions.setDocumentLoader(documentLoader);
      jsonLdOptions.setExpandContext(expandContext);

      final JsonArray jsonArray = ExpansionProcessor.expand(jsonDocument, jsonLdOptions, false);
      JsonObject jsonObject = jsonArray.getJsonObject(0);

      findUndefinedTerms(jsonObject);
    } catch (JsonLdError ex) {
      throw new InvalidJsonLdException(
          String.format(
              "Json LD validation failed for json: %s", jsonLdObject.toJsonObject().toString()),
          ex);
    }
  }

  public void validate(Verifiable verifiable) throws InvalidJsonLdException {
    if (verifiable instanceof VerifiableCredential) {
      validateJsonLd(verifiable);
    } else if (verifiable instanceof VerifiablePresentation) {
      VerifiablePresentation verifiablePresentation = (VerifiablePresentation) verifiable;
      for (VerifiableCredential verifiableCredential :
          verifiablePresentation.getVerifiableCredentials()) {
        validate(verifiableCredential);
      }
    } else {
      LOG.warning("Unsupported Verifiable type: " + verifiable.getClass().getName());
    }
  }
}
