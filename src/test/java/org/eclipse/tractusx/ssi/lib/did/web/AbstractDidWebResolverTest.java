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

package org.eclipse.tractusx.ssi.lib.did.web;

import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethodIdentifier;
import org.eclipse.tractusx.ssi.lib.util.TestResourceUtil;

public abstract class AbstractDidWebResolverTest {
  protected DidDocument didWebDoc;
  protected Did validDidWeb;
  protected DidWebResolver resolver;
  protected static Did VALID_DID_KEY =
      new Did(
          new DidMethod("key"),
          new DidMethodIdentifier("z6Mkfriq1MqLBoPWecGoDLjguo1sB9brj6wT3qZ5BxkKpuP6"),
          null);

  public AbstractDidWebResolverTest(Did validDidWeb) {
    this.validDidWeb = validDidWeb;
    this.didWebDoc = new DidDocument(TestResourceUtil.getPublishedDidDocument());
    System.out.println("Testing with DID: " + validDidWeb.toString());
  }

  public abstract void shouldResolveValidWebDid() throws Exception;

  public abstract void shouldNotResolveNonWebDid() throws Exception;
}
