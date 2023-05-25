package org.eclipse.tractusx.ssi.lab.connector.extensions.ids.message.receive;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.fraunhofer.iais.eis.DescriptionRequestMessage;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.edc.protocol.ids.api.multipart.handler.Handler;
import org.eclipse.edc.protocol.ids.api.multipart.message.MultipartRequest;
import org.eclipse.edc.protocol.ids.api.multipart.message.MultipartResponse;
import org.eclipse.edc.protocol.ids.api.multipart.util.ResponseUtil;
import org.eclipse.edc.protocol.ids.spi.domain.IdsConstants;
import org.eclipse.edc.protocol.ids.spi.types.IdsId;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgent;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgentRegistry;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.registry.IdsConnectorRegistry;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiableCredential;
import org.eclipse.tractusx.ssi.lab.connector.extensions.ids.message.send.VerifiableCredentialRequestPayload;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class VerifiableCredentialRequestHandler implements Handler {

  private final ObjectMapper objectMapper;
  private final SsiAgentRegistry ssiAgentRegistry;
  private final Monitor monitor;
  private final IdsId connectorId;
  // workaround
  // the policy engine needs to be able to enforce policies based on what's in the security token
  // (verifiable presentation)
  // part of this information is a  DID. We need to map the did to an IDS url.
  // this canHandle method is called first, so we use it for now to create the mapping.
  private final IdsConnectorRegistry idsConnectorRegistry;

  @Override
  public boolean canHandle(@NotNull MultipartRequest multipartRequest) {
    final URI did =
        URI.create(
            multipartRequest
                .getHeader()
                .getIssuerConnector()
                .toString()
                .replace("urn:connector:", ""));
    final URI endEndpoint =
        (URI)
            multipartRequest
                .getHeader()
                .getProperties()
                .get(IdsConstants.IDS_WEBHOOK_ADDRESS_PROPERTY);
    idsConnectorRegistry.register(did, endEndpoint);

    if (!(multipartRequest.getHeader() instanceof DescriptionRequestMessage)) {
      return false;
    }

    final String payload = multipartRequest.getPayload();
    if (payload == null) {
      return false;
    }

    // TODO instead of payload look at possibility to use 'requestedElement' field
    try {
      var request = objectMapper.readValue(payload, VerifiableCredentialRequestPayload.class);
      return request != null;
    } catch (JsonProcessingException e) {
      return false;
    }
  }

  @Override
  @SneakyThrows({JsonProcessingException.class})
  public @NotNull MultipartResponse handleRequest(@NotNull MultipartRequest multipartRequest) {
    var message = (DescriptionRequestMessage) multipartRequest.getHeader();

    final SsiAgent ssiAgent = ssiAgentRegistry.getConfiguredAgent();
    final String payload = multipartRequest.getPayload();
    var vcRequest = objectMapper.readValue(payload, VerifiableCredentialRequestPayload.class);

    final List<VerifiableCredential> verifiableCredentials = new ArrayList<>();
    for (var vcType : vcRequest.getRequestedVerifiableCredentialTypes()) {
      var vc = ssiAgent.getByType(vcType);
      if (vc == null) {
        monitor.warning(
            String.format(
                "Received description request for verifiable credential(s) from '%s'. Could not provide credential type '%s'. (message-id: '%s', requested-credentials: '[%s]')",
                message.getIssuerConnector(),
                vcType,
                message.getId(),
                String.join(", ", vcRequest.getRequestedVerifiableCredentialTypes())));
      } else {
        verifiableCredentials.add(vc);
      }
    }

    final URI audience =
        (URI) message.getProperties().get(IdsConstants.IDS_WEBHOOK_ADDRESS_PROPERTY);
    final String vpJwt =
        ssiAgent
            .createVerifiablePresentationAsJwt(verifiableCredentials, audience.toString())
            .serialize();

    monitor.info(
        String.format(
            "Answering description request %s from %s with verifiable presentation %s.",
            multipartRequest.getHeader().getId(),
            multipartRequest.getHeader().getIssuerConnector().toString(),
            vpJwt));
    return ResponseUtil.createMultipartResponse(
        ResponseUtil.descriptionResponse(message, connectorId), vpJwt);
  }
}
