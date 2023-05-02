package org.eclipse.tractusx.ssi.lab.system.test.client.connector;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.io.entity.StringEntity;

@Slf4j
public abstract class ApacheClient {

  private final CloseableHttpClient httpClient;
  private final Map<String, String> defaultHeaders;
  private final String baseUrl;

  public ApacheClient(String baseUrl) {
    this.baseUrl = baseUrl;
    this.httpClient = HttpClientBuilder.create().build();
    this.defaultHeaders = new HashMap<>();
  }

  protected void setDefaultHeader(String key, String value) {
    defaultHeaders.put(key, value);
  }

  protected <T> T get(String path, String params, TypeToken<?> typeToken) throws IOException {
    return get(path + "?" + params, typeToken);
  }

  protected <T> T get(String path, TypeToken<?> typeToken) throws IOException {

    final HttpGet get = new HttpGet(baseUrl + path);
    final CloseableHttpResponse response = sendRequest(get);
    final byte[] json = response.getEntity().getContent().readAllBytes();

    log.debug("Received response: {}", new String(json, StandardCharsets.UTF_8));
    return new Gson().fromJson(new String(json, StandardCharsets.UTF_8), typeToken.getType());
  }

  protected void post(String path, Object object) throws IOException {
    post(path, object, new TypeToken<Void>() {});
  }

  protected <T> T post(String path, Object object, TypeToken<?> typeToken) throws IOException {
    final String url = String.format("%s%s", baseUrl, path);
    final HttpPost post = new HttpPost(url);
    post.addHeader("Content-Type", "application/json");

    var json = new Gson().toJson(object);

    log.debug("POST Payload: " + json);

    post.setEntity(new StringEntity(json));
    final CloseableHttpResponse response = sendRequest(post);

    T responseJson = null;
    if (!typeToken.equals(new TypeToken<Void>() {})) {
      final byte[] responseBytes = response.getEntity().getContent().readAllBytes();
      responseJson =
          new Gson()
              .fromJson(new String(responseBytes, StandardCharsets.UTF_8), typeToken.getType());
    }

    response.close();

    return responseJson;
  }

  @SneakyThrows(URISyntaxException.class)
  protected CloseableHttpResponse sendRequest(ClassicHttpRequest request) throws IOException {

    defaultHeaders.forEach(request::addHeader);

    log.debug(String.format("Send %-6s %s", request.getMethod(), request.getUri().toString()));

    final CloseableHttpResponse response = httpClient.execute(request);

    if (200 > response.getCode() || response.getCode() >= 300) {
      throw new RuntimeException(String.format("Unexpected response: %s", response));
    }

    return response;
  }
}
