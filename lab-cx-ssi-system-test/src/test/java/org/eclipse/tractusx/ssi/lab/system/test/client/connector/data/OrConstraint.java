package org.eclipse.tractusx.ssi.lab.system.test.client.connector.data;

import java.util.List;
import lombok.NonNull;
import lombok.Value;

@Value
public class OrConstraint implements Constraint {

  @NonNull List<? extends Constraint> constraints;
}
