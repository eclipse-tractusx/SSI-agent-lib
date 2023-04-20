package org.eclipse.tractusx.ssi.agent.app.delegates;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.ssi.agent.api.VerifiableCredentialsApiDelegate;
import org.eclipse.tractusx.ssi.agent.app.services.AgentService;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialAlreadyStoredException;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialNotFoundException;
import org.eclipse.tractusx.ssi.agent.model.VerifiableCredential;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class CredentialsApiDelegateImpl implements VerifiableCredentialsApiDelegate {

  private static final String LOG_JUST_STORED_CREDENTIAL_NOT_FOUND =
      "Just stored credential not found. This should not happen.";
  private static final String LOG_CREDENTIAL_ALREADY_SIGNED =
      "Cannot sign credential. Credential already signed by proof. Id: {}";
  private static final String LOG_CREDENTIAL_ALREADY_STORED = "Credential already stored. Id: {}";
  private static final String LOG_CREDENTIAL_CANNOT_SIGNED_STORED_CREDENTIAL =
      "Cannot sign credential. Credential already stored. Id: {}";
  private static final String LOG_CREDENTIAL_SIGNED = "Credential signed. Id: {}";
  private static final String LOG_CREDENTIAL_STORED = "Credential stored. Id: {}";
  private static final String LOG_CREDENTIALS_RETURNED = "Return all Credentials. Count: {}";

  private final AgentService agentService;

  @Override
  public ResponseEntity<VerifiableCredential> signCredential(
      VerifiableCredential unsignedVerifiableCredential) {
    if (unsignedVerifiableCredential.getProof() != null) {
      log.info(LOG_CREDENTIAL_ALREADY_SIGNED, unsignedVerifiableCredential.getId());
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    try {
      final VerifiableCredential signedCredential =
          agentService.signVerifiableCredential(unsignedVerifiableCredential);
      agentService.storeVerifiableCredential(signedCredential);
      final VerifiableCredential storedCredential =
          agentService.getVerifiableCredentialById(signedCredential.getId());

      log.info(LOG_CREDENTIAL_SIGNED, storedCredential.getId());
      return new ResponseEntity<>(storedCredential, null, HttpStatus.CREATED);
    } catch (CredentialAlreadyStoredException e) {
      log.info(
          LOG_CREDENTIAL_CANNOT_SIGNED_STORED_CREDENTIAL, unsignedVerifiableCredential.getId());
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    } catch (CredentialNotFoundException e) {
      log.error(LOG_JUST_STORED_CREDENTIAL_NOT_FOUND, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public ResponseEntity<List<VerifiableCredential>> getCredentials() {
    final List<VerifiableCredential> credentials = agentService.getVerifiableCredentials();

    log.info(LOG_CREDENTIALS_RETURNED, credentials.size());
    return new ResponseEntity<>(credentials, null, HttpStatus.OK);
  }

  @Override
  public ResponseEntity<VerifiableCredential> storeCredential(
      VerifiableCredential verifiableCredential) {
    try {
      agentService.storeVerifiableCredential(verifiableCredential);
      final VerifiableCredential storedCredential =
          agentService.getVerifiableCredentialById(verifiableCredential.getId());

      log.info(LOG_CREDENTIAL_STORED, storedCredential.getId());
      return new ResponseEntity<>(storedCredential, null, HttpStatus.CREATED);
    } catch (CredentialAlreadyStoredException e) {
      log.info(LOG_CREDENTIAL_ALREADY_STORED, verifiableCredential.getId());
      return new ResponseEntity<>(HttpStatus.CONFLICT);
    } catch (CredentialNotFoundException e) {
      log.error(LOG_JUST_STORED_CREDENTIAL_NOT_FOUND, e);
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}
