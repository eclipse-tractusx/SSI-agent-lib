package org.eclipse.tractusx.ssi.lab.connector.extension.spi.policy;

import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.eclipse.edc.policy.model.Expression;

@Builder
@Getter
@ToString
@Jacksonized
@JsonTypeName("tx:verifiableCredentialExpression")
public class VerifiableCredentialExpression extends Expression {

  public static VerifiableCredentialExpression fromString(String s) throws JsonProcessingException {
    return new ObjectMapper().readValue(s, VerifiableCredentialExpression.class);
  }

  @NonNull private final String type;

  @NonNull private final List<JsonPath> paths;

  @Override
  public <R> R accept(Visitor<R> visitor) {
    return (R) this;
  }

  @SneakyThrows
  public String asString() {
    return new ObjectMapper().writeValueAsString(this);
  }
}
