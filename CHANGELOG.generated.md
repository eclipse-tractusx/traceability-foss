<a name="1.1.0"></a>

## [1.1.0](https://github.com/catenax-ng/tx-traceability-foss-frontend/compare/helm-charts-1.0.0...1.1.0) (2023-02-06)

### Chore

- use SONAR_PROJECT_KEY and SONAR_ORGANIZATION env vars in build
- use SONAR_PROJECT_KEY and SONAR_ORGANIZATION env vars in build
- cypress docker configuration improvements - KICS

### Features

- prepare for release 1 1 0
- adjust names of container
- disabled unit tests for customer parts
- improve pl translations and code quality
- added functionality to update status of recieved investigations
- added functionality to update status of recieved investigations
- enable notification feature

### Fixes

- temporary fix for sporadic faling of tests
- added more unit tests for format date pipe
- adding a new test for the date formatter
- interpreting the date as -- when it is NULL in backend

### Refactoring

- code refactoring and added unit tests

### Testing Code

- cypress e2e tests run by github actions + align local cypress env
- cypress e2e locally

<a name="helm-charts-1.0.0"></a>

## [helm-charts-1.0.0](https://github.com/catenax-ng/tx-traceability-foss-frontend/compare/1.0.0...helm-charts-1.0.0) (2022-12-12)

<a name="1.0.0"></a>

## [1.0.0](https://github.com/catenax-ng/tx-traceability-foss-frontend/compare/1.0.0-RC...1.0.0) (2022-12-12)

### Chore

- release 1.0.0
- change value-pen.yaml config to avoid name collisions with DEV env
- refactoring ingress.yaml tsl section

### Features

- tracefoss-866_add_van_to_application
- fixed unit tests

### Fixes

- fixed german translations
- missing translations and small changes
- new line problem in header, between unix and windows
- fixed unit tests for map

### Testing Code

- disable d3 minimap test

<a name="1.0.0-RC"></a>

## [1.0.0-RC](https://github.com/catenax-ng/tx-traceability-foss-frontend/compare/0.1.0...1.0.0-RC) (2022-12-06)

### Chore

- align with BE chart-release.yml
- align with BE deployment helm chart
- let dependabot run every wednesday night
- added changelog for release 0.1.0
- activate dependabot

### Features

- change header for eclipse movement
- used new detail info endpoint for map
- finalization of tree zoom with broken minimap
- fixed zoom communication
- added d3 zoom to tree and minimap
- add batchId to parts model
- update Polish translations and align with EN items
- improved german translation
- rename delete action in notifications to cancel
- refresh investigation data after succeeded action
- added table for registry lookups
- switch to new keycloak for int
- fixed unit tests
- changed way how table menu actions are displayed
- added distinct menues for recieved and qued and requested tab
- add approve and delete button to notification detail page
- added unit tests for close modal
- aligned endpoints with be
- added unit tests for investigations component
- added unit tests for investigation details
- update client id after argo cd update
- extend notification detail page - close
- added unit tests
- added modal component
- added child parts to part details
- changed way request investigation works as a component
- remove wip flag and implement new endpoints
- added investigation detail page
- changed dashboard count to other parts
- new action menu for table component
- make relation tree
- adapted to keycloak changes
- adapted to keycloak changes
- added workflow for docker registry release version

### Fixes

- wrong env file replacement for local aut config
- missing CANCELED and CLOSED status info
- fixed delete handling for cancel investigation
- formating of non existing translation keys
- enable run tests github action for all PRs
- fixed broken mock and translation
- sonarqube code coverage with karma
- submittedMock
- fixed broken code after update
- refresh supplier parts list on investigation start
- added hardening for relations
- minimap translate

### Refactoring

- add only legal documents into this push request
- adjust code for moving to eclipse repository
- adjust code for moving to eclipse repository
- adjust code for moving to eclipse repository
- adjust code for moving to eclipse repository
- adjust code for moving to eclipse repository
- adjust code for moving to eclipse repository
- adjust code for moving to eclipse repository
- fix broken tests and refactor of cancel action
- changed way menu items behave
- cleanings after code review
- cleanings after code review
- some clean up - close action translation
- some clean up - close action on detail page
- some clean up
- app-delete-notification-modal
- app-approve-notification-modal
- cleaning up using menuActionsConfig
- cleaning up CloseNotificationModalComponent
- rename closeModalComponent
- replace jest with karma ([#105](https://github.com/catenax-ng/tx-traceability-foss-frontend/issues/105))
- rename investigation to more generic notification component
- add return types
- rename cta-notification to cta-snackbar
- renaming of notification to toast
- tree cleanup
- improve tests and translations

### Testing Code

- fix broken tests require wip role
- temporary disable minimap and relations facade tests
- restore valid test setting - broken after merge with main
- tests for modal component - improvements
- tests for modal component - improvements
- tests for modal component - onEsc
- tests for modal component

### Work In Progress

- temporary disable notification feature
- make closeModal works as list action
- move menuActionsConfig outside
- introduce close-modal-component

<a name="0.1.0"></a>

## [0.1.0](https://github.com/catenax-ng/tx-traceability-foss-frontend/compare/helm-charts-0.1.0...0.1.0) (2022-09-21)

<a name="helm-charts-0.1.0"></a>

## [helm-charts-0.1.0](https://github.com/catenax-ng/tx-traceability-foss-frontend/compare/product-traceability-foss-frontend-0.1.0...helm-charts-0.1.0) (2022-09-21)

### Features

- added new logo and fav icon
- prepare investigation mock for featuer impl

<a name="product-traceability-foss-frontend-0.1.0"></a>

## product-traceability-foss-frontend-0.1.0 (2022-09-20)

### Chore

- activate dependabot
- code cleanup and reuse of architecture
- cleanup code smells
- fix code smells
- improved imports
- clean up after code review
- code cleanup and removed unused code
- cleaned up dependencies
- removed comments and fixed eslint

### Documentation

- fix NOTICE.md
- add NOTICE and SECURITY files
- angular template convention
- fixed typos
- Updated and improved documentation

### Features

- update documents for release
- added installation file with instructions
- renamed app
- re enable other parts
- align fe with be for supplier parts
- generate changelog
- highlight parts if notification active
- added dashboard investigation widget
- added fixes for quality gates and relations
- renamed veracode.yml to .yaml so it complies with checks
- helm chart release ([#76](https://github.com/catenax-ng/tx-traceability-foss-frontend/issues/76))
- improve performance throughout appication
- added wip role for notification and role check to table component
- added better test data for local mock
- added close functionality to sidenav button
- create app-scroll-with-shadow
- cleanuo copyright headers
- added pagination for parts
- improved quality of life for tree component
- added css to make sidenav more responsive
- added unit tests for select component
- add shadow to table when scroll appear
- repositioned sidenav and placed it in private layout
- new quality type component and custom renderer for dopdown options
- adjusted fe to be implementation for quality investigation creation
- added new display functionality for quality type
- align translations
- address review
- added unit tests and updated translation
- added idicator if name fails to load
- added unit tests
- added trivy security scan engine
- fix textarea tests
- refactor textarea
- renaming of other parts so it aligns with naming conventions
- code improvements and reuse of functionality
- added functionality to start investigation from my parts
- improvements after code review
- reused components and architecture throughout application
- extend notification to support action
- added functionality to request investigations for other parts
- refactor investigations
- refactor investigations
- refactor
- implement investigations inbox
- refined and updated unit tests
- added other parts list view
- added multi select feature to table component
- cover dashboard integration with tests
- add more exclusions for sonar
- added unit tests for minimap
- add new line to test workflow
- debug sonar-scan workflow as a part of test workflow
- debug sonar-scan workflow to wait for Tests workflow
- update sonar-scan workflow to wait for Tests workflow
- cache test-report.xml between workflows
- update sonar-scan workflow to depend on test workflow
- code coverage for sonar cloud
- integrate dashboard API calls
- code improvements and bug fixing
- added minimap functionality for tree
- aligne FE implementation with BE implementation
- improved scrolling in tree
- added translation for map component
- improved iso to coordinate mapping
- added functionality to zoom in and out of tree
- added unit test, code cleanup, code improvements
- change static part count
- added map view for parts per country
- ui improvements for tree
- added functionality to change quality type for part
- added pagination for parts
- fe improvements
- added docker environment variables
- improved Docker file for multi-environment use
- improve FE usability and design
- change api url for int testing
- add dev environment values for argo cd
- make docker image configurable
- added unit tests
- integrate api
- adapted catena-x styling
- add basice roles support
- updated "about" text in de+en
- added relation view for parts
- added translations for german and part detail
- added unit tests for parts and detial parts
- add language selector
- i18n support
- added detail view for parts
- changed build config to mock with auth
- changed build config to mock with auth
- updated keycloak and added localAuth start option
- updated fe endpoint according to be description
- added 2 further columns for parts
- added new page for parts. Added table components
- add veracode scan and upload for main branch
- replaced svg-icon with material icon
- helm chart cleanup
- align helm chart with other fe repo
- fixe hel chart
- add hotel budapest helm chart
- Added github bages to readme and renamed file
- added veracode github action
- Added github action for testing on PR and push to main
- changed aria-label usage in button and removed unused code
- added new material theme and updated button component
- chenged styling to catena-x
- Changed folder structure and added alias
- Avatar component improvements
- added Mock and cleand up application
- simple mock setup
- update packages
- Initial project setup

### Fixes

- fixed error on dashboard
- fixed sonar issues
- added hardening for pagination pages
- fixed findings from kcis
- add dependencies
- use maplibre instead of mapbox
- not-standalone parts tree size
- minimap resize
- relations centering
- fixed broken unit tests
- tree initial centering
- part detail a11y
- remove resize icon from the map
- textarea behaviour
- table responsivness
- table responsivness
- headers in scss
- sonar issues
- update headers
- address sonar issues
- flickering toasters
- quality type now updates in table when changed
- fixed broken unit tests
- subcribtions in test
- fixed broken tree view on 0 height
- updated gitignore to ignore test report
- changed scrolling speed in tree and fixed double event for button
- fixed missing quality type
- removed styling for mat-spinner
- fixed wrong imports
- fixing first pr push to docker fails
- loading issue of tree in detail view and tree improvements
- using own certificate issuer
- removed commented out code
- try fixing ssl issue
- fixed ssl certificate issue
- fixed certificate issue
- roles fetching
- ingress https redirect
- ingress redirect
- fixed sizing issue for relation view in part-detail
- fixes after pr review
- apy-service.yaml
- ingress.yaml
- changed url for certificates
- fix certificate issue on INT
- local auth config
- github actions
- code fixes after code review
- fixed unit tests after translation change
- layout navigate
- fix parts test
- reverted to dev env because keycloak is not configured
- code clean up after review
- fixed display issue for view controll
- hotel budapest deployment
- hotel budapest deployment
- hotel budapest deployment and GH workflow
- added test alias for shared index file
- fixing github action config

### Refactoring

- improve custom protocols
- add todo
- remove unused imports
- changed usage of spys in unit tests
- address comments
- introduce auto format pipe
- datapage name
- address comments
- refactor button
- rename i18n pipe
- i18n implementation
- add enabled button test
- add unit tests
- add precommit hook
