# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [UNRELEASED]

## [0.0.19] - 2024-04-24

### BREAKING CHANGES

- New exception system

### Added

- Implemented credential service
- VC in JWT format

### Fixed

- Fix did constructor
- Fix VP token compliance
- Fix VC missing expirationDate
- Fix remove duplicate json validator
- Fix 'titanium-jsonld' dependency
- Fix integratin tests not running
- Fix missing credential status
- Fix code quality

## [0.0.18] - 2023-12-5

### Added

- A `NOTICE.md` file to be processed inside of the `docs/` folder, containing the necessary legal
  headers.
- A `skipReleaseChecks` section to the tractus-x metadata file, as the Dockerfile in the `docs/`
  directory is not being published.

### Fixed

- The Docker image tag inside of the `docs/` directory use the `tractus-x` prefix now.

## [0.0.18-SNAPSHOT] 2023-12-4

### Added

- Extra layer of validation in `org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofValidation`
  for `verify`.

### Fixed

- Server id for the SNAPSHOT repository.

### Security

- Updated project dependencies to resolve reported vulnerabilities.

## [0.0.17] - 2023-11-29

### BREAKING CHANGES

- The misspelled method name `verifiy`
  in `org.eclipse.tractusx.ssi.lib.proof.LinkedDataProofValidation` was renamed to `verify`.

### Added

- Maven Publish Workflow using a manual trigger
- JSON-LD validation is now performed during linked data validation.
- Caching for remote resources has been implemented, with fixed duration of 1 day.
- Add INSTALL.md instructions including Maven and Gradle setup.

### Changed

- The class `DocumentLoader` is renamed to `RemoteDocumentLoader` and has become a singleton.

### Removed

- Removed `credentialStatus` from `VerifiableCredential` as it is needed for revocation which is not
  implemented yet.

## [0.0.16] - 2023-10-10

### Added

- Migrated the repository catenax-ng/product-lab-ssi at tag-level 0.0.16, hence the odd
  versioning. [Old repository](https://github.com/catenax-ng/product-lab-ssi)
