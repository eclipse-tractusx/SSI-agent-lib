package org.eclipse.tractusx.ssi.lib.model.verifiable.presentation;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

@NoArgsConstructor
public class VerifiablePresentationBuilder {
  private List<String> context = List.of(VerifiablePresentation.DEFAULT_CONTEXT);
  private URI id;
  private List<String> types;
  private List<VerifiableCredential> verifiableCredentials;

  public VerifiablePresentationBuilder context(List<String> context) {
    this.context = context;
    return this;
  }

  public VerifiablePresentationBuilder id(URI id) {
    this.id = id;
    return this;
  }

  public VerifiablePresentationBuilder type(List<String> types) {
    this.types = types;
    return this;
  }

  public VerifiablePresentationBuilder verifiableCredentials(
      List<VerifiableCredential> verifiableCredentials) {
    this.verifiableCredentials = verifiableCredentials;
    return this;
  }

  public VerifiablePresentation build() {
    Map<String, Object> map = new HashMap<>();
    map.put(VerifiablePresentation.CONTEXT, context);
    map.put(VerifiablePresentation.ID, id.toString());
    map.put(VerifiablePresentation.TYPE, types);
    map.put(VerifiablePresentation.VERIFIABLE_CREDENTIAL, verifiableCredentials);

    return new VerifiablePresentation(map);
  }
}
