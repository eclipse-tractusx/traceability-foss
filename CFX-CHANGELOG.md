# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

_**For better traceability add the corresponding JIRA issue number in each changelog entry, please.**_

## [Unreleased]

## [14.0.0-cfx-2]

### Changed

 - TRACEX-533 remove fdescribe from test to run full test set
 - TRACEX-34 fix path-to-regexp CVE-2024-45296
 - TRACEX-60 fix cross-spawn dependency
 - TRACEX-533 repair unit test pipeline and revert breaking dependency changes
 - TRACEX-21 add resolution to fix Path traversal in webpack-dev-middleware
 - TRACEX-28 add resolution to fix Denial of service in http-proxy-middleware
 - TRACEX-489 provide fix for json-smart vulnerability
 - TRACEX-25 provide fix for semver vulnerability
 - TRACEX-471 Modify Helm chart pipeline to append git hash and update values repo
 - TRACEX-534 change dummy test controller to post requests to resolve CSRF alert
 - TRACEX-539 repair flaky integration tests
 - TRACEX-493 reverted supplier/customer parts sync

### Removed

## [14.0.0-cfx-1]

### Changed

- TRACEX-528 upgrade keycloak libs for keycloak 25 compatibility
- TRACEX-516 Remove supervisor role from admin endpoints
- Bump irs-registry-client to 2.1.25 to fix CVE-2024-7254
- TRACEX-418 Add pipeline for CFX Cucumber tests
- TRACEX-471 Add pipeline for publishing Helm charts

### Removed

- Remove unused vulnerable dependency commons-fileupload to fix CVE-2024-47554
- TRACEX-463 remove callbacks path parameters templates

## [13.0.2-cfx-13]

### Added

- TRACEX-145 Access and Authentication for BPDM Lookup over EDC communication
- TRACEX-184 Change Quality Notification API according to CX-0125 Traceability Use Case v2.0.0
- TRACEX-357 Add concept for BPDM Lookup Handling in UI and regarding Updates of BPDM information
- TRACEX-420 functionality to Sync child/supplier parts for Admin
- TRACEX-203 Added concept for aas lookup
- TRACEX-394 add double click listener to open detail view
- TRACEX-526 add customer part ID to advance filter

### Changed

- TRACEX-224 Setup Trace-X gatling test workflow using containers and switch to technical api key
- TRACEX-419 Fix env variables in settings.xml
- TRACEX-374 Fix bug in FE pagination for asbuilt and asplanned tables
- TRACEX-141 Fix global table filters
- Bump jsonschema2pojo-core to 1.2.2 to fix CVE-2024-47554
- Bump springdoc-openapi-starter-webmvc-ui to 2.8.3 to fix CVE-2024-45801, CVE-2024-47875, CVE-2024-48910
- TRACEX-384  Remove null check for manufacturerName in findByBpn
- TRACEX-278 Enable nginx forward proxy for cfx-frontend
- TRACEX-493 Fixed correct owner assignment after supplier sync
- TRACEX-395 Adjust logic to open part details on click leaf of the part tree

## [13.0.2-cfx-9 - 09.01.2025]

### Added

- TRX-179 Added reverse proxy to frontend nginx configuration
- TRX-366 Provide proper imprint and legal links

## [13.0.2-cfx-8 - 11.12.2024]

### Added
- TRX-377 Added GitHub workflow for Sonar scanning
- TRX-471 Added api key authentication feature
- TRX-476 Delete asset by id api provided
- TRX-476 Delete Part in added in FE
- TRX-441 Added dedicated Cofinity-X Trivy workflow
- TRX-458 Create quality notification will not be allowed if a business partner number is missing on a corresponding part.
- TRX-543 Duplicate connector urls returned by the discovery service will be filtered out.
- TRX-326 Added Metrics and Prometheus Endpoint
- TRACEX-159 Added concept and arc42 documentation for improved EDC discovery flow

### Changed
- TRX-373 Integrated automatic test data upload for BMW and Trace-X A on preprod
- TRX-93 Added load testing with Gatling
- TRX-116 Change User in Dockerfile for CFX Frontend
- TRX-117 updated spring boot to v3.3.5
- TRX-540 Refactor BPDM repository to use legalShortName (legalName as fallback) for manufacturer name
- TRX-XXX Replace null values from Manufacturer in as built and as planned response
- TRX-426 Changed building block to current architecture
- TRX-437 Set not null constraint to contractAgreementId in contract tables
- TRX-485 Adapt query catalog to find first valid contractOffer
- TRX-540 Adjust Notification Detail view to make less requests to the backend
- TRX-201 Remove submodel api

## [13.0.2-cfx-5 - 06.11.2024]

### Added
- #350 Added properties for provisioning to submodel / dtr service
- #349 Added second identity provider for Oauth2 for DTR and submodel service
- #62 - Added configurable assert refresh cron job with default values
- #99 added configurable publish asset job
- #66 Added api key authorization for /qualitynotifications and /qualityalerts
- #346 Reconfigured GitHub workflows to use GitHub Packages to fetch dependencies
- #373 Added test data for CFX/BMW for Preprod
- #127 Enhanced getManufacturerId logic to fallback to specificAssetIds in Shell.Payload when manufacturerId is not available in local_identifiers.

### Changed
- #105 Upgraded irs client library to 2.1.15
- #66 changed integration tests for edc notifications
- #66 changed error handling config and added new exception InvalidApiKeyException
- #410 extended timeout for feign client
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
- #353 Added digitalTwinType for assets provisioning

### Fixed
- #43 Resolved missing API issues causing search functionality in CFX frontend to fail.

### Changed
- Add separate changelog for cfx. (TRX-347).

[Unreleased]: https://github.com/eclipse-tractusx/traceability-foss-frontend/compare/1.1.0...HEAD
