/*
 *  Copyright (c) 2020 - 2022 Fraunhofer Institute for Software and Systems Engineering
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Fraunhofer Institute for Software and Systems Engineering - initial API and implementation
 *       ZF Friedrichshafen AG - enable asset filtering
 *
 */

package org.eclipse.tractusx.ssi.lab.connector.extensions.ids.message.send;

import com.fasterxml.jackson.databind.JsonNode;
import de.fraunhofer.iais.eis.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import org.eclipse.edc.catalog.spi.Catalog;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.MultipartSenderDelegate;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.SenderDelegateContext;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.IdsMultipartParts;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.response.MultipartResponse;
import org.eclipse.edc.protocol.ids.spi.domain.IdsConstants;
import org.eclipse.edc.protocol.ids.util.CalendarUtil;
import org.eclipse.edc.spi.EdcException;
import org.eclipse.edc.spi.query.QuerySpec;

/** MultipartSenderDelegate for connector catalog requests. */
public class MyMultipartCatalogDescriptionRequestSender
    implements MultipartSenderDelegate<CatalogRequest, Catalog> {

  private final SenderDelegateContext context;

  public MyMultipartCatalogDescriptionRequestSender(SenderDelegateContext context) {
    this.context = context;
  }

  /**
   * Builds a {@link de.fraunhofer.iais.eis.DescriptionRequestMessage} for requesting another
   * connector's self description. Includes paging information defined in the {@link
   * CatalogRequest}.
   *
   * @param request the request.
   * @param token the dynamic attribute token.
   * @return a DescriptionRequestMessage
   */
  @Override
  public Message buildMessageHeader(CatalogRequest request, DynamicAttributeToken token) {
    var message =
        new DescriptionRequestMessageBuilder()
            ._modelVersion_(IdsConstants.INFORMATION_MODEL_VERSION)
            ._issued_(CalendarUtil.gregorianNow())
            ._securityToken_(token)
            ._issuerConnector_(context.getConnectorId().toUri())
            ._senderAgent_(context.getConnectorId().toUri())
            ._recipientConnector_(Collections.singletonList(URI.create(request.getConnectorId())))
            .build();
    // TODO: IDS REFACTORING: incorporate this into the protocol itself
    message.setProperty(QuerySpec.QUERY_SPEC, request.getQuerySpec());
    message.setProperty(IdsConstants.IDS_WEBHOOK_ADDRESS_PROPERTY, context.getIdsWebhookAddress());
    return message;
  }

  @Override
  public String buildMessagePayload(CatalogRequest request) throws Exception {
    return null;
  }

  /**
   * Parses the response content and extracts the catalog from the received self description.
   *
   * @param parts container object for response header and payload {@link InputStream}s.
   * @return the other connector's catalog
   */
  @Override
  public MultipartResponse<Catalog> getResponseContent(IdsMultipartParts parts) throws Exception {
    var header = context.getObjectMapper().readValue(parts.getHeader(), Message.class);

    if (parts.getPayload() == null) {
      throw new EdcException("Payload was null but connector self-description was expected");
    }

    var baseConnector = getBaseConnector(parts);
    if (baseConnector.getResourceCatalog() == null
        || baseConnector.getResourceCatalog().isEmpty()) {
      throw new EdcException(
          "Resource catalog is null in connector self-description, should not happen");
    }

    // If there is no resource catalog in connector self-description, we initialize a new empty
    // resource catalog.
    var resourceCatalog =
        baseConnector.getResourceCatalog().stream()
            .findFirst()
            .orElse(new ResourceCatalogBuilder().build());

    if (catalogDoesNotContainAnyOfferResource(resourceCatalog)) {
      createOfferResourcesFromProperties(resourceCatalog);
    }

    var transformResult =
        context.getTransformerRegistry().transform(resourceCatalog, Catalog.class);
    if (transformResult.failed()) {
      throw new EdcException(
          String.format(
              "Could not transform ids data catalog: %s",
              String.join(", ", transformResult.getFailureMessages())));
    }

    return new MultipartResponse<>(header, transformResult.getContent());
  }

  @Override
  public List<Class<? extends Message>> getAllowedResponseTypes() {
    return List.of(DescriptionResponseMessageImpl.class);
  }

  @Override
  public Class<CatalogRequest> getMessageType() {
    return CatalogRequest.class;
  }

  private BaseConnector getBaseConnector(IdsMultipartParts parts) {
    try {
      var payload = Objects.requireNonNull(parts.getPayload());
      return context.getObjectMapper().readValue(payload.readAllBytes(), BaseConnector.class);
    } catch (IOException exception) {
      throw new EdcException(
          String.format(
              "Could not deserialize connector self-description: %s", exception.getMessage()));
    }
  }

  private void createOfferResourcesFromProperties(ResourceCatalog catalog) {
    var objectMapper = context.getObjectMapper();
    if (catalog.getProperties() != null) {
      for (Map.Entry<String, Object> entry : catalog.getProperties().entrySet()) {
        if ("ids:offeredResource".equals(entry.getKey())) {
          JsonNode node = objectMapper.convertValue(entry.getValue(), JsonNode.class);
          List<Resource> offeredResources = new LinkedList<>();
          for (JsonNode objNode : node.get("objectList")) {
            Map<String, Object> resource = new HashMap<>();
            resource.put("@type", "ids:Resource");
            resource.putAll(objectMapper.convertValue(objNode, Map.class));
            offeredResources.add(objectMapper.convertValue(resource, Resource.class));
          }
          catalog.setOfferedResource(offeredResources);
        }
      }
    }
  }

  private boolean catalogDoesNotContainAnyOfferResource(ResourceCatalog catalog) {
    return catalog.getOfferedResource() == null || catalog.getOfferedResource().isEmpty();
  }
}
