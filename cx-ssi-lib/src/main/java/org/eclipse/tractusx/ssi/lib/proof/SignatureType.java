package org.eclipse.tractusx.ssi.lib.proof;

public enum SignatureType {
    ED21559 {
        public String toString() {
            return "Ed25519Signature2020";
        }
        
    },
    JWS {
        public String toString() {
            return "JsonWebSignature2020";
        }

        
    }
}
