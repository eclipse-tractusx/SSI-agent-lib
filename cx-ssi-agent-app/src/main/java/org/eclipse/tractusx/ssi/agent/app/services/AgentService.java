package org.eclipse.tractusx.ssi.agent.app.services;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.ssi.agent.app.map.DidDocumentMapper;
import org.eclipse.tractusx.ssi.agent.app.map.VerifiableCredentialMapper;
import org.eclipse.tractusx.ssi.agent.lib.WebAgent;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialAlreadyStoredException;
import org.eclipse.tractusx.ssi.agent.lib.exceptions.CredentialNotFoundException;
import org.eclipse.tractusx.ssi.agent.model.DidDocument;
import org.eclipse.tractusx.ssi.agent.model.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentService {

  private final SigningKeyService signingKeyService;
  private WebAgent agent;

  @Value("${host.name}")
  private String hostName;

  public DidDocument getDidDocument() {
    initializeAgent();
    return DidDocumentMapper.map(agent.getDidDocument());
  }

  public VerifiableCredential getVerifiableCredentialById(URI id)
      throws CredentialNotFoundException {
    initializeAgent();
    return VerifiableCredentialMapper.map(agent.getCredentialById(id));
  }

  public List<VerifiableCredential> getVerifiableCredentials() {
    initializeAgent();
    return agent.getAllCredentials().stream().map(VerifiableCredentialMapper::map).toList();
  }

  public void storeVerifiableCredential(VerifiableCredential verifiableCredential)
      throws CredentialAlreadyStoredException {
    initializeAgent();
    agent.storeCredential(VerifiableCredentialMapper.map(verifiableCredential));
  }

  private void initializeAgent() {
    if (agent == null) {
      initializeAgentSynchronized();
    }
  }

  @Synchronized
  private void initializeAgentSynchronized() {
    log.trace("Initializing SSI Agent Service");
    agent = new WebAgent(hostName, signingKeyService.getSigningKeySet());
  }

  public VerifiableCredential signVerifiableCredential(VerifiableCredential credential) {
    initializeAgent();
    final Did issuer = agent.getDid();
    return signingKeyService.signVerifiableCredential(credential, issuer);
  }
}
