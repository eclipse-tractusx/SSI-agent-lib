package org.eclipse.tractusx.ssi.lib.proof;

import static org.junit.jupiter.api.Assertions.*;

import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.lib.util.identity.TestDidResolver;
import org.eclipse.tractusx.ssi.lib.util.identity.TestIdentityFactory;
import org.junit.jupiter.api.Test;

/**
 * @author Pascal Manaras <a href="mailto:manaras@xignsys.com">manaras@xignsys.com</a>
 */
class LinkedDataProofValidationTest {
  @Test
  void stuff() throws NoSuchAlgorithmException {
    var testIdentity = TestIdentityFactory.newIdentityWithRSAKeys();

    final TestDidResolver didResolver = new TestDidResolver();
    didResolver.register(testIdentity);

    LinkedDataProofValidation.newInstance(didResolver);
  }
}
