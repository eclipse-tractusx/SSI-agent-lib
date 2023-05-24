package org.eclipse.tractusx.ssi.lib.model.did;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DidDocumentTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Test
    public void canCreateDidDocument() {
        final List<Map<String, Object>> documents = TestResourceUtil.getAllDidDocuments();

        for (Map<String, Object> document : documents) {
            Assertions.assertDoesNotThrow(() -> new DidDocument(document));
        }
    }

    @Test
    @SneakyThrows
    public void canSerializeDidDocument() {
        final List<Map<String, Object>> documents = TestResourceUtil.getAllDidDocuments();
        for (Map<String, Object> document : documents) {
            var doc = new DidDocument(document);
            var json = doc.toJson();
            var mapFromJson = MAPPER.readValue(json, Map.class);
            Assertions.assertEquals(mapFromJson.get(DidDocument.ID), doc.get(DidDocument.ID));
        }
    }

    @Test
    @SneakyThrows
    public void canDeserializeDidDocument() {
        final List<Map<String, Object>> documents = TestResourceUtil.getAllDidDocuments();
        for (Map<String, Object> document : documents) {
            var docFromMap = new DidDocument(document);
            var json = docFromMap.toJson();
            var docFromJson = DidDocument.fromJson(json);
            Assertions.assertEquals(docFromJson.get(DidDocument.ID), docFromMap.get(DidDocument.ID));
        }
    }
}
