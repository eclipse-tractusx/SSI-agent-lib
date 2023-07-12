package org.eclipse.tractusx.ssi.lib.crypt.x21559;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.spec.PKCS8EncodedKeySpec;
import lombok.NonNull;
import org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.eclipse.tractusx.ssi.lib.crypt.IPrivateKey;
import org.eclipse.tractusx.ssi.lib.exception.InvalidePrivateKeyFormat;
import org.eclipse.tractusx.ssi.lib.model.base.EncodeType;
import org.eclipse.tractusx.ssi.lib.model.base.MultibaseFactory;

public class x21559PrivateKey implements IPrivateKey {

  private final @NonNull byte[] key;

  public x21559PrivateKey(byte[] privateKey) throws InvalidePrivateKeyFormat {
    if (this.getKeyLength() != privateKey.length)
      throw new InvalidePrivateKeyFormat(getKeyLength(), privateKey.length);
    this.key = privateKey;
  }

  public x21559PrivateKey(String privateKey, boolean PEMFormat)
      throws InvalidePrivateKeyFormat, IOException {
    if (PEMFormat) {
      StringReader sr = new StringReader(privateKey);
      PemReader reader = new PemReader(sr);
      PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(reader.readPemObject().getContent());
      Ed25519PrivateKeyParameters ed25519PrivateKeyParameters =
          new Ed25519PrivateKeyParameters(keySpec.getEncoded());
      this.key = ed25519PrivateKeyParameters.getEncoded();

    } else {
      this.key = MultibaseFactory.create(privateKey).getDecoded();
    }

    if (this.getKeyLength() != key.length)
      throw new InvalidePrivateKeyFormat(getKeyLength(), privateKey.length());
  }

  @Override
  public String asStringForStoring() throws IOException {

    PemObject pemObject = new PemObject("ED21559 Private Key", this.key);
    StringWriter sw = new StringWriter();
    PemWriter writer = new PemWriter(sw);
    writer.writeObject(pemObject);
    writer.close();
    return sw.toString();
  }

  @Override
  public String asStringForExchange(EncodeType encodeType) throws IOException {

    return MultibaseFactory.create(encodeType, key).getEncoded();
  }

  @Override
  public byte[] asByte() {
    return this.key;
  }

  @Override
  public int getKeyLength() {
    return 32;
  }
}
