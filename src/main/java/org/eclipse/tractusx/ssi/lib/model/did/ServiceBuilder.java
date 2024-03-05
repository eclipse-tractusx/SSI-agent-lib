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

package org.eclipse.tractusx.ssi.lib.model.did;

import java.net.URI;
import java.util.Map;
import lombok.ToString;

@ToString
public class ServiceBuilder {
  private URI id;
  private String type;
  private URI serviceEndpoint;

  /**
   * Id service builder.
   *
   * @param id the id
   * @return the service builder
   */
  public ServiceBuilder id(URI id) {
    this.id = id;
    return this;
  }

  /**
   * Type service builder.
   *
   * @param type the type
   * @return the service builder
   */
  public ServiceBuilder type(String type) {
    this.type = type;
    return this;
  }

  /**
   * ServiceEndpoint service builder.
   *
   * @param serviceEndpoint the serviceEndpoint
   * @return the service builder
   */
  public ServiceBuilder serviceEndpoint(URI serviceEndpoint) {
    this.serviceEndpoint = serviceEndpoint;
    return this;
  }

  /**
   * Build service.
   *
   * @return the service instance
   */
  public Service build() {
    return new Service(
        Map.of(
            Service.ID, id,
            Service.TYPE, type,
            Service.SERVICE_ENDPOINT, serviceEndpoint));
  }
}
