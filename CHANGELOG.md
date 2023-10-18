# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [UNRELEASED]

### Added

- JSON-LD validation is now performed durin linked data validation.
- Caching for remote resources has been implemented, with fixed duration of 1 day.

### Changed

- The class `DocumentLoader` is renamed to `RemoteDocumentLoader` and has become a singleton.

### Removed

- Removed `credentialStatus` from `VerifiableCredential` as it is needed for revocation which is not
  implemented yet.

### Fixed

- The misspelled method name `verifiy` in `LinkedDataProofValidation` was fixed.
- The misspelled method name `verifiy` in `Verification` was fixed.

## [0.0.16] - 2023-10-10

### Added

- Migrated the repository catenax-ng/product-lab-ssi at tag-level 0.0.16, hence the odd
  versioning. [Old repository](https://github.com/catenax-ng/product-lab-ssi)
