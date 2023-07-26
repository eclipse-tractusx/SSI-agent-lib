package org.eclipse.tractusx.ssi.lab.connector.extensions.ids.message.send;

import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class VerifiableCredentialRequestPayload {
  // TODO extend credential request payload, so that it can request certain
  // issuers or content
  // see https://w3c-ccg.github.io/vp-request-spec/
  List<String> requestedVerifiableCredentialTypes;
}
