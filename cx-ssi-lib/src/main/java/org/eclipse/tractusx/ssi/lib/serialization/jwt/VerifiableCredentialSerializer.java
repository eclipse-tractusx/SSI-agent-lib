package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import java.util.Map;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

/**
 * Deserializes a Verifiable Credential from a JSON representation. As the deserialization results
 * in an information loss, der is not serialization of the same credential possible anymore.
 */
public interface VerifiableCredentialSerializer {
  VerifiableCredential deserialize(Map<String, Object> credentialJson);

  String serialize(VerifiableCredential credential);
}
