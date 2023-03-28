# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased - x.x.x]
### Added
- Added validation for UpdateInvestigationRequest, reason for (decline, accepted, close)
- Added Title to sections that might be cut of with three dots (...)

### Changed
- Restructured helm charts to an parent helm chart which includes frontend and backend
- Updated database fields within Investigation table (accept_reason, decline_reason, close_reason)
- Used same shadow for other parts table than we use for my parts table
- Improved visibility of text that might be too large.
- Changed mapping of partNumber from customerPartId to manufacturerPartId
- Adapted catena style guide for colors
- Removed quality type from ui but kept base functionality for future use
- Improved and separated *.md documentation, especially README.md

## [3.0.0] - 2023-03-21

### Added
- Added functionality to update & close notifications – Quality Investigations
- Included a guide for connecting sonarcloud to IntelliJ -> Contribution.md
- Added properties targetDate and severity to the Quality Investigations
- Included reason for accept, decline and closure of a Quality Investigation
- Added additional information to be displayed on the Quality Investigation detail page
- Added native datetime component to be used for Quality Investigation
- Merged the backend repository into this repository within /backend
- Added beta environment

### Changed
- Changed github action docker-release to have maven cache instead of previously used gradle
- Fixed a bug which caused the wrong bpn sender was set
- Added the default param to the irs/jobs api lookupBPNs and set it to true
- Added targetdate to the notification creation.
- Removed duplicated dependencies / Added version numbers for all dependencies for better managing
- Moved the frontend application to /frontend
- Updated to spring boot 3
- Fixed jersey-client cve
- Fixed spring-web cve
- Fixed spring-security-oauth-client cve
- Some minor updates on dependencies
- Restructured pom to use properties for all versions


## [2.0.0] - 2023-03-06

### Added
- Added functionality to close notifications – Quality Investigations

### Changed
- Fixed Sending and saving quality investigation closure reason
- Added connector lookup dataspace discovery service functionality for finding EDC url

## [1.1.0] - 2023-02-06

### Added

- Functionality to send and receive notifications – Quality Investigations
- EDC router component to support notifications and data ingest process in parallel
- Arc42 documentation

### Changed

- Endpoints introducing proper validation for request body depending on the endpoint type

### Removed

- Email Feature from application for security reasons

## [1.0.1] - 2022-12-19

### Added

Using the "relationships" structure instead of "AssemblyPartRelationship" in the IRS response.

## [1.0.0] - 2022-12-12

### Added

- **Traceability BoM asBuilt** - with the use of IRS we retrieve a BoM tree for lifecycle "as built" for serialized parts as well as batches, which as a prerequisite are provided to the Catena-X network.
- Usage of the aspects "SerialPartTypification", “Batch” and "AssemblyPartRelationship".
- Tree built in the downwards direction top-down/parent-child. Visualization of the BoM tree and list view of own manufactured as well as being supplied with parts.

## [0.1.0] - 2022-09-21

### Added

- **BoM asBuilt** Fetch parts and supplier parts, display details, display part tree
- **EDC and IRS integration**

[unreleased]: https://github.com/eclipse-tractusx/traceability-foss-frontend/compare/1.1.0...HEAD
[1.1.0]: https://github.com/eclipse-tractusx/traceability-foss-frontend/compare/1.1.0
[1.0.0]: https://github.com/eclipse-tractusx/traceability-foss-frontend/compare/1.0.0
[0.1.0]: https://github.com/eclipse-tractusx/traceability-foss-frontend/compare/0.1.0
