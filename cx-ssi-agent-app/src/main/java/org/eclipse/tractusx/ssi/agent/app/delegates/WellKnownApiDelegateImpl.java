package org.eclipse.tractusx.ssi.agent.app.delegates;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.tractusx.ssi.agent.api.WellKnownApiDelegate;
import org.eclipse.tractusx.ssi.agent.app.services.AgentService;
import org.eclipse.tractusx.ssi.agent.model.DidDocument;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class WellKnownApiDelegateImpl implements WellKnownApiDelegate {

  private static final String LOG_RETURN_DID_DOCUMENT = "Return DID document";

  private final AgentService agentService;

  @Override
  public ResponseEntity<DidDocument> didDocument() {
    final var didDocument = agentService.getDidDocument();

    log.info(LOG_RETURN_DID_DOCUMENT);
    return new ResponseEntity<>(didDocument, null, HttpStatus.OK);
  }
}
