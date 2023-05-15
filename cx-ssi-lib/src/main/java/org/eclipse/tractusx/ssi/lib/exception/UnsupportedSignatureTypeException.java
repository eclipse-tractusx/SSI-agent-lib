package org.eclipse.tractusx.ssi.lib.exception;

public class UnsupportedSignatureTypeException extends Exception {
  public UnsupportedSignatureTypeException(String signatureType) {
    super("Unsupported signature type: " + signatureType);
  }
}
