package org.eclipse.tractusx.ssi.lib.proof.transform;

import foundation.identity.jsonld.JsonLDException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import org.eclipse.tractusx.ssi.lib.model.verifiable.credential.VerifiableCredential;
import org.eclipse.tractusx.ssi.lib.serialization.jsonLd.DanubTechMapper;

public class LinkedDataTransformer {
  public TransformedLinkedData transform(VerifiableCredential credential) {
    // make a copy and remove proof from credential, as it is not part of the linked data
    VerifiableCredential copyCredential = new VerifiableCredential(credential);
    copyCredential.remove(VerifiableCredential.PROOF);

    var dtCredential = DanubTechMapper.map(copyCredential);
    try {

      var normalized = dtCredential.normalize("urdna2015");
      return new TransformedLinkedData(normalized);

    } catch (JsonLDException e) {
      throw new RuntimeException(e); // TODO
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e); // TODO
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO
    }
  }
}
