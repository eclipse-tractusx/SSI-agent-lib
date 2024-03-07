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
 * *******************************************************************************
 */

package org.eclipse.tractusx.ssi.examples;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.tractusx.ssi.lib.crypt.IKeyGenerator;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.crypt.KeyPair;
import org.eclipse.tractusx.ssi.lib.crypt.x25519.X25519Generator;
import org.eclipse.tractusx.ssi.lib.did.web.DidWebFactory;
import org.eclipse.tractusx.ssi.lib.exception.key.KeyGenerationException;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocumentBuilder;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationMethod;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationMethodBuilder;
import org.eclipse.tractusx.ssi.lib.model.did.VerificationMethod;

/** This is example class to demonstrate how to create @{@link DidDocument} using Ed25519 key */
public class BuildDIDDocEd25519VerificationKey2020 {

  private BuildDIDDocEd25519VerificationKey2020() {
    // static
  }

  /**
   * Build did document
   *
   * @param hostName the host name
   * @return the did document
   * @throws KeyGenerationException the key generation exception
   */
  public static DidDocument buildDidDocument(String hostName) throws KeyGenerationException {
    final Did did = DidWebFactory.fromHostname(hostName);

    // Extracting keys
    IKeyGenerator keyGenerator = new X25519Generator();
    KeyPair keyPair = keyGenerator.generateKey();
    IPublicKey publicKey = keyPair.getPublicKey();

    final MultibaseString publicKeyBase = MultibaseFactory.create(publicKey.asByte());

    // Building Verification Methods:
    final List<VerificationMethod> verificationMethods = new ArrayList<>();
    final Ed25519VerificationMethodBuilder builder = new Ed25519VerificationMethodBuilder();
    final Ed25519VerificationMethod key =
        builder
            .id(URI.create(did.toUri() + "#key-" + 1))
            .controller(did.toUri())
            .publicKeyMultiBase(publicKeyBase)
            .build();
    verificationMethods.add(key);

    final DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
    didDocumentBuilder.id(did.toUri());
    didDocumentBuilder.verificationMethods(verificationMethods);

    return didDocumentBuilder.build();
  }
}
