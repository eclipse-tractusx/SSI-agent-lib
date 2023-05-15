package org.eclipse.tractusx.ssi.lib.validation;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.JsonLdOptions;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.http.media.MediaType;
import com.apicatalog.jsonld.processor.ExpansionProcessor;
import foundation.identity.jsonld.ConfigurableDocumentLoader;
import foundation.identity.jsonld.JsonLDObject;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.exception.InvalidJsonLdException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;

public class JsonLdValidatorImpl implements JsonLdValidator {
  private static final String UNDEFINED_TERM_URI = "urn:UNDEFINEDTERM";

  public void validate(VerifiablePresentation verifiablePresentation)
      throws InvalidJsonLdException {
    for (VerifiableCredential verifiableCredential :
        verifiablePresentation.getVerifiableCredentials()) {
      validate(verifiableCredential);
    }
  }

  @Override
  public void validate(VerifiableCredential verifiableCredential) throws InvalidJsonLdException {
    final JsonLDObject jsonLdObject =
        JsonLDObject.builder().properties(verifiableCredential).build();
    final ConfigurableDocumentLoader loader = new ConfigurableDocumentLoader();
    loader.setEnableHttps(true);
    jsonLdObject.setDocumentLoader(loader);

    validateJsonLd(jsonLdObject);
  }

  private void validateJsonLd(JsonLDObject jsonLdObject) throws InvalidJsonLdException {
    try {

      final JsonObject expandContext =
          Json.createObjectBuilder().add("@vocab", Json.createValue(UNDEFINED_TERM_URI)).build();

      final JsonDocument jsonDocument =
          JsonDocument.of(MediaType.JSON_LD, jsonLdObject.toJsonObject());

      final JsonLdOptions jsonLdOptions = new JsonLdOptions();
      jsonLdOptions.setDocumentLoader(jsonLdObject.getDocumentLoader());
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

  private static void findUndefinedTerms(JsonArray jsonArray) {
    for (JsonValue entry : jsonArray) {
      if (entry instanceof JsonObject) findUndefinedTerms((JsonObject) entry);
    }
  }

  private static void findUndefinedTerms(JsonObject jsonObject) {
    for (Map.Entry<String, JsonValue> entry : jsonObject.entrySet()) {
      if (entry.getKey().startsWith(UNDEFINED_TERM_URI)) {

        throw new RuntimeException(
            "Undefined JSON-LD term: " + entry.getKey().substring(UNDEFINED_TERM_URI.length()));
      }

      if (entry.getValue() instanceof JsonArray) findUndefinedTerms((JsonArray) entry.getValue());
      if (entry.getValue() instanceof JsonObject) findUndefinedTerms((JsonObject) entry.getValue());
    }
  }
}
