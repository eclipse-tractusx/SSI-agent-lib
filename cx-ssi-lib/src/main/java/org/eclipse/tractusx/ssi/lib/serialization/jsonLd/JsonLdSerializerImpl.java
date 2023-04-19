package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;

public class JsonLdSerializerImpl implements JsonLdSerializer {

  @Override
  public SerializedVerifiablePresentation serializePresentation(
      VerifiablePresentation verifiablePresentation) {

    final com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation =
        DanubTechMapper.map(verifiablePresentation);
    final String dtPresentationJson = dtPresentation.toJson();

    return new SerializedVerifiablePresentation(dtPresentationJson);
  }

  @Override
  public VerifiablePresentation deserializePresentation(
      SerializedVerifiablePresentation serializedPresentation) {

    final String serializedPresentationJson = serializedPresentation.getJson();
    final com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation =
        com.danubetech.verifiablecredentials.VerifiablePresentation.fromJson(
            serializedPresentationJson);

    return DanubTechMapper.map(dtPresentation);
  }
}
