package org.eclipse.tractusx.ssi.agent.lib;

import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialAlreadyStoredException;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialNotFoundException;
import org.eclipse.tractusx.ssi.agent.lib.wallet.SsiMemoryStorageWallet;
import org.eclipse.tractusx.ssi.agent.lib.wallet.VerifiableCredentialWallet;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519KeySet;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

@Builder
@AllArgsConstructor
public class WebAgent {

  private final String hostName;
  // TODO Test Signing Key on initialize
  private final Ed25519KeySet signingKeySet;

  @Builder.Default private final VerifiableCredentialWallet wallet = new SsiMemoryStorageWallet();

  public Did getDid() {
    return DidWebFactory.fromHostname(hostName);
  }

  public DidDocument getDidDocument() {
    final Did did = DidWebFactory.fromHostname(hostName);
    return new DidDocumentBuilder()
        .withDid(did)
        .withEd25519PublicKey(signingKeySet.getPublicKey())
        .build();
  }

  public void storeCredential(VerifiableCredential verifiableCredential)
      throws CredentialAlreadyStoredException {
    wallet.storeCredential(verifiableCredential);
  }

  public VerifiableCredential getCredentialById(URI id) throws CredentialNotFoundException {
    return wallet.getCredentialById(id);
  }

  public VerifiableCredential getCredentialByType(String type) throws CredentialNotFoundException {
    return wallet.getCredentialByType(type);
  }

  public List<VerifiableCredential> getAllCredentials() {
    return wallet.getAllCredentials();
  }
}
