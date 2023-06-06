/********************************************************************************
 * Copyright (c) 2021,2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

package org.eclipse.tractusx.ssi.lib.serialization.jsonLd;

import org.eclipse.tractusx.ssi.lib.exception.InvalidJsonLdException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.presentation.VerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.serialization.jwt.SerializedVerifiablePresentation;
import org.eclipse.tractusx.ssi.lib.validation.JsonLdValidator;
import org.eclipse.tractusx.ssi.lib.validation.JsonLdValidatorImpl;

public class JsonLdSerializerImpl implements JsonLdSerializer {

  @Override
  public SerializedVerifiablePresentation serializePresentation(
      VerifiablePresentation verifiablePresentation) {

    final com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation =
        DanubeTechMapper.map(verifiablePresentation);
    final String dtPresentationJson = dtPresentation.toJson();

    return new SerializedVerifiablePresentation(dtPresentationJson);
  }

  @Override
  public VerifiablePresentation deserializePresentation(
      SerializedVerifiablePresentation serializedPresentation) throws InvalidJsonLdException {
    return deserializePresentation(serializedPresentation, true);
  }

  @Override
  public VerifiablePresentation deserializePresentation(
      SerializedVerifiablePresentation serializedPresentation, boolean validateJsonLd)
      throws InvalidJsonLdException {

    final String serializedPresentationJson = serializedPresentation.getJson();
    final com.danubetech.verifiablecredentials.VerifiablePresentation dtPresentation =
        com.danubetech.verifiablecredentials.VerifiablePresentation.fromJson(
            serializedPresentationJson);

    final VerifiablePresentation presentation = DanubeTechMapper.map(dtPresentation);

    if (validateJsonLd) {
      JsonLdValidator jsonLdValidator = new JsonLdValidatorImpl();
      jsonLdValidator.validate(presentation);
    }

    return presentation;
  }
}
