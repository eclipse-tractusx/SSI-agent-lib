package org.eclipse.tractusx.ssi.lib.model.verifiable;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.NonNull;
import org.eclipse.tractusx.ssi.lib.model.JsonLdObject;
import org.eclipse.tractusx.ssi.lib.model.proof.Proof;
import org.eclipse.tractusx.ssi.lib.serialization.SerializeUtil;

public abstract class Verifiable extends JsonLdObject {

  public static final String ID = "id";
  public static final String TYPE = "type";
  public static final String PROOF = "proof";

  private VerifiableType verifableType;

  public enum VerifiableType {
    VC,
    VP
  }

  public Verifiable(Map<String, Object> json, VerifiableType type) {
    super(json);
    Objects.requireNonNull(this.getId());
    Objects.requireNonNull(this.getTypes());
    Objects.requireNonNull(type, "Verifable Type should not be null");
    this.verifableType = type;
    this.checkId();
  }

  @NonNull
  public URI getId() {
    return SerializeUtil.asURI(this.get(ID));
  }

  @NonNull
  public List<String> getTypes() {
    return (List<String>) this.get(TYPE);
  }

  public Proof getProof() {

    final Object subject = this.get(PROOF);

    if (subject == null) {
      return null;
    }

    return new Proof((Map<String, Object>) subject);
  }

  public VerifiableType getType() {
    return verifableType;
  }

  /**
   * There exists an error that prevents quads from being created correctly. as this interferes with
   * the credential signature, this is a security risk see
   * https://github.com/eclipse-tractusx/SSI-agent-lib/issues/4 as workaround we ensure that the
   * credential ID starts with one or more letters followed by a colon
   */
  private void checkId() {
    final String regex = "^[a-zA-Z]+:.*$";
    if (!this.getId().toString().matches(regex)) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid VerifiableCredential. Credential ID must start with one or more letters followed by a colon. This is a temporary mitigation for the following security risk: %s",
              "https://github.com/eclipse-tractusx/SSI-agent-lib/issues/4"));
    }
  }
}
