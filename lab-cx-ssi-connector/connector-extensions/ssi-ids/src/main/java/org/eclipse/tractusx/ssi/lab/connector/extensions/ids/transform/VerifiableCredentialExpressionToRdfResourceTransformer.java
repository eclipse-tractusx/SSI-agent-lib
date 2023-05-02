package org.eclipse.tractusx.ssi.lab.connector.extensions.ids.transform;

import de.fraunhofer.iais.eis.util.RdfResource;
import org.eclipse.edc.policy.model.Expression;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.protocol.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.policy.VerifiableCredentialExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// TODO Replace existing expression transformer
public class VerifiableCredentialExpressionToRdfResourceTransformer
    implements IdsTypeTransformer<Expression, RdfResource> {

  @Override
  public Class<Expression> getInputType() {
    return Expression.class;
  }

  @Override
  public Class<RdfResource> getOutputType() {
    return RdfResource.class;
  }

  @Override
  public @Nullable RdfResource transform(
      @NotNull Expression object, @NotNull TransformerContext context) {
    String value = null;
    if (object instanceof LiteralExpression) {
      value = ((LiteralExpression) object).asString();
    } else if (object instanceof VerifiableCredentialExpression) {
      value = ((VerifiableCredentialExpression) object).asString();
    }

    return value == null ? new RdfResource() : new RdfResource(value);
  }
}
