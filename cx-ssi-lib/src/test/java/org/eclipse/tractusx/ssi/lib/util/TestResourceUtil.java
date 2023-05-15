package org.eclipse.tractusx.ssi.lib.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.SneakyThrows;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.tractusx.ssi.lib.model.did.Ed25519VerificationKey2020;
import org.eclipse.tractusx.ssi.lib.util.identity.KeyResourceLoader;

public class TestResourceUtil {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final String DID_DOCUMENT_ED25519 = "/did-document/document.ed25519.json";
  private static final String PUBLIC_KEY_ED25519 = "keys/ed25519/public.pem";
  private static final String PRIVATE_KEY_ED25519 = "keys/ed25519/private.pem";
  private static String VERIFIABLE_CREDENTIAL_ALUMNI =
      "verifiable-credential/alumni-credential.json";

  public static List<Map<String, Object>> getAllDidDocuments() {
    return Arrays.asList(readJsonResource(DID_DOCUMENT_ED25519));
  }

  public static Map<String, Object> getAlumniVerifiableCredential() {
    return readJsonResource(VERIFIABLE_CREDENTIAL_ALUMNI);
  }

  public static Map<String, Object> getDidDocument(String verificationKeyType) {
    if (Ed25519VerificationKey2020.DEFAULT_TYPE.equals(verificationKeyType)) {
      return readJsonResource(DID_DOCUMENT_ED25519);
    }

    throw new IllegalArgumentException("Unsupported verification key type: " + verificationKeyType);
  }

  public static byte[] getPublicKeyEd25519() {
    return readPemResource(PUBLIC_KEY_ED25519);
  }

  public static byte[] getPrivateKeyEd25519() {
    return readPemResource(PRIVATE_KEY_ED25519);
  }

  @SneakyThrows
  private static Map<String, Object> readJsonResource(String resource) {
    try (final InputStream inputStream = readResource(resource)) {
      return MAPPER.readValue(inputStream, Map.class);
    }
  }

  @SneakyThrows
  private static byte[] readPemResource(String resource) {
    try (final InputStream inputStream = readResource(resource)) {
      final PemReader reader = new PemReader(new InputStreamReader(inputStream));
      return reader.readPemObject().getContent();
    }
  }

  private static InputStream readResource(String resource) {
    final InputStream inputStream =
        KeyResourceLoader.class.getClassLoader().getResourceAsStream(resource);

    return Objects.requireNonNull(inputStream, "Resource not found: " + resource);
  }
}
