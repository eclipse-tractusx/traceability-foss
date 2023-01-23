# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

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
