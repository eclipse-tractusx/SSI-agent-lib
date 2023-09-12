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
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

public class RemoteDocumentLoader implements DocumentLoader {

  private static DocumentLoader DEFAULT_HTTP_LOADER;
  private static DocumentLoader DEFAULT_FILE_LOADER;
  @Getter private DocumentLoader httpLoader;
  @Getter private DocumentLoader fileLoader;

  @Getter @Setter private boolean enableLocalCache = true;
  @Getter @Setter private boolean enableHttp = false;
  @Getter @Setter private boolean enableHttps = false;
  @Getter @Setter private boolean enableFile = false;
  @Getter @Setter private Map<URI, JsonDocument> localCache = new HashMap<URI, JsonDocument>();
  @Getter @Setter private Cache<URI, Document> remoteCache = null;
  @Getter @Setter private List<URI> httpContexts = new ArrayList<URI>();
  @Getter @Setter private List<URI> httpsContexts = new ArrayList<URI>();
  @Getter @Setter private List<URI> fileContexts = new ArrayList<URI>();

  public static final DocumentLoader DOCUMENT_LOADER;

  static {
    DOCUMENT_LOADER = new RemoteDocumentLoader();
  }

  public static DocumentLoader getDefaultHttpLoader() {
    if (DEFAULT_HTTP_LOADER == null) {
      DEFAULT_HTTP_LOADER = new HttpLoader(DefaultHttpClient.defaultInstance());
    }
    return DEFAULT_HTTP_LOADER;
  }

  public static DocumentLoader getDefaultFileLoader() {
    if (DEFAULT_FILE_LOADER == null) {
      DEFAULT_FILE_LOADER = new FileLoader();
    }
    return DEFAULT_FILE_LOADER;
  }

  public static void setDefaultHttpLoader(DocumentLoader defaultHttpLoader) {
    DEFAULT_HTTP_LOADER = defaultHttpLoader;
  }

  public static void setDefaultFileLoader(DocumentLoader defaultFileLoader) {
    DEFAULT_FILE_LOADER = defaultFileLoader;
  }

  public RemoteDocumentLoader() {}

  public RemoteDocumentLoader(Map<URI, JsonDocument> localCache) {
    if (localCache == null) {
      throw new NullPointerException();
    }
    this.localCache = localCache;
  }

  @Override
  public Document loadDocument(URI url, DocumentLoaderOptions options) throws JsonLdError {

    if (this.isEnableLocalCache() && this.getLocalCache().containsKey(url)) {
      return this.getLocalCache().get(url);
    }
    if (this.isEnableHttp() && "http".equalsIgnoreCase(url.getScheme())) {

      DocumentLoader httpLoader = this.getHttpLoader();
      if (httpLoader == null) {
        httpLoader = getDefaultHttpLoader();
      }
      Document document =
          this.getRemoteCache() == null ? null : this.getRemoteCache().getIfPresent(url);
      if (document == null) {
        document = httpLoader.loadDocument(url, options);
        if (this.getRemoteCache() != null) {
          this.getRemoteCache().put(url, document);
        }
      }
      return document;
    }
    if (this.isEnableHttps() && "https".equalsIgnoreCase(url.getScheme())) {

      DocumentLoader httpLoader = this.getHttpLoader();
      if (httpLoader == null) {
        httpLoader = getDefaultHttpLoader();
      }
      Document document =
          this.getRemoteCache() == null ? null : this.getRemoteCache().getIfPresent(url);
      if (document == null) {
        document = httpLoader.loadDocument(url, options);
        if (this.getRemoteCache() != null) {
          this.getRemoteCache().put(url, document);
        }
      }
      return document;
    }
    if (this.isEnableFile() && "file".equalsIgnoreCase(url.getScheme())) {

      DocumentLoader fileLoader = this.getFileLoader();
      if (fileLoader == null) {
        fileLoader = getDefaultFileLoader();
      }
      Document document =
          this.getRemoteCache() == null ? null : this.getRemoteCache().getIfPresent(url);
      if (document == null) {
        document = fileLoader.loadDocument(url, options);
        if (this.getRemoteCache() != null) {
          this.getRemoteCache().put(url, document);
        }
      }
      return document;
    }

    Logger.getLogger(this.getClass().getName()).warning("Cannot load context: " + url);
    return null;
  }
}
