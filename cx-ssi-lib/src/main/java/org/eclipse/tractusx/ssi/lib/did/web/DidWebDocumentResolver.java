package org.eclipse.tractusx.ssi.lib.did.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.eclipse.tractusx.ssi.lib.did.web.util.Constants;
import org.eclipse.tractusx.ssi.lib.did.web.util.DidWebParser;
import org.eclipse.tractusx.ssi.lib.exception.DidWebException;
import org.eclipse.tractusx.ssi.lib.exception.SsiException;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.eclipse.tractusx.ssi.lib.model.did.DidDocument;
import org.eclipse.tractusx.ssi.lib.model.did.DidMethod;
import org.eclipse.tractusx.ssi.lib.resolver.DidDocumentResolver;

@RequiredArgsConstructor
public class DidWebDocumentResolver implements DidDocumentResolver {

  private final HttpClient client;
  private final DidWebParser parser;
  private final boolean enforceHttps;

  @Override
  public DidMethod getSupportedMethod() {
    return Constants.DID_WEB_METHOD;
  }

  @Override
  public DidDocument resolve(Did did) {
    if (!did.getMethod().equals(Constants.DID_WEB_METHOD))
      throw new SsiException(
          "Handler can only handle the following methods:" + Constants.DID_WEB_METHOD);

    final URI uri = parser.parse(did, enforceHttps);

    final HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

    try {
      final HttpResponse<String> response =
          client.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() < 200 || response.statusCode() > 299) {
        throw new DidWebException(
            String.format(
                "Unexpected response when resolving did document [Code=%s, Payload=%s]",
                response.statusCode(), response.body()));
      }
      if (response.body() == null) {
        throw new DidWebException("Empty response body");
      }

      final byte[] body = response.body().getBytes(StandardCharsets.UTF_8);

      // TODO Fix this
      final ObjectMapper mapper = new ObjectMapper();
      final Map<String, Object> json = mapper.readValue(body, Map.class);

      return new DidDocument(json);
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
