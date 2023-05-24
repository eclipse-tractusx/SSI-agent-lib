package org.eclipse.tractusx.ssi.lib.model.verifiable.presentation;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.util.SerializeUtil;

@ToString
public class VerifiablePresentation extends JsonLdObject {
    public static final String DEFAULT_CONTEXT = "https://www.w3.org/2018/credentials/v1";

    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String VERIFIABLE_CREDENTIAL = "verifiableCredential";

    public VerifiablePresentation(Map<String, Object> json) {
        super(json);

        try {
            // validate getters
            Objects.requireNonNull(this.getId(), "id s null");
            Objects.requireNonNull(this.getTypes(), "context is null");
            Objects.requireNonNull(this.getVerifiableCredentials(), "VCs is null");
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Invalid VerifiablePresentation: %s", SerializeUtil.toJson(json)), e);
        }
    }

    @NonNull
    public URI getId() {
        return SerializeUtil.asURI(this.get(ID));
    }

    @NonNull
    public List<String> getTypes() {
        return SerializeUtil.asStringList(this.get(TYPE));
    }

    @NonNull
    public List<VerifiableCredential> getVerifiableCredentials() {
        final List<Map<String, Object>> credentials = (List<Map<String, Object>>) this.get(VERIFIABLE_CREDENTIAL);
        return credentials.stream().map(VerifiableCredential::new).collect(Collectors.toList());
    }

}
