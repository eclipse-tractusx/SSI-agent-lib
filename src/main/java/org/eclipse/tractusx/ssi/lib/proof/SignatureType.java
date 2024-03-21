/*
 * ******************************************************************************
 * Copyright (c) 2021,2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.ssi.lib.proof;

import com.nimbusds.jose.JWSAlgorithm;

public enum SignatureType {
  ED25519("Ed25519Signature2020", ""),
  JWS(Constants.JSON_WEB_SIGNATURE_2020, JWSAlgorithm.EdDSA.getName()),
  JWS_SEC_P_256K1(Constants.JSON_WEB_SIGNATURE_2020, JWSAlgorithm.ES256K.getName()),
  JWS_P256(Constants.JSON_WEB_SIGNATURE_2020, JWSAlgorithm.ES256.getName()),
  JWS_P384(Constants.JSON_WEB_SIGNATURE_2020, JWSAlgorithm.ES384.getName()),
  JWS_RSA(Constants.JSON_WEB_SIGNATURE_2020, JWSAlgorithm.PS256.getName());

  public final String type;

  public final String algorithm;

  private SignatureType(String type, String algorithm) {
    this.type = type;
    this.algorithm = algorithm;
  }

  private static class Constants {

    public static final String JSON_WEB_SIGNATURE_2020 = "JsonWebSignature2020";
  }
}
