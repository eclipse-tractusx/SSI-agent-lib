package org.eclipse.tractusx.ssi.lib.model;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;

import com.apicatalog.jsonld.JsonLdError;
import com.apicatalog.jsonld.JsonLdErrorCode;
import com.apicatalog.jsonld.http.DefaultHttpClient;
import com.apicatalog.jsonld.loader.DocumentLoaderOptions;
import com.apicatalog.jsonld.loader.HttpLoader;
import com.github.tomakehurst.wiremock.common.ssl.KeyStoreSettings;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.file.Path;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.stream.Stream;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;

class RemoteDocumentLoaderTest {
  private static final String CA_PATH;
  private static final String KEYSTORE_PATH;
  private static final String TRUST_STORE_PATH;
  public static final String KEY_STORE_PASSWORD = "changeit";

  private static final HttpClient trustallClient;

  private static final TrustManager[] trustAllCerts =
      new TrustManager[] {
        new X509TrustManager() {
          public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
          }

          public void checkClientTrusted(
              java.security.cert.X509Certificate[] certs, String authType) {}

          public void checkServerTrusted(
              java.security.cert.X509Certificate[] certs, String authType) {}
        }
      };

  static {
    Path trustStorePath = Path.of("src/test/resources/wiremock/wiremock-truststore.jks");
    Path keystore = Path.of("src/test/resources/wiremock/wiremock-keystore.p12");
    System.setProperty("javax.net.ssl.trustStore", trustStorePath.toAbsolutePath().toString());
    Path caPath = Path.of("src/test/resources/wiremock/ca.p12");

    CA_PATH = caPath.toAbsolutePath().toString();
    KEYSTORE_PATH = keystore.toAbsolutePath().toString();
    TRUST_STORE_PATH = trustStorePath.toAbsolutePath().toString();

    try {
      SSLContext sslContext = SSLContext.getInstance("TLS");
      sslContext.init(null, trustAllCerts, new SecureRandom());

      trustallClient = HttpClient.newBuilder().sslContext(sslContext).build();
    } catch (NoSuchAlgorithmException | KeyManagementException e) {
      throw new RuntimeException(e);
    }
  }

  @RegisterExtension
  static WireMockExtension wm1 =
      WireMockExtension.newInstance()
          .options(
              wireMockConfig()
                  .dynamicPort()
                  .dynamicHttpsPort()
                  .keystoreType("pkcs12")
                  .keystorePassword(KEY_STORE_PASSWORD)
                  .keystorePath(KEYSTORE_PATH)
                  .keyManagerPassword(KEY_STORE_PASSWORD)
                  .caKeystorePassword(KEY_STORE_PASSWORD)
                  .caKeystorePath(CA_PATH)
                  .caKeystoreSettings(new KeyStoreSettings(CA_PATH, KEY_STORE_PASSWORD, "pkcs12")))
          .build();

  @BeforeAll
  public static void beforeAll() {
    System.setProperty("javax.net.ssl.trustStore", TRUST_STORE_PATH);
    RemoteDocumentLoader.setDefaultHttpLoader(
        new HttpLoader(new DefaultHttpClient(trustallClient)));
  }

  @Test
  void shouldInitializeAndReturnDocumentLoader() {
    assertNotNull(RemoteDocumentLoader.getDefaultFileLoader());
  }

  @Test
  void shouldReturnDocumentLoader() {
    assertNotNull(RemoteDocumentLoader.getDefaultFileLoader());
  }

  @ParameterizedTest
  @MethodSource("testConfigs")
  @SneakyThrows
  void test(TestConfig config) {
    RemoteDocumentLoader remoteDocumentLoader = RemoteDocumentLoader.DOCUMENT_LOADER;
    remoteDocumentLoader.setEnableHttps(config.httpsEnabled);
    remoteDocumentLoader.setEnableHttp(!config.httpsEnabled);
    remoteDocumentLoader.setEnableLocalCache(config.enableLocalCache);

    wm1.stubFor(get("/test").willReturn(ok("{}").withHeader("Content-Type", "application/json")));
    assertDoesNotThrow(() -> remoteDocumentLoader.loadDocument(config.uri, config.options));
  }

  @Test
  @SneakyThrows
  void shouldThrowWhenHttpLoaderThrowsJsonLdError() {
    HttpLoader httpLoader = new HttpLoader(new DefaultHttpClient(trustallClient));
    HttpLoader spy = Mockito.spy(httpLoader);
    doThrow(new JsonLdError(JsonLdErrorCode.INVALID_ANNOTATION))
        .when(spy)
        .loadDocument(any(URI.class), any(DocumentLoaderOptions.class));
    RemoteDocumentLoader.setDefaultHttpLoader(httpLoader);
    RemoteDocumentLoader remoteDocumentLoader = RemoteDocumentLoader.DOCUMENT_LOADER;
    remoteDocumentLoader.setEnableHttps(false);
    remoteDocumentLoader.setEnableHttp(true);
    remoteDocumentLoader.setEnableLocalCache(false);

    assertThrows(
        JsonLdError.class,
        () ->
            remoteDocumentLoader.loadDocument(
                URI.create(String.format("http://localhost:%d/test", wm1.getPort())),
                new DocumentLoaderOptions()));
  }

  @SneakyThrows
  private static Stream<Arguments> testConfigs() {

    DocumentLoaderOptions options = new DocumentLoaderOptions();

    return Stream.of(
        Arguments.of(
            new TestConfig(
                true,
                true,
                URI.create(String.format("https://localhost:%d/test", wm1.getHttpsPort())),
                options)),
        Arguments.of(
            new TestConfig(
                true,
                true,
                URI.create(String.format("https://localhost:%d/test", wm1.getHttpsPort())),
                options)),
        Arguments.of(
            new TestConfig(
                true,
                false,
                URI.create(String.format("https://localhost:%d/test", wm1.getHttpsPort())),
                options)),
        Arguments.of(
            new TestConfig(
                false,
                true,
                URI.create(String.format("http://localhost:%d/test", wm1.getPort())),
                options)),
        Arguments.of(
            new TestConfig(
                false,
                false,
                URI.create(String.format("http://localhost:%d/test", wm1.getPort())),
                options)));
  }

  @RequiredArgsConstructor
  private static class TestConfig {
    final boolean httpsEnabled;

    final boolean enableLocalCache;

    final URI uri;

    final DocumentLoaderOptions options;
  }
}
