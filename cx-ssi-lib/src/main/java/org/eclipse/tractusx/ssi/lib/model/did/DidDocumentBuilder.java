package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DidDocumentBuilder {
  private URI id;
  private final List<VerificationMethod> verificationMethods = new ArrayList<>();

  public DidDocumentBuilder id(URI id) {
    this.id = id;
    return this;
  }

  public DidDocumentBuilder verificationMethods(List<VerificationMethod> verificationMethods) {
    this.verificationMethods.addAll(verificationMethods);
    return this;
  }

  public DidDocumentBuilder verificationMethod(VerificationMethod verificationMethod) {
    this.verificationMethods.add(verificationMethod);
    return this;
  }

  public DidDocument build() {
    return new DidDocument(
        Map.of(
            DidDocument.CONTEXT, DidDocument.DEFAULT_CONTEXT,
            DidDocument.ID, id,
            DidDocument.VERIFICATION_METHOD, verificationMethods));
  }
}
