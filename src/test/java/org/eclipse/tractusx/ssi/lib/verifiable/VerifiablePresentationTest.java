package org.eclipse.tractusx.ssi.lib.verifiable;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VerifiablePresentationTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  @SneakyThrows
  public void canSerializeVP() {
    final Map<String, Object> vpFromMap = TestResourceUtil.getAlumniVerifiablePresentation();
    var vp = new VerifiablePresentation(vpFromMap);
    var json = vp.toJson();
    var mapFromJson = MAPPER.readValue(json, Map.class);
    Assertions.assertEquals(
        mapFromJson.get(VerifiablePresentation.VERIFIABLE_CREDENTIAL),
        vp.get(VerifiablePresentation.VERIFIABLE_CREDENTIAL));
  }

  @Test
  @SneakyThrows
  public void canSerializeVPwithCredentialNotAsList() {
    final Map<String, Object> vpFromMap = TestResourceUtil.getAlumniVerifiablePresentation();
    var vp = new VerifiablePresentation(vpFromMap);
    vp.put("verifiableCredential", vp.getVerifiableCredentials().get(0));
    var json = vp.toJson();
    var mapFromJson = MAPPER.readValue(json, Map.class);
    Assertions.assertDoesNotThrow(() -> new VerifiablePresentation(mapFromJson));
  }
}
