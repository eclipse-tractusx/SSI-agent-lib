package org.eclipse.tractusx.ssi.agent.lib.wallet;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialAlreadyStoredException;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialNotFoundException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

@RequiredArgsConstructor
public class SsiMemoryStorageWallet implements VerifiableCredentialWallet {

  private static final String MESSAGE_NOT_FOUND_ID = "Credential with id %s not found in wallet";
  private static final String MESSAGE_NOT_FOUND_TYPE =
      "Credential with type %s not found in wallet";

  private final List<VerifiableCredential> credentials = new ArrayList<>();

  @Override
  public List<VerifiableCredential> getAllCredentials() {
    return Collections.unmodifiableList(credentials);
  }

  @Override
  public VerifiableCredential getCredentialByType(String credentialType)
      throws CredentialNotFoundException {
    return credentials.stream()
        .filter(c -> c.getTypes().contains(credentialType))
        .findFirst()
        .orElseThrow(
            () ->
                new CredentialNotFoundException(
                    String.format(MESSAGE_NOT_FOUND_TYPE, credentialType)));
  }

  @Override
  public void storeCredential(VerifiableCredential credential)
      throws CredentialAlreadyStoredException {
    if (credentials.stream().anyMatch(c -> c.getId().equals(credential.getId()))) {
      throw new CredentialAlreadyStoredException(credential.getId());
    }
    credentials.add(credential);
  }

  @Override
  public VerifiableCredential getCredentialById(URI id) throws CredentialNotFoundException {
    return credentials.stream()
        .filter(c -> c.getId().equals(id))
        .findFirst()
        .orElseThrow(
            () -> new CredentialNotFoundException(String.format(MESSAGE_NOT_FOUND_ID, id)));
  }
}
