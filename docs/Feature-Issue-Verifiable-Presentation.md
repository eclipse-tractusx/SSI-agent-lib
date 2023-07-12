# Feature: Issue Verifiable Presentation

## 1. Specification

Given a verifiable credential, an issuer DID and an audience, return a verifiable presentation as a JWT.

*Example: JWT payload*
```json
{
  "iss": "did:web:localhost%3A8080",
  "sub": "did:web:localhost%3A8080",
  "aud": "test",
  "vp": {
    "id": "did:web:localhost%3A8080#fd10a61d-3726-45e7-8355-db5f3a4dbe60",
    "type": [
      "VerifiablePresentation"
    ],
    "@context": [
      "https://www.w3.org/2018/credentials/v1"
    ],
    "verifiableCredential": {
      "@context": [
        "https://www.w3.org/2018/credentials/v1"
      ],
      "type": [
        "VerifiableCredential"
      ],
      "id": "https://localhost:8080/12345",
      "issuer": "did:web:localhost%3A8080",
      "issuanceDate": "2023-05-26T13:58:00Z",
      "expirationDate": "2000-01-23T04:56:07Z",
      "credentialSubject": {
        "name": "Jane Doe",
        "id": "did:example:abcdef1234567"
      },
      "proof": {
        "proofPurpose": "proofPurpose",
        "type": "Ed25519Signature2020",
        "proofValue": "zLLs4YXK4dhsaifJGmeyp23TsyUnGxJkobsT8fDgzXdq27dKFSgbXwvb857VyXRtBSLv2wBQbargrHJos93DreKT",
        "verificationMethod": "did:web:localhost%3A8080#key-1",
        "created": "2023-05-26T13:58:00Z"
      }
    }
  },
  "exp": 1686311279,
  "jti": "89c16630-69ca-4b18-baa7-e93d3d3a016c"
}
```

#### 1.1 Assumptions
Multiple signature algorithms *SHOULD* be supported.

#### 1.2 Constraints
Currently only verification type **Ed25519Signature2020** needs to be supported.

## 2. Architecture

#### 2.1 Class Diagrams
*Provide here any class diagrams needed to illustrate the application. These can be ordered by which component they construct or contribute to. If there is any ambiguity in the diagram or if any piece needs more description provide it here as well in a subsection.*

#### 2.2 Sequence Diagrams
*Provide here any sequence diagrams. If possible list the use case they contribute to or solve. Provide descriptions if possible.*
