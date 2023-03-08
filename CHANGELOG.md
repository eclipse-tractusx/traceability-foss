# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- Added functionality to update notifications – Quality Investigations
- Included a guide for connecting sonarcloud to IntelliJ -> Contribution.md

### Changed
- Changed github action docker-release to have maven cache instead of previously used gradle
- Added the default param to the irs/jobs api lookupBPNs and set it to true

## [2.0.0] - 2023-03-06

### Added
- Added functionality to close notifications – Quality Investigations

### Changed
- Fixed Sending and saving quality investigation closure reason

## [1.1.0] - 2023-03-02

### Added

- Added functionality to send and receive notifications – Quality Investigations
- Added EDC router component to support notifications and data ingest process in parallel
- Added arc42 documentation

### Changed

- Changed endpoints introducing proper validation for request body depending on the endpoint type

### Removed

- Removed email feature from application for security reasons

## [1.0.1] - 2022-12-19

### Added

Using the "relationships" structure instead of "AssemblyPartRelationship" in the IRS response.

## [1.0.0] - 2022-12-09

### Added

- **Traceability BoM asBuilt** - with the use of IRS we retrieve a BoM tree for lifecycle "as built" for serialized parts as well as batches, which as a prerequisite are provided to the Catena-X network.
- Usage of the aspects "SerialPartTypization", “Batch” and "AssemblyPartRelationship".
- Tree built in the downwards direction top-down/parent-child. Visualization of the BoM tree and list view of own manufactured as well as being supplied with parts.

## [0.1.0] - 2022-09-21

### Added

- **BoM asBuilt** Fetch parts and supplier parts, display details, display part tree
- **EDC and IRS integration**

[Unreleased]: https://github.com/eclipse-tractusx/traceability-foss-backend/compare//0.1.0...HEAD

[1.0.1]: https://github.com/eclipse-tractusx/traceability-foss-backend/compare/1.0.1

[1.0.0]: https://github.com/eclipse-tractusx/traceability-foss-backend/compare/1.0.0

[0.1.0]: https://github.com/eclipse-tractusx/traceability-foss-backend/compare/0.1.0
