package org.eclipse.tractusx.ssi.agent.lib.wallet;

import java.net.URI;
import java.util.List;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialAlreadyStoredException;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialNotFoundException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

public interface VerifiableCredentialWallet {

  List<VerifiableCredential> getAllCredentials();

  void storeCredential(VerifiableCredential verifiableCredential)
      throws CredentialAlreadyStoredException;

  VerifiableCredential getCredentialByType(String credentialType)
      throws CredentialNotFoundException;

  VerifiableCredential getCredentialById(URI id) throws CredentialNotFoundException;
}
