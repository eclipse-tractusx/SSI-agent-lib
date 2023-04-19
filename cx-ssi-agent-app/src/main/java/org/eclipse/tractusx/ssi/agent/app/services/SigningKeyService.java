package org.eclipse.tractusx.ssi.agent.app.services;

import java.io.FileReader;
import java.io.IOException;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.io.pem.PemReader;
import org.eclipse.tractusx.ssi.agent.app.map.VerifiableCredentialMapper;
import org.eclipse.tractusx.ssi.agent.lib.Ed25519Signature2020Issuer;
import org.eclipse.tractusx.ssi.agent.model.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.crypt.ed25519.Ed25519KeySet;
import org.eclipse.tractusx.ssi.lib.model.did.Did;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SigningKeyService {

  @Value("${public.key}")
  private String publicKey;

  private byte[] publicKeyBytes;

  @Value("${private.key}")
  private String privateKey;

  private byte[] privateKeyBytes;

  public Ed25519KeySet getSigningKeySet() {
    return new Ed25519KeySet(privateKeyBytes, publicKeyBytes);
  }

  public VerifiableCredential signVerifiableCredential(
      VerifiableCredential verifiableCredential, Did issuerDid) {
    final Ed25519Signature2020Issuer issuer = new Ed25519Signature2020Issuer();

    return VerifiableCredentialMapper.map(
        issuer.createEd25519Signature(
            VerifiableCredentialMapper.map(verifiableCredential),
            getSigningKeySet().getPrivateKey(),
            issuerDid));
  }

  @PostConstruct
  private void initializeKeys() throws IOException {

    log.trace("Reading private signing key");
    privateKeyBytes = readPEMFile(privateKey);

    log.trace("Reading public signing key");
    publicKeyBytes = readPEMFile(publicKey);
  }

  private byte[] readPEMFile(String path) throws IOException {
    PemReader pemReader = new PemReader(new FileReader(path));
    return pemReader.readPemObject().getContent();
  }
}
