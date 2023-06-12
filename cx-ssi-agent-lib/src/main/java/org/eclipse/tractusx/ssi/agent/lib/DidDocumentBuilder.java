package org.eclipse.tractusx.ssi.agent.lib;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationKey2020;

@NoArgsConstructor
public class DidDocumentBuilder {

  private Did did;
  private final List<byte[]> ed25519PublicKeys = new ArrayList<>();

  public DidDocumentBuilder withDid(Did did) {
    this.did = did;
    return this;
  }

  public DidDocumentBuilder withEd25519PublicKeys(List<byte[]> ed25519PublicKeys) {
    this.ed25519PublicKeys.addAll(ed25519PublicKeys);
    return this;
  }

  public DidDocumentBuilder withEd25519PublicKey(byte[] ed25519PublicKey) {
    this.ed25519PublicKeys.add(ed25519PublicKey);
    return this;
  }

  public DidDocument build() {
    if (did == null) throw new IllegalStateException("DID is not set.");

    final List<Ed25519VerificationKey2020> keys = new ArrayList<>();
    for (var i = 0; i < ed25519PublicKeys.size(); i++) {
      final byte[] publicKey = ed25519PublicKeys.get(i);
      final MultibaseString publicKeyBase = MultibaseFactory.create(publicKey);
      final Ed25519VerificationKey2020 key =
          Ed25519VerificationKey2020.builder()
              .id(URI.create(did.toUri() + "#key-" + i + 1))
              .controller(did.toUri())
              .multibase(publicKeyBase)
              .build();
      keys.add(key);
    }

    return DidDocument.builder().id(did.toUri()).verificationMethods(keys).build();
  }
}
