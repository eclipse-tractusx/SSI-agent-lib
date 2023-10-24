package org.eclipse.tractusx.ssi.lib.verifiable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.proof.transform.LinkedDataTransformer;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VerifiableCredentialTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  @SneakyThrows
  public void canSerializeVC() throws JsonMappingException, JsonProcessingException {
    final Map<String, Object> vpFromMap = TestResourceUtil.getAlumniVerifiableCredential();
    var vp = new VerifiableCredential(vpFromMap);
    var json = vp.toJson();
    var mapFromJson = MAPPER.readValue(json, Map.class);
    Assertions.assertEquals(
        mapFromJson.get(VerifiableCredential.ISSUER), vp.get(VerifiableCredential.ISSUER));
  }

  @Test
  public void shouldLoadCachedContext() {
    var vcFromMap = TestResourceUtil.getAlumniVerifiableCredential();
    var vc = new VerifiableCredential(vcFromMap);

    var transform = new LinkedDataTransformer();
    Assertions.assertDoesNotThrow(
        () -> {
          transform.transform(vc);
        });
  }
}
