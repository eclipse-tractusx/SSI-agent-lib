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

package org.eclipse.tractusx.ssi.lib.util.identity;

import java.net.URI;
import java.security.KeyPairGenerator;
import java.security.Security;
import java.util.List;

import lombok.SneakyThrows;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eclipse.tractusx.ssi.lib.base.MultibaseFactory;
import org.eclipse.tractusx.ssi.lib.model.MultibaseString;
import org.eclipse.tractusx.ssi.lib.model.did.*;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;

public class TestIdentityFactory {

    @SneakyThrows
    public static TestIdentity newIdentity() {

        final Did did = TestDidFactory.createRandom();

        Security.addProvider(new BouncyCastleProvider());
        var keyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair();

        final byte[] publicKey = keyPair.getPublic().getEncoded();
        final byte[] privateKey = keyPair.getPrivate().getEncoded();
        final MultibaseString publicKeyMultiBase = MultibaseFactory.create(publicKey);
        final Ed25519VerificationKey2020Builder ed25519VerificationKey2020Builder =
                new Ed25519VerificationKey2020Builder();
        final Ed25519VerificationKey2020 verificationMethod =
                ed25519VerificationKey2020Builder
                        .id(URI.create(did + "#key-1"))
                        .controller(URI.create(did + "#controller"))
                        .publicKeyMultiBase(publicKeyMultiBase)
                        .build();

        final DidDocumentBuilder didDocumentBuilder = new DidDocumentBuilder();
        final DidDocument didDocument =
                didDocumentBuilder.id(did.toUri()).verificationMethods(List.of(verificationMethod)).build();

        return new TestIdentity(did, didDocument, publicKey, privateKey);
    }
}
