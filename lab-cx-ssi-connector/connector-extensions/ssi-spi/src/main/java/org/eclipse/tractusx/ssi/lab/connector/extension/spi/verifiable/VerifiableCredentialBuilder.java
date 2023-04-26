package org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class VerifiableCredentialBuilder {

  private URI id;
  private List<String> types;
  private URI issuer;
  private VerifiableCredentialSubject credentialSubject;

  public VerifiableCredentialBuilder id(URI id) {
    this.id = id;
    return this;
  }

  public VerifiableCredentialBuilder type(List<String> types) {
    this.types = types;
    return this;
  }

  public VerifiableCredentialBuilder issuer(URI issuer) {
    this.issuer = issuer;
    return this;
  }

  public VerifiableCredentialBuilder credentialSubject(
      VerifiableCredentialSubject credentialSubject) {
    this.credentialSubject = credentialSubject;
    return this;
  }

  public VerifiableCredential build() {

    // Map.of does not work, as proof can be null
    Map<String, Object> map = new HashMap<>();
    map.put(VerifiableCredential.ID, id.toString());
    map.put(VerifiableCredential.TYPE, types);
    map.put(VerifiableCredential.ISSUER, issuer.toString());
    map.put(VerifiableCredential.CREDENTIAL_SUBJECT, credentialSubject);

    return new VerifiableCredential(map);
  }
}
