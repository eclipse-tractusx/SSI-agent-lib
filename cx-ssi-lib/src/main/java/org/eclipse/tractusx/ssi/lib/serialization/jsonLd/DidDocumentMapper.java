package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.experimental.PackagePrivate;

@PackagePrivate
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DidDocumentMapper {
    @NonNull
    public static foundation.identity.did.DIDDocument map(
            DidDocument doc) {

        foundation.identity.did.DIDDocument.builder().id(doc.ID);

    }

}
