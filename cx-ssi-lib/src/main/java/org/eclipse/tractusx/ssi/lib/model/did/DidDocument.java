package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.*;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.util.SerializeUtil;

// spec https://www.w3.org/TR/did-core/
@ToString
public class DidDocument extends JsonLdObject {

    public static final String DEFAULT_CONTEXT = "https://www.w3.org/ns/did/v1";
    public static final String ID = "id";
    public static final String VERIFICATION_METHOD = "verificationMethod";

    public DidDocument(Map<String, Object> json) {
        super(json);

        try {
            // validate getters
            Objects.requireNonNull(this.getContext(), "context is null");
            Objects.requireNonNull(this.getId(), "id is null");
            Objects.requireNonNull(this.getVerificationMethods(), "verificationMethod is null");
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Invalid DidDocument: %s", SerializeUtil.toJson(json)), e);
        }
    }

    public URI getId() {
        return SerializeUtil.asURI(this.get(ID));
    }

    public List<VerificationMethod> getVerificationMethods() {

        final List<VerificationMethod> result = new ArrayList<>();

        final Object verificationMethod = this.get(VERIFICATION_METHOD);
        if (verificationMethod instanceof Map) {
            result.add(new VerificationMethod((Map<String, Object>) verificationMethod));
        }
        if (verificationMethod instanceof List) {
            ((List<Map<String, Object>>) verificationMethod)
                    .forEach(vm -> result.add(new VerificationMethod(vm)));
        }

        return result;
    }

    public static DidDocument fromJson(String json) {
        var map = SerializeUtil.fromJson(json);
        return new DidDocument(map);
    }
}
