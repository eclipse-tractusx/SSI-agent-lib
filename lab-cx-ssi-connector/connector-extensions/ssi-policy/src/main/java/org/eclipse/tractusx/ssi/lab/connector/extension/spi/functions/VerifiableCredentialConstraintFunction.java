/*
 * Copyright (c) 2022 Mercedes-Benz Tech Innovation GmbH
 * Copyright (c) 2021,2022 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.eclipse.tractusx.ssi.lab.connector.extension.spi.functions;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.eclipse.edc.policy.engine.spi.AtomicConstraintFunction;
import org.eclipse.edc.policy.engine.spi.PolicyContext;
import org.eclipse.edc.policy.model.Operator;
import org.eclipse.edc.policy.model.Permission;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.VerifiableCredentialCache;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.exceptions.VerifiableCredentialNotFoundException;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.policy.JsonPath;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.policy.VerifiableCredentialExpression;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.registry.IdsConnector;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.registry.IdsConnectorRegistry;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.util.JsonPathReader;
import org.eclipse.tractusx.ssi.lab.connector.extension.spi.verifiable.VerifiableCredential;

@RequiredArgsConstructor
public class VerifiableCredentialConstraintFunction
    implements AtomicConstraintFunction<Permission> {

  private final IdsConnectorRegistry idsConnectorRegistry;
  private final VerifiableCredentialCache verifiableCredentialCache;
  private final Monitor monitor;

  @Override
  public boolean evaluate(
      Operator operator, Object rightValue, Permission rule, PolicyContext context) {
    if (!(rightValue instanceof VerifiableCredentialExpression)) {
      // TODO LOG
      return false;
    }

    final VerifiableCredentialExpression expression = (VerifiableCredentialExpression) rightValue;

    final URI policyEnforcementSubjectDid =
        URI.create((String) context.getParticipantAgent().getClaims().get("did"));
    final IdsConnector idsConnector = idsConnectorRegistry.get(policyEnforcementSubjectDid);

    monitor.info(
        String.format("IDS CONNECTOR %S %s", idsConnector.getDid(), idsConnector.getIdsEndpoint()));

    final List<VerifiableCredential> credentials;
    try {
      credentials =
          verifiableCredentialCache.get(
              idsConnector.getIdsEndpoint(), List.of(expression.getType()));
    } catch (VerifiableCredentialNotFoundException e) {
      monitor.info(
          String.format(
              "Did not receive verifiable credentials [%s] from connector [%s]",
              String.join(", ", e.getNotFoundCredentials()), idsConnector.getDid()));
      return false;
    }

    final VerifiableCredential matchingCredential =
        credentials.stream()
            .filter(c -> c.getTypes().stream().anyMatch(t -> t.equals(expression.getType())))
            .findFirst()
            .orElse(null);
    if (matchingCredential == null) {
      monitor.info(
          String.format(
              "Did not receive verifiable credentials [%s] from connector [%s]",
              String.join(", ", expression.getType()), idsConnector.getDid()));
      return false;
    }

    final JsonPathReader jsonPathReader = new JsonPathReader(matchingCredential);
    for (final JsonPath jsonPath : expression.getPaths()) {
      var values = jsonPathReader.read(jsonPath.getJsonPath());
      if (values.stream().noneMatch(v -> v.equals(jsonPath.getValue()))) {
        return false;
      }
    }

    return true;
  }
}
