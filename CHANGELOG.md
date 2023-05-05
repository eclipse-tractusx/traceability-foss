# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased - x.x.x]

### Added
- Added logic to push image to docker hub for eclipse-tractusx repository

### Changed
- Updated Publish documentation workflow to convert and deploy documentation as markdown (.md)
- Bumped cypress-io/github-action from 5.6.1 to 5.6.2
- Bumped veracode/veracode-uploadandscan-action@0.2.5 to 0.2.6
- Updated catena links from readme to reflext tractusx links
- Updated path of digital twin registry for dev environment
- Downgraded webpack to 5.75.0 and angular-devkit/build-angular to 15.2.3 to fix unit test pipeline

## [3.3.0] - 2023-05-02
### Added
- Added tx-root pom for maven multi module project
- Added tx-parent-spring-boot for using spring boot dependencies
- Added tx-models
- Added possibility to configure realm in FE
- Added license headers to helm chart files
- Added possibility to create an alert within my parts page, marked with WIP
- Added additional irs request on irs-service to also include SingleLevelUsageAsBuilt param
- Added owner column to asset table
- Added upstream visibility for parts
- Added bpn-url mapping api which provides simple CRUD operations
- Added project lombok
- Added Dockerfile to root directory
- Added helm-test workflow

### Changed
- Updated spring-boot:core from 6.0.6 to 6.0.8 for cve-2023-20863
- backend directory and module to tx-backend
- backend/cucumber-tests directory to tx-cucumber-tests
- Updated mapping of assets
- Alignment of user and groups between helmchart and application
- Updated readme links
- Container labelling refactored
- Increased table size tenfold. From 5, 10, 25 to 50, 100, 250
- Moved dash-ip tool to root-pom to have a managed version
- Updated backend dependencies to reflect current state
- Updated notifications contract policy to include trace policy constraint
- Switched from GET catalog (edc) to POST catalog
- Moved Investigation JPA implementation to the correct folder structure
- Moved Notification JPA implementation to the correct folder structure
- Fixed bug to reflect investigation status on assets

### Removed
- Removed usage of add-license-header script in FE hook
- Removed usage of map and map component
- Removed supplierPart boolean from asset
- Removed all excluded files from kics scan

## [3.2.0] - 2023-04-17

### Added
- Created BPN - BPN - EDC configuration page with mappings for notification flow

### Changed
- Refactored messageId of a notification to have own uuid instead of reusing notificationId
- Changed receive / update edc callbacks to match one seperate method for each process
- Updated the Notification Publisher to request initial notifications to the /receive endpoint and updates to the related /update endpoint
- Updated cypress-io/github-action from 5.6.0 to 5.6.1
- Updated peaceiris/actions-gh-pages from 3.9.2 to 3.9.3
- Upgraded base image from sha256@2b33ef284e6dc43a61903cef6d36dbce13414a9e5444e2c96cdd5e35123f9903 to: sha256@c26a727c4883eb73d32351be8bacb3e70f390c2c94f078dc493495ed93c60c2f
- Fixed parts not being marked as under investigation
- Adapt notification receiver side to accept severity by real name instead of enum constant
- Improved admin page navigation (as a left side menu)
- Combined results of discovery service and fallback mock service to provide bpn url mappings
- Updated EDC Provider to 0.3.0 version

## [3.1.1] - 2023-04-04

### Added
- Some unit tests for better code quality

### Changed
- Fixed edc notification flow bug
- Updated org.springframework/spring-expression from 6.0.6 to 6.0.7
- Updated net.minidev/json-smart from 2.4.8 to 2.4.10
- Updated documentation of application

## [3.1.0] - 2023-04-03

### Added
- Added persistent history of EDC Notifications
- Added validation for UpdateInvestigationRequest, reason for (decline, accepted, close)
- Added Title to sections that might be cut of with three dots (...)
- Added documentation regarding base docker image and prepared for future QG updates
- Added hint to input field with additional information for max and min length validation
- Added feature to table to be able to clear all selections or only the current page
- Added license headers to all chart specific files
- Added information about the specific base image used by our docker images

### Changed
- Validation logic of edc notification flow
- Restructured helm charts to an parent helm chart which includes frontend and backend
- Updated database fields within Investigation table (accept_reason, decline_reason, close_reason)
- Used same shadow for other parts table than we use for my parts table
- Improved visibility of text that might be too large.
- Changed mapping of partNumber from customerPartId to manufacturerPartId
- Adapted catena style guide for colors
- Removed quality type from ui but kept base functionality for future use
- Improved and separated *.md documentation, especially README.md
- Improved error page styles and fixed menu urls in terms of 404 pages
- Improved visibility for field validation and added * to required fields
- Improved validation error message for max and min length
- Updated Investigation model with `createdByName` and `sendToName` fields
- Improved UX for table selection
- Updated all environment specific helm chart files to follow same structure
- Upgraded all environments to use most recent release of irs including edc charts 0.3.0
- Removed unnecessary properties on irs charts
- Removed catena specific value files
- Updated deck.gl/core from 8.8.25 to 8.9.2
- Updated angular/language-service from 15.1.5 to 15.2.2
- Updated veracode action from 0.2.4 to 0.2.5
- Updated trivy-action to use version instead of sha and set to 0.9.2
- Updated cypress action from 5.0.7 to 5.6.0

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
