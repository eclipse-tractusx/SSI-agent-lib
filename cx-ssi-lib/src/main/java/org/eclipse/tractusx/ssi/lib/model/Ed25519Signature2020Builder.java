package org.eclipse.tractusx.ssi.lib.model;

import java.net.URI;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class Ed25519Signature2020Builder {

  private String proofPurpose;
  private String proofValue;
  private URI verificationMethod;
  private Instant created;

  public Ed25519Signature2020Builder proofPurpose(String proofPurpose) {
    this.proofPurpose = proofPurpose;
    return this;
  }

  public Ed25519Signature2020Builder proofValue(String proofValue) {
    this.proofValue = proofValue;
    return this;
  }

  public Ed25519Signature2020Builder verificationMethod(URI verificationMethod) {
    this.verificationMethod = verificationMethod;
    return this;
  }

  public Ed25519Signature2020Builder created(Instant created) {
    this.created = created;
    return this;
  }

  public Ed25519Signature2020 build() {
    DateTimeFormatter formatter =
        DateTimeFormatter.ofPattern(Ed25519Signature2020.TIME_FORMAT).withZone(ZoneOffset.UTC);

    Map<String, Object> map =
        Map.of(
            Ed25519Signature2020.TYPE,
            Ed25519Signature2020.ED25519_VERIFICATION_KEY_2018,
            Ed25519Signature2020.PROOF_PURPOSE,
            proofPurpose,
            Ed25519Signature2020.PROOF_VALUE,
            proofValue,
            Ed25519Signature2020.VERIFICATION_METHOD,
            verificationMethod.toString(),
            Ed25519Signature2020.CREATED,
            formatter.format(created));

    return new Ed25519Signature2020(map);
  }
}
