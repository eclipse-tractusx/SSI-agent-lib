package org.eclipse.tractusx.ssi.agent.app.map;

import java.util.List;
import org.eclipse.tractusx.ssi.agent.model.DidDocument;
import org.eclipse.tractusx.ssi.agent.model.DidDocumentVerificationMethod;

public class DidDocumentMapper {

  public static DidDocument map(org.eclipse.tractusx.ssi.lib.model.did.DidDocument didDocument) {

    final List<DidDocumentVerificationMethod> verificationMethods =
        didDocument.getVerificationMethods().stream().map(VerificationMethodMapper::map).toList();

    var document = new DidDocument();
    document.atContext(List.of("https://w3id.org/did/v1"));
    document.setId(didDocument.getId());
    document.setVerificationMethod(verificationMethods);

    return document;
  }
}
