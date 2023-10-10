/*
 * ******************************************************************************
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
 * *******************************************************************************
 */

package org.eclipse.tractusx.ssi.lib.exception;

import java.net.URI;
import lombok.Getter;
import org.eclipse.tractusx.ssi.lib.model.did.Did;

@Getter
public class JwtSignatureCheckFailedException extends JwtException {
  private final Did issuerDid;
  private final URI verificationKey;

  public JwtSignatureCheckFailedException(Did issuerDid, URI verificationKey) {
    super(
        "JWT signature check failed for issuer "
            + issuerDid
            + " and verification key "
            + verificationKey);
    this.issuerDid = issuerDid;
    this.verificationKey = verificationKey;
  }
}
