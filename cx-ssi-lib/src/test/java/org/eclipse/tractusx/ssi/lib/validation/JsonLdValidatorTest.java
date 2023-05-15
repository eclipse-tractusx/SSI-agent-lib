package org.eclipse.tractusx.ssi.lib.validation;

import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonLdValidatorTest {
  private JsonLdValidator validator;

  @BeforeEach
  public void setUp() {
    validator = new JsonLdValidatorImpl();
  }

  @Test
  public void validateTestSuccess() {
    final VerifiableCredential toTest = loadValidjsonLDObject();
    Assertions.assertDoesNotThrow(() -> validator.validate(toTest));
  }

  @SneakyThrows
  private VerifiableCredential loadValidjsonLDObject() {

    final Map<String, Object> verifiableCredential =
        TestResourceUtil.getAlumniVerifiableCredential();
    return new VerifiableCredential(verifiableCredential);
  }
}
