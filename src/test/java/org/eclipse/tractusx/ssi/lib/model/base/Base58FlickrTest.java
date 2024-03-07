package org.eclipse.tractusx.ssi.lib.model.base;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.ipfs.multibase.Multibase;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

class Base58FlickrTest {

  @Test
  @SneakyThrows
  void canDecode() {
    String encoded =
        Multibase.encode(Multibase.Base.Base58BTC, "data".getBytes(StandardCharsets.UTF_8));
    encoded = encoded.substring(0, 1).toUpperCase() + encoded.substring(1);
    assertTrue(Base58Flickr.canDecode(encoded));
  }

  @Test
  void canNotDecode() {
    assertFalse(Base58Flickr.canDecode("msdfjkhsdhjfshjdf"));
  }
}
