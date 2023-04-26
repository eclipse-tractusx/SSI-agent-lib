package org.eclipse.tractusx.ssi.lab.system.test.client.connector.data;

import lombok.NonNull;
import lombok.Value;

@Value
public class BusinessPartnerNumberConstraint implements Constraint {

  @NonNull String businessPartnerNumber;
}
