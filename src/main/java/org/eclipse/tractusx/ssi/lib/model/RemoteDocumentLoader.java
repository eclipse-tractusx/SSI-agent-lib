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

package org.eclipse.tractusx.ssi.lib.model;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;
import com.apicatalog.jsonld.http.DefaultHttpClient;
import com.apicatalog.jsonld.loader.DocumentLoader;
import com.apicatalog.jsonld.loader.DocumentLoaderOptions;
import com.apicatalog.jsonld.loader.FileLoader;
import com.apicatalog.jsonld.loader.HttpLoader;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/** The type Remote document loader. */
public class RemoteDocumentLoader implements DocumentLoader {

  private static final int CACHE_PERIOD_IN_DAYS = 1;

  public static final String CANNOT_LOAD_CONTEXT = "Cannot load context: %s";

  /**
   * -- SETTER -- Sets default http loader.
   *
   * @param loader the default http loader
   */
  @Setter private static DocumentLoader defaultHttpLoader;

  /**
   * -- SETTER -- Sets default file loader.
   *
   * @param loader the default file loader
   */
  @Setter private static DocumentLoader defaultFileLoader;

  @Getter private static DocumentLoader httpLoader;

  @Getter private static DocumentLoader fileLoader;

  @Getter @Setter private boolean enableLocalCache = true;

  @Getter @Setter private boolean enableHttp = false;

  @Getter @Setter private boolean enableHttps = false;

  @Getter @Setter private boolean enableFile = false;

  @Getter @Setter private Map<URI, JsonDocument> localCache = new HashMap<>();

  @Getter @Setter
  private Cache<URI, Document> remoteCache =
      Caffeine.newBuilder().expireAfterWrite(Duration.ofDays(CACHE_PERIOD_IN_DAYS)).build();

  @Getter @Setter private List<URI> httpContexts = new ArrayList<>();

  @Getter @Setter private List<URI> httpsContexts = new ArrayList<>();

  @Getter @Setter private List<URI> fileContexts = new ArrayList<>();

  /** The constant DOCUMENT_LOADER. */
  public static final RemoteDocumentLoader DOCUMENT_LOADER = new RemoteDocumentLoader();

  /**
   * Gets default http loader.
   *
   * @return the default http loader
   */
  public static DocumentLoader getDefaultHttpLoader() {
    if (defaultHttpLoader == null) {
      defaultHttpLoader = new HttpLoader(DefaultHttpClient.defaultInstance());
    }
    return defaultHttpLoader;
  }

  /**
   * Gets default file loader.
   *
   * @return the default file loader
   */
  public static DocumentLoader getDefaultFileLoader() {
    if (defaultFileLoader == null) {
      defaultFileLoader = new FileLoader();
    }
    return defaultFileLoader;
  }

  RemoteDocumentLoader() {}

  /**
   * Gets instance.
   *
   * @return the instance
   */
  public static synchronized RemoteDocumentLoader getInstance() {
    return DOCUMENT_LOADER;
  }

  private DocumentLoader getLoader(URI url) {
    DocumentLoader loader = null;
    if ((this.isEnableHttp() || this.isEnableHttps())
        && ("http".equalsIgnoreCase(url.getScheme())
            || "https".equalsIgnoreCase(url.getScheme()))) {
      loader = getHttpLoader();
      if (loader == null) {
        loader = getDefaultHttpLoader();
      }
    } else if (this.isEnableFile() && "file".equalsIgnoreCase(url.getScheme())) {
      loader = getFileLoader();
      if (loader == null) {
        loader = getDefaultFileLoader();
      }
    }

    return loader;
  }

  @Override
  public Document loadDocument(URI url, DocumentLoaderOptions options) throws JsonLdError {

    if (url.getScheme().equalsIgnoreCase("http") && !isEnableHttp()) {
      throw new IllegalArgumentException(
          String.format("http is disabled, and %s scheme is http", url));
    }

    if (this.isEnableLocalCache() && this.getLocalCache().containsKey(url)) {
      return this.getLocalCache().get(url);
    }

    DocumentLoader documentLoader = getLoader(url);
    Document document =
        this.getRemoteCache() == null ? null : this.getRemoteCache().getIfPresent(url);
    if (document == null) {
      try {
        document = documentLoader.loadDocument(url, options);
      } catch (JsonLdError e) {
        Logger.getLogger(this.getClass().getName())
            .log(Level.SEVERE, String.format(CANNOT_LOAD_CONTEXT, url));
        throw e;
      }
      if (this.getRemoteCache() != null) {
        this.getRemoteCache().put(url, document);
      }
    }
    return document;
  }
}
