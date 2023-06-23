package org.eclipse.tractusx.ssi.lib.verifiable;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.DanubeTechMapper;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class VerifiableCredentialTest {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  @Test
  @SneakyThrows
  public void canSerializeVC() {
    final Map<String, Object> vpFromMap = TestResourceUtil.getAlumniVerifiableCredential();
    var vp = new VerifiableCredential(vpFromMap);
    var json = vp.toJson();
    var mapFromJson = MAPPER.readValue(json, Map.class);
    Assertions.assertEquals(
        mapFromJson.get(VerifiableCredential.ISSUER), vp.get(VerifiableCredential.ISSUER));
  }

  @Test
  public void shouldNotRemoveContext() {
    var vcFromMap = TestResourceUtil.getAlumniVerifiableCredential();
    var vc = new VerifiableCredential(vcFromMap);
    var vcMapped = DanubeTechMapper.map(vc);

    var ctx = URI.create("https://www.w3.org/2018/credentials/examples/v1");

    Assertions.assertTrue(vcMapped.getContexts().contains(ctx));
  }

  @Test
  public void shouldLoadCachedContext() {
    var vcFromMap = TestResourceUtil.getAlumniVerifiableCredential();
    var vc = com.danubetech.verifiablecredentials.VerifiableCredential.fromMap(vcFromMap);
    Assertions.assertDoesNotThrow(
        () -> {
          vc.normalize("urdna2015");
        });
  }
}
