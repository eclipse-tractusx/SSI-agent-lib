package org.eclipse.tractusx.ssi.lib.serialization.jwt;

import java.util.Map;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.DanubTechMapper;

public class DanubTechVerifiableCredentialSerializerImpl implements VerifiableCredentialSerializer {
  @Override
  public VerifiableCredential deserialize(Map<String, Object> credentialJson) {

    final com.danubetech.verifiablecredentials.VerifiableCredential dtCredential =
        com.danubetech.verifiablecredentials.VerifiableCredential.fromMap(credentialJson);

    return DanubTechMapper.map(dtCredential);
  }

  @Override
  public String serialize(VerifiableCredential credential) {

    final com.danubetech.verifiablecredentials.VerifiableCredential dtCredential =
        DanubTechMapper.map(credential);

    return dtCredential.toJson(true);
  }
}
