package org.eclipse.tractusx.ssi.lab.connector.extensions.ids.transform;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.fraunhofer.iais.eis.util.RdfResource;
import org.eclipse.edc.policy.model.Expression;
import org.eclipse.edc.policy.model.LiteralExpression;
import org.eclipse.edc.protocol.ids.spi.transform.IdsTypeTransformer;
import org.eclipse.edc.transform.spi.TransformerContext;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.policy.VerifiableCredentialExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RdfResourceToVerifiableCredentialExpressionTransformer
    implements IdsTypeTransformer<RdfResource, Expression> {
  @Override
  public Class<RdfResource> getInputType() {
    return RdfResource.class;
  }

  @Override
  public Class<Expression> getOutputType() {
    return Expression.class;
  }

  @Override
  public @Nullable Expression transform(
      @NotNull RdfResource rdfResource, @NotNull TransformerContext transformerContext) {
    try {
      return VerifiableCredentialExpression.fromString(rdfResource.getValue());
    } catch (JsonProcessingException e) {
      return new LiteralExpression(rdfResource.getValue());
    }
  }
}
