package org.eclipse.tractusx.ssi.lib.resolver;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;

public class DidDocumentResolverRegistryImpl implements DidDocumentResolverRegistry {

  private final Map<DidMethod, DidDocumentResolver> resolvers = new HashMap<>();

  @Override
  public DidDocumentResolver get(DidMethod didMethod) {
    if (!resolvers.containsKey(didMethod))
      throw new SsiException(String.format("Resolver for method '%s' not found", didMethod));

    return resolvers.get(didMethod);
  }

  @Override
  public void register(DidDocumentResolver resolver) {
    if (resolvers.containsKey(resolver.getSupportedMethod()))
      throw new SsiException(
          String.format(
              "Resolver for method '%s' is already registered", resolver.getSupportedMethod()));
    resolvers.put(resolver.getSupportedMethod(), resolver);
  }

  @Override
  public void unregister(DidDocumentResolver resolver) {
    if (!resolvers.containsKey(resolver.getSupportedMethod()))
      throw new SsiException(
          String.format("Resolver for method '%s' not registered", resolver.getSupportedMethod()));
    resolvers.remove(resolver.getSupportedMethod());
  }
}
