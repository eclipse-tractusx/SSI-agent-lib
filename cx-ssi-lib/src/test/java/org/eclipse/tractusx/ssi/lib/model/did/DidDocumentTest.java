package org.eclipse.tractusx.ssi.lib.model.did;

import java.util.List;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DidDocumentTest {

  @Test
  public void canCreateDidDocument() {
    final List<Map<String, Object>> documents = TestResourceUtil.getAllDidDocuments();

    for (Map<String, Object> document : documents) {
      Assertions.assertDoesNotThrow(() -> new DidDocument(document));
    }
  }
}
