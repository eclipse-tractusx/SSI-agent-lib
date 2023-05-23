package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import org.eclipse.tractusx.ssi.lib.exception.InvalidJsonLdException;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.validation.JsonLdValidator;
import org.eclipse.tractusx.ssi.lib.validation.JsonLdValidatorImpl;

public class JsonLdSerializerImpl implements JsonLdSerializer {

    @Override
    public SerializedVerifiablePresentation serializePresentation(
            VerifiablePresentation verifiablePresentation) {

        final com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation = DanubTechMapper
                .map(verifiablePresentation);
        final String dtPresentationJson = dtPresentation.toJson();

        return new SerializedVerifiablePresentation(dtPresentationJson);
    }

    @Override
    public VerifiablePresentation deserializePresentation(
            SerializedVerifiablePresentation serializedPresentation) throws InvalidJsonLdException {
        return deserializePresentation(serializedPresentation, true);
    }

    @Override
    public VerifiablePresentation deserializePresentation(
            SerializedVerifiablePresentation serializedPresentation, boolean validateJsonLd)
            throws InvalidJsonLdException {

        final String serializedPresentationJson = serializedPresentation.getJson();
        final com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation = com.danubetech.verifiablecredentials.VerifiablePresentation
                .fromJson(
                        serializedPresentationJson);

        final VerifiablePresentation presentation = DanubTechMapper.map(dtPresentation);

        if (validateJsonLd) {
            JsonLdValidator jsonLdValidator = new JsonLdValidatorImpl();
            jsonLdValidator.validate(presentation);
        }

        return presentation;
    }

    @Override
    public SerializedDidDocument serializedDidDocument(DidDocument didDocument) {
        final foundation.identity.did.DIDDocument didDoc = DidDocumentMapper.map(didDocument);
        final String didDocJson = didDoc.toJson();
        return new SerializedDidDocument(didDocJson);
    }

}
