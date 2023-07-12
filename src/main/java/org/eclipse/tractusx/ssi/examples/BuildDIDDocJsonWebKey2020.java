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

package org.eclipse.tractusx.ssi.examples;

import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import java.util.ArrayList;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.crypt.jwk.JsonWebKey;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PrivateKey;
import org.eclipse.tractusx.ssi.lib.crypt.x21559.x21559PublicKey;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.eclipse.tractusx.ssi.lib.model.did.*;

public class BuildDIDDocJsonWebKey2020 {
  @SneakyThrows
  public static DidDocument buildDidDocument(String hostName) {
    // Building DID and Key
    final Did did = DidWebFactory.fromHostname(hostName);
    OctetKeyPair octetKeyPair = new OctetKeyPairGenerator(Curve.Ed25519).keyID("1").generate();

    IPrivateKey privateKey = new x21559PrivateKey(octetKeyPair.getDecodedD());
    IPublicKey publicKey = new x21559PublicKey(octetKeyPair.getDecodedX());

    // JWK
    JsonWebKey jwk = new JsonWebKey(octetKeyPair.getKeyID(), publicKey, privateKey);

    // Building Verification Methods:
    final List<VerificationMethod> verificationMethods = new ArrayList<>();
    final JWKVerificationMethodBuilder builder = new JWKVerificationMethodBuilder();
    final JWKVerificationMethod key = builder.did(did).jwk(jwk).build();
    verificationMethods.add(key);

    final DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
    didDocumentBuilder.id(did.toUri());
    didDocumentBuilder.verificationMethods(verificationMethods);

    return didDocumentBuilder.build();
  }
}
