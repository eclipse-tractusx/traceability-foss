# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

_**For better traceability add the corresponding JIRA issue number in each changelog entry, please.**_
## [Unreleased - DD.MM.YYYY]

## [13.0.2-cfx-5 - 06.11.2024]

### Added
- #350 Added properties for provisioning to submodel / dtr service
- #349 Added second identity provider for Oauth2 for DTR and submodel service
- #62 - Added configurable assert refresh cron job with default values
- #99 added configurable publish asset job
- #66 Added api key authorization for /qualitynotifications and /qualityalerts
- #346 Reconfigured GitHub workflows to use GitHub Packages to fetch dependencies
- #373 Added test data for CFX/BMW for Preprod

### Changed
- #105 Upgraded irs client library to 2.1.15
- #66 changed integration tests for edc notifications
- #66 changed error handling config and added new exception InvalidApiKeyException

### Fixed
- #55 ids path for notification catalog requests is only appended if not already present.

### Fixed

- #375 fixed auto dispatch workflow for auto deployment from main

## [13.0.2-cfx-4]

### Changed

- #50 Made the discovery type configurable, with a default value of bpnl in (FeignDiscoveryRepositoryImpl).
- #55 Added distinct environment variable to configure provider EDC API key
- #64 updated the irs-client version to 2.1.14 to adapt the edc discovery flow(bpnl)

### Added

- #48 Functionality to choose two themes (cofinityx /catenax) during build.
- #43 Implemented backend API for search filtering (asBuilt and asPlanned).
- #43 Integrated search functionality in the CFX frontend.
- #43 Added integration tests for search functionality based on legacy repo code.

### Fixed
- #43 Resolved missing API issues causing search functionality in CFX frontend to fail.

### Changed
- Add separate changelog for cfx. (TRX-347).

[Unreleased]: https://github.com/eclipse-tractusx/traceability-foss-frontend/compare/1.1.0...HEAD
