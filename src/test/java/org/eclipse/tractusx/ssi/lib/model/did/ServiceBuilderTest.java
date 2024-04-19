package org.eclipse.tractusx.ssi.lib.model.did;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import org.junit.jupiter.api.Test;

class ServiceBuilderTest {

  @Test
  void test() {
    ServiceBuilder sb = new ServiceBuilder();
    sb.id(URI.create("id:dd")).serviceEndpoint(URI.create("http://example.com")).type("type");

    assertNotNull(sb.build());
  }
}
