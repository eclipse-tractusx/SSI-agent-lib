package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.ToString;
import org.eclipse.tractusx.ssi.lib.util.SerializeUtil;

// spec https://www.w3.org/TR/did-core/#verification-methods
@ToString
public class VerificationMethod extends HashMap<String, Object> {
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String CONTROLLER = "controller";

    public VerificationMethod(Map<String, Object> json) {
        super(json);

        try {
            // validate getters
            Objects.requireNonNull(this.getId(), "id is null");
            Objects.requireNonNull(this.getType(), "type is null");
            Objects.requireNonNull(this.getController(), "controller is null");
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Invalid VerificationMethod: %s", SerializeUtil.toJson(json)), e);
        }
    }

    public URI getId() {
        return SerializeUtil.asURI(this.get(ID));
    }

    public String getType() {
        return (String) this.get(TYPE);
    }

    public URI getController() {
        return SerializeUtil.asURI(this.get(CONTROLLER));
    }
}
