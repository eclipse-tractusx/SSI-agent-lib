package org.eclipse.tractusx.ssi.lab.connector.extensions.ids.message.send;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.util.IOUtils;
import com.nimbusds.jwt.SignedJWT;
import de.fraunhofer.iais.eis.DescriptionRequestMessageBuilder;
import de.fraunhofer.iais.eis.DescriptionResponseMessageImpl;
import de.fraunhofer.iais.eis.DynamicAttributeToken;
import de.fraunhofer.iais.eis.Message;
import java.io.InputStream;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.MultipartSenderDelegate;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.SenderDelegateContext;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.IdsMultipartParts;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.domain.IdsConstants;
import org.eclipse.edc.protocol.ids.util.CalendarUtil;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.message.VerifiableCredentialRequest;

@RequiredArgsConstructor
public class VerifiableCredentialRequestMessageSender
    implements MultipartSenderDelegate<VerifiableCredentialRequest, SignedJWT> {

  private final ObjectMapper objectMapper;
  private final SenderDelegateContext context;
  private final Monitor monitor;

  @Override
  public Message buildMessageHeader(
      VerifiableCredentialRequest request, DynamicAttributeToken token) throws Exception {
    var message =
        new DescriptionRequestMessageBuilder()
            ._modelVersion_(IdsConstants.INFORMATION_MODEL_VERSION)
            ._issued_(CalendarUtil.gregorianNow())
            ._securityToken_(token)
            ._issuerConnector_(context.getConnectorId().toUri())
            ._senderAgent_(context.getConnectorId().toUri())
            ._recipientConnector_(
                Collections.singletonList(URI.create(request.getConnectorAddress())))
            .build();

    message.setProperty(IdsConstants.IDS_WEBHOOK_ADDRESS_PROPERTY, context.getIdsWebhookAddress());
    return message;
  }

  @Override
  public String buildMessagePayload(VerifiableCredentialRequest verifiableCredentialRequest)
      throws Exception {
    final List<String> requestedTypes =
        verifiableCredentialRequest.getRequestedVerifiableCredentialTypes();
    final VerifiableCredentialRequestPayload payload =
        new VerifiableCredentialRequestPayload(requestedTypes);
    return objectMapper.writeValueAsString(payload);
  }

  @Override
  public MultipartResponse<SignedJWT> getResponseContent(IdsMultipartParts idsMultipartParts)
      throws Exception {
    var header = context.getObjectMapper().readValue(idsMultipartParts.getHeader(), Message.class);

    final InputStream is = idsMultipartParts.getPayload();
    if (is == null) {
      throw new EdcException(
          String.format(
              "Multipart payload empty. Expected verifiable credential. Header: %s",
              context.getObjectMapper().writeValueAsString(header)));
    }

    final String payload = IOUtils.readInputStreamToString(is);

    monitor.info(
        String.format(
            "Received credential request response %s from %s. Content: %s",
            header.getId(), header.getIssuerConnector().toString(), payload));

    final SignedJWT jwt = SignedJWT.parse(payload);
    return new MultipartResponse<>(header, jwt);
  }

  @Override
  public List<Class<? extends Message>> getAllowedResponseTypes() {
    return List.of(DescriptionResponseMessageImpl.class);
  }

  @Override
  public Class<VerifiableCredentialRequest> getMessageType() {
    return VerifiableCredentialRequest.class;
  }
}
