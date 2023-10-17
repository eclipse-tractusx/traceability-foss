# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [8.0.0 - 16.10.2023]

### Added

- added multisorting in FE for notifications
- added possiblity to add operator to searchquery of assets
- added global search field and combined with the OR operator to be able to search for multiple elements in the database
- Filter for each assetAsBuilt table attribute
- Filter for each assetAsPlanned table attribute
- Extended testdata to reflect better overview of assets
- Extended testdata to reflect better overview of assets
- Support for TractionBatteryCode
- Support for JustInSequence Semantice data model in FE
- Added country flags icons to manufacturing Country
- new submodelserver related API endpoints for data provisioning /api/submodel/data/{id}
- Added global search field to other parts
- Added possiblity to provide multiple semanticDataModels in filter
- Added two new columns for amount of open Notifications per part in part views (asBuilt)
- Added test dependencies to dash ip check
- Added datepicker for date fields inside of the table filters

### Changed

- updated IRS helm chart from 6.6.1 to 6.7.2
- Updated policy related logic to reflect IRS changes
- Moved response handling from the backend folder to the model folder, addressing a TODO item.
- replaced StartQualityAlertRequest with StartQualityNotification
- updated mikefarah/yq from 4.35.1 to 4.35.2
- updated maven-site-plugin from 4.0.0-M5 to 4.0.0-M9
- updated testcontainer-postgresql from 1.17.6 to 1.19.0
- updated docker/build-push-action from 4 to 5
- Updated user manual to reflect current state of the part views
- Fixed dash IP GitHub action that scans Backend dependencies
- Date format of manufacturingDate, validityPeriodFrom, validityPeriodUntil, functionValidFrom, functionValidTo to be
  OffsetDateTime for Business logic and Instant for Database
- Changed types of OffsetDates within the database to be a timestamp including timezone
- Fixxed mapping of idShort
- Changed date formats of assets to have offsetDateTime instead of Date or LocalDateTime
- Aligned date formats in the rest api for assets
- Increased version of jetty-http from 11.0.15 to 11.0.17 and excluded from edc package

### Removed

- Owner filter and replaced it with the new filter query param
- Removed profile based feature flag for investigations / alerts

## [7.1.0 - 29.09.2023]

### Added

- Splitscreen View with sliders on parts and otherParts View
- New test data for as planned assets aswell as JustInSequence and TractionBatteryCode
- Toggle for parts and other parts to switch views asPlanned/asBuilt
- LocalStorage to be used for saving view setting
- archunit tests as preparation for good quality architecture checks
- safety and security doc including roles matrix
- handling for duplicate shellDescriptor ids when refreshing registry

### Changed

- added sorting for /api/investigations received and created endpoints
- added sorting for /api/alerts received and created endpoints
- integration tests uses flyway now rather than hibernate schema auto creation
- irs helm updated from 6.5.0 to 6.6.1
- BpnEntity now contains BpnEdcMappingEntity fields
- Directories of bpnEntity to match architecture
- Mapping logic of catena-x site id and manufacturerPartId for AssetsAsPlanned

### Removed

- Old edc code not necessary anymore
- BpnEdcMappingEntity removed with related repository

## [7.0.0 - 18.09.2023]

### Added

- OAuth2 client credentials rest template interceptor
- Configuration for left and right policies to use registry client library
- Add support for JustInSequence aspect model
- TRG Github Action Pipeline for quality checks
- ErrorMessage field to investigation and alerts
- Cucumber tests for quality investigations
- Refactored asset structure to allow new API for assets-as-planned
- Optional parameter "receiverBpn" to /investigations endpoint
- NEW API /api/assets/as-planned
- NEW API DELETE /api/registry
- NEW API GET /api/shelldescriptors
- cascading sorting functionality by allowing multiple sort query parameters on APIs
- cascading sorting functionality for Parts and OtherParts tables in FE
- NEW API GET /api/assets/as-planned/distinctFilterValues
- NEW API GET /api/assets/as-built/distinctFilterValues
- Added Batch 2.0.0 support
- Updated some patch version for used dependencies.

### Changed

- API BREAKING CHANGE: /api/assets changed to /api/assets/as-built
- Changed digitalTwinRegistryRestTemplate to use token in requests
- Update asBuild test data to 1.5.3 and asPlanned to 1.5.1
- Changed transfer notification logic not to break iteration loop when sending notifications to bpn with more than 1
  connector endpoints
- Decentral flow only using bpn for resolving globalAssetIds
- Logic of commitId retrieval by frontend has changed
- Readme / Docker Notice information as required in TRGs
- Removed references to GitHub registry and added docker hub for tractusx instead
- Removed UX breaking black status box
- Updated cypress-io/github-action from 5.8.4 to 6.0.0
- Updated asciidoctor-maven-plugin from 2.2.3 to 2.2.4
- Updated owasp:dependency-check from 8.3.1 to 8.4.0
- Updated commons-io from 2.11.0 to 2.13.0
- Updated snakeyaml from 2.0 to 2.1
- Split up Parts View in Frontend to parts asBUilt and asPlanned
- Replaced own implementation of getCatalog, negotiateAgreement, and validatePolicy with irs-client-library
  implementation.
- Updated irs-registry-client from 1.1.0-SNAPSHOT to 1.2.0-SNAPSHOT
- Updated irs-helm from 6.4.1 to 6.5.0
- Migrated groovy integration tests to SpringBootTests
- API GET/api/assets/as-built allow Search criteria to be provided
- API GET/api/assets/as-planned allow Search criteria to be provided
- Upgraded aquasecurity/trivy-action from 0.11.2 to 0.12.0
- Upgraded actions/checkout from 3 to 4
- Upgraded maven-checkstyle-plugin from 3.2.1 to 3.3.0
- Upgraded swagger-annotations from 1.6.10 to 1.6.11
- Upgraded spring-boot-maven-plugin from 3.0.2 to 3.1.3
- Upgraded shedlock.version from 5.5.0 to 5.7.0
- Upgraded mikefarah/yq from 4.34.2 to 4.35.1
- Upgraded snakeyaml from 2.0 to 2.2
- Upgraded docker/login-action from 2 to 3
- Upgraded cypress-io/github-action 6.0.0 to 6.5.0

### Known knowns

- Backend [TRACEFOSS-1458]: AdminView: No validation of BPN for BPN EDC URL mapping
- Backend [TRACEFOSS-589]: Backend API access without login returns incorrect HTTP status code (500 instead of 401)
- Backend [TRACEFOSS-2148]: Endpoints for parts and notifications returns unsorted list

---

- Frontend [TRACEFOSS-2149]: Sorting on empty table causes unhandled error view

---

- Security [TRACEFOSS-829]: CVE Strict-Transport-Security header - The HSTS Warning and Error may allow attackers to
  bypass HSTS
- Security [TRACEFOSS-830]: CVE one stack trace disclosure (Java) in the target web server's HTTP response
- Security [TRACEFOSS-919]: Authorization Bypass Through User-Controlled SQL Primary Key CWE ID 566
- Security [TRACEFOSS-984]: Improper Output Neutralization for Logs CWE ID 117
- Security [TRACEFOSS-1313]: Using components with known vulnerabilities
- Security [TRACEFOSS-1314]: Open Redirect - host header injection
- Security [TRACEFOSS-1315]: No additional authentication component (MFA) during login process

---

- Environment [TRACEFOSS-2164]: HTTP Requests for syncing the submodel server inoperable~~

### Removed

## [6.0.1-rc2]

### Added

- OAuth2 client credentials rest template interceptor
- Configuration for left and right policies to use registry client library
- TRG Github Action Pipeline for quality checks
- ErrorMessage field to investigation and alerts

### Changed

- Changed digitalTwinRegistryRestTemplate to use token in requests
- Update asBuild test data to 1.5.3 and asPlanned to 1.5.1
- Changed transfer notification logic not to break iteration loop when sending notifications to bpn with more than 1
  connector endpoints
- added handling for null manufacturerName in IrsJobResponse, if null is passed it is replaced with "
  UNKNOWN_MANUFACTURER"

### Removed

## [6.0.0 - 2023-07-21]

### Added

- Moved all parts of app config to helm charts to be fully configurable
- Helmignore config params for wrong values.yaml files
- Home / Source URL in Helm Chart
- Name Overrides in Helmchart for pgadmin, irs-helm and tractusx-connector
- Added decentral registry approach
- Added discovery finder / edc discovery service for looking up edc urls of receiver of notifications
- Added about component with additional Workflow to load repo info into the component
- Add Transformer to support new EDC constraint operator format

### Changed

- Modified IRS Policies support to handle multiple policies
- Readme titles to match TRGs
- Updated Irs helm chart to 6.3.1
- Update EDC dependencies to 0.1.3
- Update implementation to use EDC 0.5.0
- Spring Security Config Update from 6.0.3 to 6.0.5
- Spring Boot Update from:  3.0.7 to 3.1.2
- Spring Core Update from: 6.0.8 to 6.0.11

### Removed

- unused classes and methods

### Known knowns

- Backend [TRACEFOSS-1458]: AdminView: No validation of BPN for BPN EDC URL mapping
- Backend [TRACEFOSS-589]: Backend API access without login returns incorrect HTTP status code (500 instead of 401)
- Backend [TRACEFOSS-2148]: Endpoints for parts and notifications returns unsorted list

---

- Frontend [TRACEFOSS-2149]: Sorting on empty table causes unhandled error view

---

- Security [TRACEFOSS-829]: CVE Strict-Transport-Security header - The HSTS Warning and Error may allow attackers to
  bypass HSTS
- Security [TRACEFOSS-830]: CVE one stack trace disclosure (Java) in the target web server's HTTP response
- Security [TRACEFOSS-919]: Authorization Bypass Through User-Controlled SQL Primary Key CWE ID 566
- Security [TRACEFOSS-984]: Improper Output Neutralization for Logs CWE ID 117
- Security [TRACEFOSS-1313]: Using components with known vulnerabilities
- Security [TRACEFOSS-1314]: Open Redirect - host header injection
- Security [TRACEFOSS-1315]: No additional authentication component (MFA) during login process

---

- Environment [TRACEFOSS-2164]: HTTP Requests for syncing the submodel server inoperable~~

## [5.0.0] - 2023-07-10

### Added

- Added back button in notification detailed view
- Added alert detail view
- EDC SPI Dependency for using provided models
- Added default response types to apis
- Irs policies support ( on application startup registers policies in irs instance )
- Added helm upgrade workflow to test upgradeability of the helm charts
- Added helm test backwards compatability to test the helm charts with the latest kubernetes versions

### Changed

- Changed Layout in notification detailed view
- Changed request parameter for registerjob request to irs to match requirements of irs
- Migration of edc 0.4.1 endpoints and api flow
- fixed bug where language switcher did not update to the selected language
- Changed SerialPartTypization aspect model to SerialPart
- Changed AssemblyPartRelationship aspect model to SingleLevelBomAsBuilt
- Changed semantic data model to be displayed in camel case

### Removed

## [4.1.0] - 2023-06-26

### Added

- Spring profiles for integration int-a and int-b environments
- Quality Alert feature
- Updated testdata to reflect asPlanned assets
- Added asPlanned lifecycle to the asset domain
- Added Semantic Data Model 'PartAsPlanned'
- Upgraded cypress-io/github-action from 5.8.0 to 5.8.3
- Upgraded jruby from 9.3.9.0 to 9.4.3.0
- Upgraded aquasecurity/trivy-action from 0.10.0 to 0.11.2
- Upgraded maven-project-info-reports-plugin from 3.4.3 to 3.4.5

### Changed

- Updated spring boot to 3.0.7 to fix: CVE-2023-20883
- Fixed calculation of otherParts amount within dashboard response
- Fixed incorrect label mapping in notification action modals

### Removed

## [4.0.0] - 2023-06-12

### Added

- Added translationContext "commonAlert" to all modal components
- Send quality alert to customer / action on queued items
- Adapt asset synchronization from IRS
- Cors configuration for environment e2e-a and e2e-b
- GitHubAction to update dependencies files on merge to main
- Added testdata with batches
- Added Semantic Data Model to frontend to support batches
- Added batch aspect to the asset import process

### Changed

- Updated registry urls within env profiles
- Changed error message for notification status transitions
- Replaced DELETE Pod Requests with StatefulSets
- Made Quality Alerts Tab visible
- bump json-unit-assertj from 2.36.1 to 2.38.0
- bump spring-cloud-dependencies from 2022.0.2 to 2022.0.3
- bump maven-project-info-reports-plugin from 3.4.3 to 3.4.4 #158
- Refactored logic to save different kind of assets related to their aspect

### Removed

- Bpn Mapping fallback in INT environment profile - has been removed

## [3.5.0] - 2023-05-30

### Added

- Added option to hide "Investigation for components" in part-detail.component.html
- E2E A & B env profile configuration
- Added tests for SonarQube exclusions
- Added alerts controller
- Inbox for quality alerts (marked with WIP role)
- Sortable columns in notification.component.ts and related components
- Insomnia Collection for Alerts api
- Added E2E cucumber test for sending notification

### Changed

- Refactored other-parts.component.html - split into new components: supplier-parts.component.html and
  customer-parts.component.html
- Bump cypress-io/github-action from 5.6.2 to 5.7.1
- Bump surefire-plugin.version from 3.0.0-M8 to 3.0.0
- Bump java-jwt from 4.3.0 to 4.4.0
- Bump asciidoctor-maven-plugin from 2.2.2 to 2.2.3
- Bump spring-cloud-dependencies from 2022.0.1 to 2022.0.2
- Bump asciidoctorj from 2.5.7 to 2.5.8
- Use Selective dependency resolutions for @angular-devkit/build-angular to keep 15.2.8 version locked (so
  @angular-builders/custom-webpack uses the same version as well)
- Aligned help button and user icon in the header to official C-X styleguide
- Changed logic of merging response from irs to match the correct ids of the relationships
- Updated open api collection to detect security issues on rest api
- Upgraded karma package dependency: socket.io-parser to 4.2.3 (to solve Insufficient validation when decoding a
  Socket.IO packet)
- Corrected alignment for severity and other fields in section "Overview" in quality investigation detail page
- Upgraded cypress-io/github-action from 5.7.1 to 5.8.0
- Changed trivy.yml settings for frontend (use "--format sarif" instead of "--template sarif.tpl")

### Removed

- Removed selection column on Customer Parts page
- Removed classes: AssetFacade, Constants, Command, ContractAgreementRequest, ContractOfferRequest, AssetFacadeTest
- Removed log flooding in asset sync process

## [3.4.0] - 2023-05-11

### Added

- Added logic to push image to docker hub for eclipse-tractusx repository
- Added testdata to database to ensure working notification flow
- Added base implementation for quality notifications which can be used for alerts and investigations

### Changed

- Updated Publish documentation workflow to convert and deploy documentation as markdown (.md)
- Bumped cypress-io/github-action from 5.6.1 to 5.6.2
- Bumped veracode/veracode-uploadandscan-action@0.2.5 to 0.2.6
- Updated catena links from readme to reflext tractusx links
- Updated path of digital twin registry for dev environment
- Updated swagger api doc and added collection
- Updated dev setup documentation
- Changed logic of investigations to be more extensible for alerts
- Upgraded irs from 5.0.9 to 5.3.0
- Upgraded @angular-devkit/build-angular to ^15.0.0 (to have the same dependency as @angular-builders/custom-webpack
  has)
- Fix sonar bug in minimap.d3.ts
- Upgraded karma dependencies (use engine.io@^6.4.2 to solve Uncaught Exception vulnerability)
- Refactored investigation class to be qualitynotification to reflect a base class for future extensions
- Renamed notification table to investigation_notification to be able to understand the difference of notification
  source

### Removed

- Not needed enum params in UpdateInvestigationRequest

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
- Added new testdata set for bom lifecycle "asBuilt"
- Added a custom Pageable Scheme

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
- Added documentation on the usage of testdata

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
- Updated the Notification Publisher to request initial notifications to the /receive endpoint and updates to the
  related /update endpoint
- Updated cypress-io/github-action from 5.6.0 to 5.6.1
- Updated peaceiris/actions-gh-pages from 3.9.2 to 3.9.3
- Upgraded base image from sha256@2b33ef284e6dc43a61903cef6d36dbce13414a9e5444e2c96cdd5e35123f9903 to:
  sha256@c26a727c4883eb73d32351be8bacb3e70f390c2c94f078dc493495ed93c60c2f
- Fixed parts not being marked as under investigation
- Adapt notification receiver side to accept severity by real name instead of enum constant
- Improved admin page navigation (as a left side menu)
- Combined results of discovery service and fallback mock service to provide bpn url mappings
- Updated EDC Provider to 0.3.0 version
- Replaced jib with docker-build-push action in workflows

## [3.1.2] - 2023-05-02

### Added

- Helm lint, test and install action workflow
- Updated frontend and backend dependency file and requested review for open license issues
- Updated broken links in readme
- Refactored github action workflow to isolate latest and release version tags
- Added missing license headers to files
- Synchronized configuration for runAsUser for container images
- Adapt default values.yaml to allow installation of helm charts in any environment

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

- **Traceability BoM asBuilt** - with the use of IRS we retrieve a BoM tree for lifecycle "as built" for serialized
  parts as well as batches, which as a prerequisite are provided to the Catena-X network.
- Usage of the aspects "SerialPartTypification", “Batch” and "AssemblyPartRelationship".
- Tree built in the downwards direction top-down/parent-child. Visualization of the BoM tree and list view of own
  manufactured as well as being supplied with parts.

## [0.1.0] - 2022-09-21

### Added

- **BoM asBuilt** Fetch parts and supplier parts, display details, display part tree
- **EDC and IRS integration**

[unreleased]: https://github.com/eclipse-tractusx/traceability-foss-frontend/compare/1.1.0...HEAD

[1.1.0]: https://github.com/eclipse-tractusx/traceability-foss-frontend/compare/1.1.0

[1.0.0]: https://github.com/eclipse-tractusx/traceability-foss-frontend/compare/1.0.0

[0.1.0]: https://github.com/eclipse-tractusx/traceability-foss-frontend/compare/0.1.0
