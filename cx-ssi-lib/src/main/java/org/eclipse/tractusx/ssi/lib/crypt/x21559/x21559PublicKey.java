package org.eclipse.tractusx.ssi.lib.crypt.x21559;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.spec.X509EncodedKeySpec;
import lombok.NonNull;
import org.bouncycastle.crypto.params.Ed25519PublicKeyParameters;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.eclipse.tractusx.ssi.lib.crypt.IPublicKey;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePublicKeyFormat;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;

public class x21559PublicKey implements IPublicKey {

  private final @NonNull byte[] originalKey;

  public x21559PublicKey(byte[] publicKey) throws InvalidePublicKeyFormat {
    if (this.getKeyLength() != publicKey.length)
      throw new InvalidePublicKeyFormat(getKeyLength(), publicKey.length);
    this.originalKey = publicKey;
  }

  public x21559PublicKey(String publicKey, boolean PEMformat)
      throws InvalidePublicKeyFormat, IOException {

    if (PEMformat) {
      StringReader sr = new StringReader(publicKey);
      PemReader reader = new PemReader(sr);
      PemObject pemObject = reader.readPemObject();
      X509EncodedKeySpec encodedKeySpec = new X509EncodedKeySpec(pemObject.getContent());
      Ed25519PublicKeyParameters ed25519PublicKeyParameters =
          new Ed25519PublicKeyParameters(encodedKeySpec.getEncoded());
      this.originalKey = ed25519PublicKeyParameters.getEncoded();
    } else {
      this.originalKey = MultibaseFactory.create(publicKey).getDecoded();
    }

    if (this.getKeyLength() != originalKey.length)
      throw new InvalidePublicKeyFormat(getKeyLength(), originalKey.length);
  }

  @Override
  public String asStringForStoring() throws IOException {
    PemObject pemObject = new PemObject("ED21559 Public Key", this.originalKey);
    StringWriter sw = new StringWriter();
    PemWriter writer = new PemWriter(sw);
    writer.writeObject(pemObject);
    writer.close();
    return sw.toString();
  }

  @Override
  public String asStringForExchange(EncodeType encodeType) throws IOException {
    return MultibaseFactory.create(encodeType, originalKey).getEncoded();
  }

  @Override
  public byte[] asByte() {
    return this.originalKey;
  }

  @Override
  public int getKeyLength() {
    return 32;
  }
}
