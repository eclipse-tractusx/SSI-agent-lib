package org.eclipse.tractusx.ssi.lib.util;

import org.eclipse.tractusx.ssi.lib.did.web.util.Constants;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConstantsTest {

  @Test
  void testDidMethod() {
    Assertions.assertTrue(Constants.DID_WEB_METHOD.getValue().equals("web"));
  }
}
