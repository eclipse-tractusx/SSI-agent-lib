package org.eclipse.tractusx.ssi.lab.connector.extensions.ids;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.eclipse.edc.catalog.spi.CatalogRequest;
import org.eclipse.edc.protocol.ids.api.multipart.controller.MultipartController;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.IdsMultipartRemoteMessageDispatcher;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.MultipartSenderDelegate;
import org.eclipse.edc.protocol.ids.api.multipart.dispatcher.sender.SenderDelegateContext;
import org.eclipse.edc.protocol.ids.api.multipart.handler.DescriptionRequestHandler;
import org.eclipse.edc.protocol.ids.api.multipart.handler.Handler;
import org.eclipse.edc.protocol.ids.spi.transform.IdsTransformerRegistry;
import org.eclipse.edc.protocol.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.edc.protocol.ids.spi.types.IdsId;
import org.eclipse.edc.protocol.ids.spi.types.MessageProtocol;
import org.eclipse.edc.protocol.ids.transform.type.policy.ExpressionFromIdsRdfResourceTransformer;
import org.eclipse.edc.protocol.ids.transform.type.policy.ExpressionToIdsRdfResourceTransformer;
import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.message.RemoteMessageDispatcher;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.edc.spi.types.domain.message.RemoteMessage;
import org.eclipse.edc.web.spi.WebService;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.agent.SsiAgentRegistry;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.registry.IdsConnectorRegistry;
import org.eclipse.tractusx.ssi.lab.connector.extensions.ids.message.receive.VerifiableCredentialRequestHandler;
import org.eclipse.tractusx.ssi.lab.connector.extensions.ids.message.send.MyMultipartCatalogDescriptionRequestSender;
import org.eclipse.tractusx.ssi.lab.connector.extensions.ids.message.send.VerifiableCredentialRequestMessageSender;
import org.eclipse.tractusx.ssi.lab.connector.extensions.ids.transform.RdfResourceToVerifiableCredentialExpressionTransformer;
import org.eclipse.tractusx.ssi.lab.connector.extensions.ids.transform.VerifiableCredentialExpressionToRdfResourceTransformer;

@Requires({
  RemoteMessageDispatcherRegistry.class,
  WebService.class,
  TypeManager.class,
  SsiAgentRegistry.class,
  IdsTransformerRegistry.class,
  IdsConnectorRegistry.class
})
public class SsiIdsServiceExtension implements ServiceExtension {

  @Override
  public String name() {
    return "SSI IDS Service Extension";
  }

  RemoteMessageDispatcherRegistry remoteMessageDispatcherRegistry;
  WebService webService;
  TypeManager typeManager;
  SsiAgentRegistry ssiAgentRegistry;
  Monitor monitor;
  IdsTransformerRegistry idsTransformerRegistry;
  IdsConnectorRegistry idsConnectorRegistry;

  @Override
  public void initialize(ServiceExtensionContext context) {
    monitor = context.getMonitor();
    remoteMessageDispatcherRegistry = context.getService(RemoteMessageDispatcherRegistry.class);
    webService = context.getService(WebService.class);
    typeManager = context.getService(TypeManager.class);
    ssiAgentRegistry = context.getService(SsiAgentRegistry.class);
    idsTransformerRegistry = context.getService(IdsTransformerRegistry.class);
    idsConnectorRegistry = context.getService(IdsConnectorRegistry.class);
  }

  @Override
  public void start() {
    registerMessageDispatcher();
    registerMessageReceiver();
    registerIdsTransformer();
  }

  @SneakyThrows
  private void registerMessageDispatcher() {
    // normally this would go into the IDS Dispatcher Extensiorequiredn
    // as long as this is not contributed upstream we will cheat a little bit and register our own
    // handler using reflection
    final Field dispatcherRegistryField =
        remoteMessageDispatcherRegistry.getClass().getDeclaredField("dispatchers");
    dispatcherRegistryField.setAccessible(true);
    final Map<String, RemoteMessageDispatcher> dispatchers =
        (Map<String, RemoteMessageDispatcher>)
            dispatcherRegistryField.get(remoteMessageDispatcherRegistry);
    IdsMultipartRemoteMessageDispatcher idsMultipartRemoteMessageDispatcher =
        (IdsMultipartRemoteMessageDispatcher) dispatchers.get(MessageProtocol.IDS_MULTIPART);

    // we want our new handler to use the same context as the other handlers, so we will steal one
    // from the existing handlers
    final Field idsDelegatesField =
        idsMultipartRemoteMessageDispatcher.getClass().getDeclaredField("delegates");
    idsDelegatesField.setAccessible(true);
    final Map<Class<? extends RemoteMessage>, MultipartSenderDelegate<? extends RemoteMessage, ?>>
        delegateMap =
            (Map<
                    Class<? extends RemoteMessage>,
                    MultipartSenderDelegate<? extends RemoteMessage, ?>>)
                idsDelegatesField.get(idsMultipartRemoteMessageDispatcher);
    final MultipartSenderDelegate<? extends RemoteMessage, ?> delegate =
        delegateMap.values().stream().findFirst().orElseThrow();

    final Field delegateContextField = delegate.getClass().getDeclaredField("context");
    delegateContextField.setAccessible(true);
    final SenderDelegateContext context =
        (SenderDelegateContext) delegateContextField.get(delegate);

    // and now we can register our own handler
    final VerifiableCredentialRequestMessageSender verifiableCredentialRequestMessageSender =
        new VerifiableCredentialRequestMessageSender(typeManager.getMapper(), context, monitor);
    idsMultipartRemoteMessageDispatcher.register(verifiableCredentialRequestMessageSender);

    // replace catalog request handler with one that adds ids callback URL
    delegateMap.put(CatalogRequest.class, new MyMultipartCatalogDescriptionRequestSender(context));
  }

  @SneakyThrows
  private void registerMessageReceiver() {
    // normally this would go into the IDS Dispatcher Extension
    // as long as this is not contributed upstream we will cheat a little bit and register our own
    // handler using reflection
    final Field controllersField = webService.getClass().getDeclaredField("controllers");
    controllersField.setAccessible(true);
    final Map<String, Object> controllers = (Map<String, Object>) controllersField.get(webService);
    final List<Object> idsControllerResources = (List<Object>) controllers.get("ids");

    final MultipartController multipartController =
        (MultipartController)
            idsControllerResources.stream()
                .filter(r -> r instanceof MultipartController)
                .findFirst()
                .orElseThrow();

    final Field multipartHandlerField =
        multipartController.getClass().getDeclaredField("multipartHandlers");
    multipartHandlerField.setAccessible(true);
    final List<Handler> multipartHandlers =
        (List<Handler>) multipartHandlerField.get(multipartController);

    // get idsId
    final DescriptionRequestHandler descriptionRequestHandler =
        (DescriptionRequestHandler)
            multipartHandlers.stream()
                .filter(h -> h instanceof DescriptionRequestHandler)
                .findFirst()
                .orElseThrow();
    final Field idsIdField = descriptionRequestHandler.getClass().getDeclaredField("connectorId");
    idsIdField.setAccessible(true);
    final IdsId idsId = (IdsId) idsIdField.get(descriptionRequestHandler);

    // and now we can register our own handler
    // there is already a handler for DescriptionRequestMessages. So we must register our handler
    // before that one
    final VerifiableCredentialRequestHandler verifiableCredentialRequestHandler =
        new VerifiableCredentialRequestHandler(
            typeManager.getMapper(), ssiAgentRegistry, monitor, idsId, idsConnectorRegistry);
    multipartHandlers.add(0, verifiableCredentialRequestHandler);
  }

  @SneakyThrows
  private void registerIdsTransformer() {

    monitor.info("IDS Transformer Registry class: " + idsTransformerRegistry.getClass().getName());

    final Field transformersField =
        org.eclipse.edc.transform.spi.TypeTransformerRegistryImpl.class.getDeclaredField(
            "transformers");
    transformersField.setAccessible(true);
    final List<IdsTypeTransformer<?, ?>> transformers =
        (List<IdsTypeTransformer<?, ?>>) transformersField.get(idsTransformerRegistry);

    final ExpressionFromIdsRdfResourceTransformer fromTransformer =
        transformers.stream()
            .filter(t -> t instanceof ExpressionFromIdsRdfResourceTransformer)
            .map(t -> (ExpressionFromIdsRdfResourceTransformer) t)
            .findFirst()
            .orElseThrow();

    final ExpressionToIdsRdfResourceTransformer toTransformer =
        transformers.stream()
            .filter(t -> t instanceof ExpressionToIdsRdfResourceTransformer)
            .map(t -> (ExpressionToIdsRdfResourceTransformer) t)
            .findFirst()
            .orElseThrow();

    final int fromIndex = transformers.indexOf(fromTransformer);
    transformers.set(fromIndex, new RdfResourceToVerifiableCredentialExpressionTransformer());

    final int toIndex = transformers.indexOf(toTransformer);
    transformers.set(toIndex, new VerifiableCredentialExpressionToRdfResourceTransformer());
  }
}
