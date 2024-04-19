package org.eclipse.tractusx.ssi.lib.model.did;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.exception.did.DidParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DidParserTest {

  @ParameterizedTest
  @ValueSource(strings = {"fluffy:localhost", "did:web"})
  @SneakyThrows
  void shouldThrowIfNotDidUri(String value) {
    assertThrows(DidParseException.class, () -> DidParser.parse(URI.create(value)));
  }

  @Test
  void shouldThrowWhenNotAURIString() {
    assertThrows(DidParseException.class, () -> DidParser.parse("{}"));
  }
}
