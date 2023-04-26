package org.eclipse.tractusx.ssi.lab.system.test.client.connector;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;
import lombok.SneakyThrows;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;

public class SsiApi extends ApacheClient {

  public SsiApi(String apiUrl) {
    super(apiUrl);
  }

  @SneakyThrows(IOException.class)
  public void storeCredential(VerifiableCredential credential) {
    post("/verifiable-credential", credential);
  }

  @SneakyThrows(IOException.class)
  public List<VerifiableCredential> getAllCredentials() {
    return get("/verifiable-credential", new TypeToken<List<VerifiableCredential>>() {});
  }
}
