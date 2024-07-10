# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres
to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

_**For better traceability add the corresponding GitHub issue number in each changelog entry, please.**_
## [UNRELEASED - DD.MM.YYYY]
### Changed
- #1173 Update IRS-Helm from 7.1.4 to 7.2.0 - updated Compatibility Matrix
- #1082 fix duplicate key errors when synchronizing assets with IRS
- #970 fixed bug where the right operand of policies was not showing up in table and detailed view

## [12.0.0 - 05.07.2024]

### Added
- #832 added policymanagement list view, creator and editor
- #737 Added concept: Contract table -> parts link action
- XXX Added interceptor to EdcRestTemplates to log requests
- #915 Added section to documentation: EDC-BPN configuration
- #1037 Added link from contracts view to the corresponding filtered part table view
- #1017 added file for CC BY 4.0 license for TRG 7
- #985 Added reference to part/notification under contract
- #786 Added icons on part table to let admin reload registry / sync assets via IRS
- #520 Added Attribute BPN to DetailView
- #1112 Added association int-a/int-b environment to argo workflow

### Changed
- #965 Implement proxy functionality of the IRS policy store
- #962 Changed notification model to new one in frontend/backend
- #962 Removed initial notification message for notification flow
- #962 Adapt cucumber test to include the mandatory attributes for creating a quality notification
- #753 Refactored message history in notification detail view
- XXX updated local deployment documentation
- #1037 extended autocomplete api by contractAgreementId
- #985 Added function to save Contracts based on notification contractAgreementIds into the database
- #985 Added function to filter notifications for contractAgreementIds
- XXX updated JsonSchemaTest now the test pulls the latest version of the json file
- XXX deactivated a test class in tx-backend which behaved undesirably
- #1017 updated contributing, notice, and readme files for TRG 7
- #639 handle expired or incorrect policies when sending notifications
- #786 Added authorization as admin for submodel api & registry api
- #884 Upgraded tractionBatteryCode from 1.0.0 to 2.0.0
- #884 Fixed mapper of tractionBatteryCode
- #1009 reimplemented retry request logic for notification approval
- #786 Added alternative port (only accessible within same cluster) for application which is used for unsecured API endpoints.
- #786 Introduced internal url for notification contracts.
- #994 improved bpn edc configuration view uux
- #1082 fix update of parts when synchronizing with IRS
- #xxx fixed notification description on receiver side
- #875 owasp dependency check tool is now used from github action image instead of maven plugin
- XXX fixed display of semantic data model in parts as planned table
- #943 renamed distinctFilterValues API to searchable-values for the asBuilt, asPlanned and notification routes
- #943 changed these endpoints to POST with body instead of GET with parameters

### Known knowns
- #786 Implemented short term solution for securing EDC Callback APIs

### Removed

- XXX Removed EdcNotifiactionMockServiceImpl class and replaced with mocks
- #1033 removed action jira-publish-release workflow

## [11.0.2 - 29.05.2024]
### Added
- #1010 Made submodel path configurable
- #838 Added User experience > Table design section in arc42 documentation


### Changed
- #1010 Updated IRS Helm Version from 5.1.6 to 5.1.7

### Changed

- XXX increase spotbugs-plugin version from 4.8.3.0 to 4.8.5.0
- XXX increase install-plugin version from 3.1.1 to 3.1.2
- XXX increase testcontainer-postgresql version from 1.19.7 to 1.19.8
- XXX increase json-unit-assertj version from 3.2.2 to 3.2.7
- XXX increase aquasecurity/trivy-action version from 0.19.0 to 0.20.0
- XXX increase mikefarah/yq version from 4.43.1 to 4.44.1

## [11.0.1 - 22.05.2024]

### Added
- #859 added autocomplete on subset of assets in notification creation/edit view
- #997 added publish assets state check to parts table
- #XXX added border to dashboard notifications table and view all count

### Changed
- #778 return empty PageResult when no contract agreement Ids are found instead of http 404 in /contacts API
- #XXX Fixed some sonar issues in frontend application
- #958 Fixed bug where available parts filter was reset when list of affected parts are zero in notification creation
- XXX Removed EdcNotifiactionMockServiceImpl class and replaced with mocks
- #XXX Switched eclipse-temurin:21-jre-alpine@sha256:fb4150a30569aadae9d693d949684a00653411528e62498b9900940c9b5b8a66 to 23467b3e42617ca197f43f58bc5fb03ca4cb059d68acd49c67128bfded132d67
- #837 Moved data import and IrsResponseMappers under same interface and shared methods
- #947 Updating irs-helm from 7.1.1 to 7.1.2 to use latest catena-x policy
- #963 update irs-client-lib.version from 2.0.3 to 2.0.4-SNAPSHOT to fix /contracts api
- #989 changed german word "Eigen" to "Eigene"
- #992 improved uux in selection of columns in table settings
- #994 moved search bar above new and existing configurations
- #994 disable inputs of newly added bpn configurations
- #995 disable creation and editof notifications for admin role
- #991 make menu table headers unvisible to not block column and filters of tables
- #915 Updated user manual documentation (chapters and screenshots)
- #998 fixed bug where global search bar could search for BPN and adjusted placeholder label to reflect column names
- #913 Changed severity icons
- #978 Fixed edc policy creation with edc version 0.7.0

## [11.0.0 - 08.05.2024]
### Added
- #844 Validation for BPN to Notification API (Create / Edit), Fixed pagination
- #726 Added @Preauthorize annotation to dashboard controller
- #849 Added concept: Notifications to multiple BPNs
- #837 Added digital twin type to data provisioning workflow to be able to lookup shells created by trace-x
- #783 Validation that receiver of a notification must not be same as sender.
- #831 Added concept: Notification data model revision
- #859 Enable autocomplete API to filter for given assets
- #778 Added counterPartyId to getCatalogRequest

### Changed
- #837 migrate to irs-helm 7.1.1
- #837 migrate to irs-decentral-client-library to 2.0.2-SNAPSHOT
- #844 Prefilled bpn on investigation creation
- #843 Refactored e2e tests, added edit notification e2e test case
- #828 fix duplicates in traction_battery_code_subcomponent table
- #617 redesigned inbox table
- #603 Upgraded SingleLevelBomAsBuilt, SingleLevelBomAsPlanned & SingleLevelBomAsBuilt to 3.0.0
- #603 Upgraded Batch, SerialPart and JustInSequencePart to 3.0.0
- #603 Upgraded PartAsPlanned to 2.0.0
- #918 Merged parts and other parts into one table
- #918 Fixed translations and normal case for autocomplete values
- #778 update EDC from 0.5.3 to 0.7.0
- #XXX update of lombok from 1.18.30 to 1.18.32
- #XXX update of findsecbugs plugin from 1.12.0 to 1.13.0
- #XXX update of commons-compress from 1.26.0 to 1.26.1
- #XXX update of logback from 1.5.5 to 1.5.6
- #XXX update of cucumber-bom from 7.16.1 to 7.17.0
- #XXX Updated spring boot from 3.2.4 to 3.2.5
- #XXX Bumped logback-core & logback-classic from 1.5.4 to 1.5.5

### Removed
- #602 digitalTwinType instead of semanticId. DigitalTwinType causes problems in release 24.05

## [10.8.4 - 17.04.2024]

### Added
- #780 store api documenation in docs/api to conform with TRG 1.08
- #622 Notification Update API
- #774 Added initial concept for handling multiple BPNs
- #834 Added possiblity to exclude elements from the results of the asset api filter
- #586 BPDM lookup feature

## Changed
- XXX Updated insomnia collection
- #834 Behaviour of parts selection in edit / create notification view
- #823 migrate to irs-helm 6.18.0
- #636 migrate to digital-twin-registry version 0.4.9 from 0.3.22
- #622 Added functionallity to edit existing notifications within CREATED state
- #602 use digitalTwinType instead of semanticId to determine asBuilt or asPlanned assets
- Spell check arc42 documentation and administration guide and make it consistent
- bump ch.qos.logback:logback-core from 1.4.14 to 1.5.4
- bump peaceiris/actions-gh-pages from 3.9.3 to 4.0.0
- bump aquasecurity/trivy-action from 0.18.0 to 0.19.0
- bump org.springframework.cloud:spring-cloud-dependencies from 2023.0.0 to 2023.0.1
- bump org.awaitility:awaitility from 3.0.0 to 4.2.1
- bump org.asciidoctor:asciidoctorj-diagram from 2.2.13 to 2.3.0
- bump io.cucumber:cucumber-bom from 7.15.0 to 7.16.1

## [10.8.2 - 05.04.2024]
### Removed
- #547 Removed classification check on alert / investigation update callback methods

## [10.8.1 - 04.04.2024]

### Added
- #695 OAuth2.0 Client scope configuration
- #606 Added error message into notifications on failure
- #596 Added Policy management documentation
- Added overview of the scheduler tasks in documentation
- #736 Added Contract Detailed View
- #706 Created notification classes to support both alert and investigations
- #706 Notification controller having the same endpoints as alerts and investigations controllers
- #736 add contractAgreementId as searchable field for /contracts
- Added capitalization section in guidelines.md
- #616 Allow edc notification update with empty asset list
- #630 Added Parts extended detailed view

### Changed
- #709 Bumped spring-core from 6.0.17 to 6.1.5
- #606 cucumber tests retry on error
- #606 refactored response model to only be used by common model package tx-models
- #709 Fixed CVE-2024-22257 overriding spring-security-core from 6.1.7 to 6.2.3
- #596 Policy management has been moved to different module
- #616 Merged quality investigations / alerts into a single view
- #762 updated documentation for release 24.5
- #706 StartNotificationRequest now requires additional parameter type ("ALERT", "INVESTIGATION") which
- #706 Search criteria allows to filter by new type parameter
- #706 Notification response have new title parameter
- #718 update tj-actions/changed-files from v42 to v44
- #718 update maven-project-info-reports-plugin from 3.4.5 to 3.5.0
- #718 update jsonschema2pojo-core from 1.1.1 to 1.2.1
- #718 migrate Spring Boot from 3.1.9 to 3.2.4
- #630 Updated user manual with new part detailed view also for supplier and customer parts
- #736 fixed bug in request logic of contracts detailed view
- Improved the release documentation
- #706 QualityNotification naming in code replaced with Notification

### Removed
- Shedlock, resilence4j, templateResolver as not used anymore
- #706 Removed alert and investigation specific classes and services to replace them with merged notification classes

## [10.8.0 - 03.04.2024] - BROKEN RELEASE - DO NOT USE

## [10.7.0 - 18.03.2024]

### Added
- #421 Added contract agreement view
- #515 Service Unavailable Response on Notification failure
- #536 Added import state PUBLISHED_TO_CORE_SERVICES in frontend
- #420 add /contracts api to fetch contract agreement information from EDC for assets
- #423 Notification will be created/persisted in case of a edc notification
- Added a step to the pull-request-backend.yml which checks if the pom.xml(root) properties have some versions ending with -SNAPSHOT
- Added a PostConstruct method in PolicyStartUpConfig to allow Integration tests to run without errors in stack traces.
- #536 added new ImportState to asset PUBLISHED_TO_CORE_SERVICES indicating edc assets and dtr shells were created for given asset
- #536 added cron job responsible to publish assets in PUBLISHED_TO_CORE_SERVICES import state to edc and dtr
- #652 add GitHub action to publish Swagger to Swaggerhub

### Changed
- Updated RELEASE.md to the latest release guide (added more steps)
- #515 Fixed notification toast click area
- #625 increased height of tables
- #423 Moved errorMessages from investigation/alert to notification list
- Updated COMPATIBILITY.md matrix adding release C-X 24.3 and 23.12
- #536 rework /policies to respond with policies from the IRS policy store
- #536 sync assets logic was adjusted to create IRS jobs only for assets that are not in TRANSIENT or IN_SYNC states
- #536 Updated Arc42 documentation and user-manual with publish assets informations
- Bumped softprops/action-gh-release from v1 to v2
- Bumped azure/setup-helm from v3 to v4
- Bumped aquasecurity/trivy-action from 0.17.0 to 0.18.0
- Bupped cucumber-bom from 7.12.1 to 7.15.0
- Bumped jetbrains-annotation from 24.0.1 to 24.1.0
- Bumped commons-logging from 1.2 to 1.3.0
- Bumped shedlock from 5.11.0 to 5.12.0
- Overridden transitive commons-compress version by 1.26.1 to fix CVE-2024-26308
- Overridden transitive commons-codec version by 1.16.1 to fix CVE-2024-26308
- Update irs-registry-client from version 1.6.0-SNAPSHOT to 1.6.0
- Updated review-message for check pom for -SNAPSHOT workflow
- Changed base image from eclipse-temurin:17-jre-alpine to eclipse-temurin:21-jre-alpine
- Changed build image from maven:3-openjdk-17-slim to maven:3-openjdk-18-slim
- #742 rework test management strategy for frontend and backend part

### Removed
- #625 Removed the header and breadcrumbs section from app layout

## [10.6.0 - 04.03.2024]

### Added
- Added concept #638: Contract agreement admin view
- Added support for meta key for multi sorting on tables

- Added concept #578: Consistent null values
### Changed
- Spring-core bumped from 6.0.16 to 6.0.17
- Updated user manual
- JSON Schema generation for valid submodel mapping
- Added support for meta key for multi sorting on tables
- Added error description and retry button to error toast when creating notifications
- Bumped version mikefarah/yq@v4.40.5 to mikefarah/yq@v4.42.1
- Bumped version lombok from 1.18.28 to 1.18.30
- Bumped version ts-graphviz/setup-graphviz@v1 to ts-graphviz/setup-graphviz@v2
- Bumped version Schedlock 5.10.0 to 5.11.0
- updated yarn.lock file
- [#542](https://github.com/eclipse-tractusx/traceability-foss/issues/542) Fixed bug where it where filter was reset when sorting in other parts table

### Removed
- Removed EDC notification asset classes and replaced with IRS lib implementation
- [#528](https://github.com/eclipse-tractusx/traceability-foss/issues/528) Removed sorting tooltip on quality incidents tables on dashboard page

## [10.5.0 - 22.02.2024]

### Changed
- updated publish-documentation workflow with two new steps
- changed @ApiModelProperty annotation with @Schema
- added logic to consume tombstone information from IRS for assets

### Removed
- removed frontend/dist folder from codeQL scan

## [10.4.0 - 19.02.2024]

### Added
- Added tombstone icon to parts table and error description in parts detail view
- Endpoint (assets/import/report/{importJobId}) for retrieving import report
- Added concept #568: Policy management
- Added concept #436: Intermediate status handling
- Added add Parts view concept
- Added separate Docker notices for both front- and backend

### Changed
- actions/chache bumped from v3 to v4
- borales/actions-yarn bumped from v4 to v5
- peter-evans/create-pull-request bumped from v5 to v6
- peter-evans/dockerhub-description bumped from v3 to v4
- aquasecurity/trivy-action bumped from 0.16.1 to 0.17.0
- sonar-maven-plugin bumped from 3.9.1.2184 to 3.10.0.2594
- rest-assured bumped from 5.3.2 to 5.4.0
- testcontainer-postgresql bumped from 1.19.1 to 1.19.4
- tomcat-embed-websocket bumped from 10.1.16 to 10.1.18
- IrsCallbackController is now validating jobId to prevent log injections from unwanted usage
- Bump irs-helm version from 6.13.0 to 6.14.1
- Bump irs-client-lib version from 1.5.1-SNAPSHOT to 1.6.0-SNAPSHOT
- Changed Add Parts concept to Edit Parts concept


### Removed
- Investigations/Alerts for assets_as_planned parts

## [10.3.0 - 05.02.2024]

### Added
- Added AVD-KSV-0014 to trivy ignore
- Added tooltips on functionalities that are unauthorized or unavailable
- Added concept for adaptions for IndustryCore Changes CX-0126 and CX-0127
- Added concept #521 revoked notification handling
- Added eclipse trace-x matrix channel to README.md and CONTRIBUTING.md

### Changed
- Updated Irs Library from 1.4.1-SNAPSHOT to 1.5.1-SNAPSHOT
- Changed some java implementations according to security findings ( business logic unchanged )
- Adjusted sync logic to create jobs only for related BomLifecycles
- Spring core updated from 6.0.14 to 6.0.16
- Springboot updated from 3.1.6 to 3.1.7
- Updated Angular and its dependencies from 15.2.8 to latest version 16 release
- Implemented asset publisher component functionality
- Updated postgres to version 15.4
- Update Spring version to 3.1.8
- Migrated from okhttpclient (feign implementation) to resttemplate.
- Refactored rest templates
- Bumped jacoco-maven-plugin from 0.8.8 to 0.8.11
- Bumped cypress-io/github-action from 6.6.0 to 6.6.1
- Bumped tj-actions/changed-files from v41 to v42
- Fixed some response type descriptions within swagger documentation
- Error handling when publishing assets

### Removed

## [10.2.1 - 23.01.2024]

### Added
- Test Release

## [10.2.0 - 22.01.2024]

### Added
- Import Data Service for data provisioning
- Added UI to publish own assets
- Sequence Diagrams for Data Provisioning Flow
- Added User Manual for Data import
- Added github action to connect pull request with github issue
- Added bpn validator to bpn edc mapping
- Added new /irs/job/callback endpoint to handle irs job finished callback
- Added Asset import info to parts table and parts detail view
- SubmodelServerService for calling submodel server with feign client
- Added GET /policies endpoint to retrieve accepted policies
- Added POST assets/publish endpoint to publish transient assets

### Changed
- Fixed security findings
- Rework GET alerts and investigations endpoint to POST to send a request body
- Fixed deadline overlap issues for Q-investigations in update menu
- Fixed sorting of asPlanned parts
- Fixed unlimited filter parameter length by setting it to 1000 characters max
- Adjusted cypress tests to new cancellation flow
- Fixed bug where applying a filter in one table erroneously affected sorting in the other table
- Moved UpdateAssetRequest to tx-models module
- updated Compatibility Matrix
- Irs Jobs are now created with callback parameter set to new /irs/job/callback endpoint
- Upgraded irs version from 6.12.0 to 6.13.0
- Switched from OAuth2.0 to API Key authentication for IRS API Requests
- switched from json-schema-friend:0.12.3 to json-schema-validator:5.4.0 for import file validation
- Moved logic for active notifications to frontend
- Updated com.github.spotbugs:spotbugs-maven-plugin from 4.7.3.0 to 4.8.3.0
- Updated actions/github-script from 5 to 7
- Updated org.apache.maven.plugins:maven-surefire-plugin from 3.1.2 to 3.2.5
- Updated org.apache.maven.plugins:maven-failsafe-plugin from 3.0.0-M8 to 3.2.5
- Updated aquasecurity/trivy-action from 0.16.0 to 0.16.1
- Updated actions/upload-artifact from 3 to 4
- Updated github/codeql-action from 2 to 3
- Updated actions/download-artifact from 3 to 4actions/download-artifact from 3 to 4
- Updated com.nimbusds:nimbus-jose-jwt from 9.37.1 to 9.37.3
- Changed some java implementations according to security findings ( business logic unchanged )
- Updated createIrsPolicyIfMissing() method to validate policies based on rightOperand values rather than policyIDs

### Removed
- Shell descriptor entity with underlying logic

## [10.1.0 - 22.12.2023]
### Added
- Added an option for testdata upload in Argo Workflow
- Validation for import data
- GitHub action that ensures an up-to-date CHANGELOG.md

### Changed
- Restricted datefield on investigation creation to be only clickable and not editable
- Removed duplication of request notification component and combined it into a reusable component
- bump aquasecurity/trivy-action from 0.14.0 to 0.16.0
- bump actions/setup-python from 4 to 5
- bump mikefarah/yq from 4.40.2 to 4.40.5
- bump actions/setup-java from 3 to 4
- bump org.apache.maven.plugins:maven-jxr-plugin from 3.3.0 to 3.3.1
- bump org.apache.maven.plugins:maven-checkstyle-plugin from 3.3.0 to 3.3.1
- bump schedlock.version from 5.9.1 to 5.10.0
- fixed bug where filter was reset when sorting filtered notifications
- redesigned notification status confirmation modal
- bump irs version from 6.9.1 to 6.12.0
- moved rights and roles matrix from arc42 doc to administration guide
- Moved accepted policy to helm environments
- Request IRS policy store to accept IRS policies in addition to our own policy

### Removed
- Removed registry lookups feature

## [10.0.0 - 12.12.2023]
### Added
- new filtering capabilities ( receivedQualityAlertIdsInStatusActive, sentQualityAlertIdsInStatusActive, receivedQualityInvestigationIdsInStatusActive, sentQualityInvestigationIdsInStatusActive )
- Validation check if table-settings correct and reset on invalid state
- Added Api-Input in Argo Workflow to fix bugs
- Added implementation for cucumber tests for quality investigations
- Added implementation of cypress tests for quality alerts
- Separation of auto complete mechanism (selected / searched elements)
- Added new step definition for cucumber tests "I use assets with ids {string}" allowing to specify assets used for notification creation
- Added autocomplete endpoints for notifications
- Added BPN column to parts table
- Emit change check to observables in frontend
- Added an Entity Relationship Model (ERM) into the Architecture Documentation to visually represent our tables and their relationships.
- Added a step for testing input and included an option for a hard refresh in Argo Workflow
- Added manufacturer_id to assets_as_planned
- Added local filtering and auto complete for notifications
- Added a drag-and-drop option for JSON files and an endpoint to receive them

### Changed
- Filter configuration for tables to be resuable and easy to adapt
- Realigned some mappings e.g. (manufacturer / manufacturerName) to be more clear
- Updated mikefarah/yq from 4.35.2 to 4.40.2
- Upgraded maven-checkstyle-plugin from 3.3.0 to 3.3.1
- Upgraded nimbus-jose-jwt from 9.31 to 9.37.1
- Upgraded maven-install-plugin from 3.0.1 to 3.1.1
- Upgraded json-unit-assertj from 2.38.0 to 3.2.2
- Upgraded asciidoctorj-diagram from 2.2.9 to 2.2.13
- Cucumber test steps for creating notifications no longer support default assetId when no asset is provided with previous step
- Upgraded the Upload_Testdata job in Argo Workflow to fix bugs
- Auto format for frontend source code applied
- Updated user-manual for Parts filtering autocomplete functionality
- Fixed issue when requesting autocomplete api endpoints with no size provided
- Default pagination size to 50.
- Split up bpn column in notification table views to show bpn and name separately
- Changed detailed part view action from clicking on list item to a menu action column
- Changed FE fieldName mapping to fix bug for properties catenaxSiteId and function
- Fix of global search field
- Parts autocomplete API now is case insensitive when using "startWith" parameter
- changed mapping for manufacturerName when syncing assets_as_planned so it is being resolved when assets are resolved
- Behaviour of auto complete toggle selections
- Updated springboot version from 3.1.3 to 3.1.6
- updated spring core version from 6.0.13 to 6.0.14
- Update tomcat-embed-websocket from 10.0.15 to 10.0.16
- Update logback-classic and logback-core version to 1.4.14 to mitigate high finding
- Fixed sorting on empty notification tables

### Removed
- removed asset filters ( qualityInvestigationIdsInStatusActive, qualityInvestigationIdsInStatusActive )
- Removed Cucumber tests steps for creating alerts with two parts as new step definition is enough for the same feature

## [9.0.0 - 05.12.2023]
### Changed
- Upgraded irs-client library from 1.4.1-SNAPSHOT to 1.4.1

### Known knowns

- Backend/Frontend [TRACEFOSS-2728]: Investigations / Alerts: Transition of the message status will take some time. If it fails the user will not be informed.

## [9.0.0-rc3 - 27.11.2023]
### Added
- DEPENDENCIES_FRONTEND, SECURITY.md, NOTICE.md, LICENSE file to frontend docker image
- Added a step-by-step guide to register a server in pgAdmin in the database dump README
- Documentation about technical users
- Added new dashboard layout and additional widgets

### Changed
- Fixed helm repository path for backend & frontend (wrong prefix)
- Refactored dashboard response
- Updated user manual
- Autocomplete endpoints changed owner String type param to Owner for input validation and sql injection prevention
- Autocomplete endpoints repository uses now criteria api rather than native query
- Fixed several bugs in local filtering of the parts table

### Removed
- apk upgrade in docker image built as requested by TRG 4.02

## [9.0.0-rc2 - 15.11.2023]
### Added
- Cypress Login to E2E Environment to enable cypress e2e tests.
- Fixed bug in argo workflow which allows to successfully run on INT-A/INT-B
- database dumps for environments A and B, along with a README guide for database recovery.
- New job named 'print_environment' to the Argo-workflow that prints the selected environment to the GitHub Step Summary.
- Added NOTIFICATION_COUNT_EQUAL filter strategy for Assets as built Specifications
- Added new supported filter for notifications assetId that allows filtering alerts and investigations by assetId
- Added autocompletion and local filter selection on parts table
- Fixed bug in argo workflow which allows to successfully upload testdata
- No logging option for cypress

### Changed
- Fixed table-settings reset bug
- Changed datepicker in FE to date range picker
- Fixed name of veracode backend job
- Bump jetty-http from 11.0.15 to 11.0.17
- Assets response have now list of notification ids rather than count of existing notifications
- Frontend adapt to backend api changes for activeAlerts and activeInvestigations
- Reconfigured all docker images user settings
- Adapted memory / cpu requests and limits in default values helm file
- Fixed textarea field for dialog.
- Removed duplicated cancel buttons from investigation and alerts workflows

- Migrate to not deprecated methods in HTTP security
- Bump actions/setup-node@ from v3 to v4
- Bump helm/chart-releaser-action from v1.5.0 to v1.6.0
- Bump aquasecurity/trivy-action from 0.12.0 to 0.14.0
- Bump cypress-io/github-action from v6.5.0 to v6.6.0
- Bump spring-core version from 6.0.12 to 6.0.13
- Bump compiler-plugin version 3.10.1 to 3.11.0
- Bump commons-io version 2.13.0 to 2.15.0
- Update IRS-helm version from 6.8.0 to 6.9.1
- Update EDC from 0.5.0 to 0.5.3
- Added owner param to distinctFilterValues endpoints

### Removed

## [9.0.0-rc1 - 03.11.2023]

### Added
- Added Table columns settings in part tables to show/hide/reorder table columns
- new endpoints supporting filtering feature for investigations and alers api/investigations api/alerts
- Added support for aspectmodel traction battery code
- Added missing translations
- support for date ranges BEFORE_LOCAL_DATE and AFTER_LOCAL_DATE providing both will cause filter result to return only relevant date ranges
- added supported searchCriteriaFieldsMappers for investigations, alerts, assetsAsBuilt and assetAsPlanned related endpoints
- added cache busting to build output of FE application
- added Argo Github-Action
- handling for invalid LocalDate string provided in filterCriteria for date related filtering
- support for filtering join tables for Specification searchCriteria
- added PR comment in case of HIGH/CRITICAL dependency check findings
- Functionality to indicate that no Dependency Check findings occur in a PR
- Badge to show successful Dependency Check status

### Changed
- Updated user manual to reflect the table column settings feature
- Fixed a bug which removed all parts asBuilt selection at once when creating notifications
- Changed Filter to support Logical operator (AND,OR) on searchCriteria
- Adapt frontend to use the changed filter logic with the correct operator per use case
- Reworked business logic of /registry/reload to always sync all assets
- Only include configured severities into report
- Shedlock version from 5.7.0 to 5.9.1
- Swagger Annotation Version from 1.6.11 to 1.6.12
- Testcontainer Postgresql Version from 1.19.0 to 1.19.1
- Bump @babel/traverse from 7.20.13 to 7.23.2 in frontend
- distinctFilterValues endpoints now support startWith parameter that will cause result to contain only suggestions starting with given string
- changed qualityNotification filtering changed from side to channel as response field name
- changed assetAsBuilt filtering manufacturerId to businessPartner
- IRS-Client-Lib from 1.2.1-SNAPSHOT to 1.4.0
- Decoupled dependency check in a separate GitHub action
- Mitigated Dependency Check findings

### Removed
- Removed &filterOperator=AND from filtering requests
- Removed no longer needed endpoints api/investigations/created, api/investigations/received, api/alerts/created, api/alerts/received

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
- Bump versions in frontend dependencies

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

## [6.0.1] - 2023-10-23
### Added
- All elements from 6.0.1-rc1,rc2,rc3,rc4


## [6.0.1-rc4]
### Added

### Changed
- updated IRS helm chart from 6.6.1 to 6.7.2
- updated policy related logic to reflect IRS changes

### Removed

## [6.0.1-rc3] - 2023-08-31
### Added


### Changed

- Updated irs-registry-client from 1.1.0-SNAPSHOT to 1.2.0-SNAPSHOT
- Updated irs-helm from 6.4.1 to 6.5.0

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
- Changed transfer notification logic not to break iteration loop when sending notifications to bpn with more than 1 connector endpoints
- added handling for null manufacturerName in IrsJobResponse, if null is passed it is replaced with "UNKNOWN_MANUFACTURER"

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
- Logic of commitId retrieval by frontend has changed
- Readme / Docker Notice information as required in TRGs
- Removed references to GitHub registry and added docker hub for tractusx instead
- Spring Boot Update from: 3.0.7 to 3.1.2
- Spring Core Update from: 6.0.8 to 6.0.11

### Removed
- unused classes and methods

### Known knowns

- Backend [TRACEFOSS-1458]: AdminView: No validation of BPN for BPN  EDC URL mapping
- Backend [TRACEFOSS-589]: Backend API access without login returns incorrect HTTP status code (500 instead of 401)
- Backend [TRACEFOSS-2148]: Endpoints for parts and notifications returns unsorted list
---
- Frontend [TRACEFOSS-2149]: Sorting on empty table causes unhandled error view
---
- Security [TRACEFOSS-829]: CVE Strict-Transport-Security header - The HSTS Warning and Error may allow attackers to bypass HSTS
- Security [TRACEFOSS-830]: CVE one stack trace disclosure (Java) in the target web server's HTTP response
- Security [TRACEFOSS-919]: Authorization Bypass Through User-Controlled SQL Primary Key CWE ID 566
- Security [TRACEFOSS-984]: Improper Output Neutralization for Logs CWE ID 117
- Security [TRACEFOSS-1313]: Using components with known vulnerabilities
- Security [TRACEFOSS-1314]: Open Redirect - host header injection
- Security [TRACEFOSS-1315]: No additional authentication component (MFA) during login process
---
- Environment [TRACEFOSS-2164]: HTTP Requests for syncing the submodel server inoperable~~


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
