package org.eclipse.tractusx.ssi.lib.model.did;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DidTest {
  @Test
  public void testDidEquals() {
    Did did1 = new Did(new DidMethod("test"), new DidMethodIdentifier("myKey"));
    Did did2 = new Did(new DidMethod("test"), new DidMethodIdentifier("myKey"));

    Assertions.assertEquals(did1, did2);
  }
}
