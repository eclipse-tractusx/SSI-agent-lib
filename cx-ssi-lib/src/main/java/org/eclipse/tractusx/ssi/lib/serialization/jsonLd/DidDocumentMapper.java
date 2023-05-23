package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.VerificationMethod;

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

        var verificationMethods = doc.getVerificationMethods().stream().map(DidDocumentMapper::map)
                .collect(Collectors.toList());

        return foundation.identity.did.DIDDocument.builder().id(doc.getId()).verificationMethods(verificationMethods)
                .build();
    }

    public static foundation.identity.did.VerificationMethod map(VerificationMethod vm) {
        return foundation.identity.did.VerificationMethod.builder().id(vm.getId()).types(Arrays.asList(vm.getType()))
                .controller(VerificationMethod.CONTROLLER).build();

    }
}
