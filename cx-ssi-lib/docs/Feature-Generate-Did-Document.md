# Feature: Generate DID Document

## 1. Specification

Given a valid DID, generate DID document as specified in [W3C-DID-Core](https://www.w3.org/TR/did-core/).

*Example:*
```json
{
  "@context": [
    "https://www.w3.org/ns/did/v1",
    "https://w3id.org/security/suites/jws-2020/v1",
    "https://w3id.org/security/suites/ed25519-2020/v1"
  ]
  "id": "did:web:mydomain.com:12345",
  "verificationMethod": [{
    "id": "did:web:mydomain.com:12345#_Qq0UL2Fq651Q0Fjd6TvnYE-faHiOpRlPVQcY_-tA4A",
    "type": "JsonWebKey2020", 
    "controller": "did:web:mydomain.com:12345",
    "publicKeyJwk": {
      "crv": "Ed25519", 
      "x": "VCpo2LMLhn6iWku8MKvSLg2ZAoC-nlOyPVQaO3FxVeQ", 
      "kty": "OKP", 
      "kid": "_Qq0UL2Fq651Q0Fjd6TvnYE-faHiOpRlPVQcY_-tA4A" 
    }
  }, {
    "id": "did:example:123456789abcdefghi#keys-1",
    "type": "Ed25519VerificationKey2020", 
    "controller": "did:example:pqrstuvwxyz0987654321",
    "publicKeyMultibase": "zH3C2AVvLMv6gmMNam3uVAjZpfkcJCwDwnZn6z3wXmqPV"
  }],
}
```

#### 1.1 Assumptions
Multiple verification methods *SHOULD* be supported.

#### 1.2 Constraints
Currently only verification type **Ed25519VerificationKey2020** needs to be supported.

## 2. Architecture

#### 2.1 Class Diagrams

![CreateDidClass.png](images/CreateDidClass.png)

#### 2.2 Sequence Diagrams

![CreateDidSequence.png](images/CreateDidSequence.png)

*You can find an Example of the class interactions here:* /src/main/java/org/eclipse/tractusx/ssi/examples/BuildDIDDoc.java

