package org.eclipse.tractusx.ssi.lab.connector.extension.spi;

import static org.eclipse.edc.policy.engine.spi.PolicyEngine.ALL_SCOPES;

import org.eclipse.edc.policy.engine.spi.PolicyEngine;
import org.eclipse.edc.policy.engine.spi.RuleBindingRegistry;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.runtime.metamodel.annotation.Requires;
import org.eclipse.edc.spi.message.RemoteMessageDispatcherRegistry;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.edc.spi.system.ServiceExtension;
import org.eclipse.edc.spi.system.ServiceExtensionContext;
import org.eclipse.edc.spi.types.TypeManager;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.functions.VerifiableCredentialConstraintFunction;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.policy.JsonPath;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.policy.VerifiableCredentialExpression;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.registry.IdsConnectorRegistry;

@Requires({
  RuleBindingRegistry.class,
  PolicyEngine.class,
  RemoteMessageDispatcherRegistry.class,
  TypeManager.class,
  IdsConnectorRegistry.class
})
public class SsiPolicyServiceExtension implements ServiceExtension {
  public static final String EXTENSION_NAME = "SSI Policy Service Extension";
  public static final String VERIFIABLE_CREDENTIAL_CONSTRAINT_KEY = "VerifiableCredential";

  @Override
  public String name() {
    return EXTENSION_NAME;
  }

  @Override
  public void initialize(ServiceExtensionContext context) {
    final Monitor monitor = context.getMonitor();

    final TypeManager typeManager = context.getService(TypeManager.class);
    typeManager.registerTypes(VerifiableCredentialExpression.class, JsonPath.class);

    final RemoteMessageDispatcherRegistry remoteMessageDispatcherRegistry =
        context.getService(RemoteMessageDispatcherRegistry.class);

    final VerifiableCredentialCache verifiableCredentialCache =
        new VerifiableCredentialCache(remoteMessageDispatcherRegistry, 60 * 5, monitor);

    final IdsConnectorRegistry idsConnectorRegistry =
        context.getService(IdsConnectorRegistry.class);

    final RuleBindingRegistry ruleBindingRegistry = context.getService(RuleBindingRegistry.class);
    final PolicyEngine policyEngine = context.getService(PolicyEngine.class);

    ruleBindingRegistry.bind("USE", ALL_SCOPES);
    ruleBindingRegistry.bind(VERIFIABLE_CREDENTIAL_CONSTRAINT_KEY, ALL_SCOPES);

    final VerifiableCredentialConstraintFunction function =
        new VerifiableCredentialConstraintFunction(
            idsConnectorRegistry, verifiableCredentialCache, monitor);
    policyEngine.registerFunction(
        ALL_SCOPES, Permission.class, VERIFIABLE_CREDENTIAL_CONSTRAINT_KEY, function);
  }
}
