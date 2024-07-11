<div style="display: flex; align-items: center;justify-content: center;align-content: center;">
   <img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/trace-x-logo.svg" alt="Product Traceability FOSS Release Guide" style="width:200px;"/>
   <h1 style="margin: 10px 0 0 10px">Product Traceability FOSS Release guide</h1>
</div>

# Release Numbering

Trace-X using semantic versioning three-part version number. https://semver.org/

## Major Release

* If there are any incompatible API changes.
* Changes with high impact
* Contains new features and changes with critical business impact.
* Full regression tests are covered.

## Minor Release

* Add functionality in a backwards compatible manner
* Features (backwards compatible has to be ensured)
* Minor release does not add features or changes with critical business impact.
* Regression impact should be low.
* No training effort necessary
* INT Test environment should be stable. No changes on depending on systems required. No changes on consumer side necessary.
* Operational risks should be low.

## Bug fix / Patch Release

* Backwards compatible bug fixes
* Bug fixes and Hotfixes
* Covers Bug fixes and changes with no critical business impact.
* No changes on depending on or consuming systems required.
* INT Test environment should be stable. No changes on depending on systems required. No changes on consumer side necessary.

## Tag

* Defined software state.

# Release an app

### Prerequisite:

- Make sure the Eclipse Git repository is in sync
- Before releasing Trace-X, it is required to go through the [IRS Library Release](#irs-library-release) steps

### IRS Library Release

The goal is to not use a -SNAPSHOT version in the Trace-X Release.

1) Check if the irs-registry-client version has a -SNAPSHOT suffix:  [IRS Repo](https://github.com/eclipse-tractusx/item-relationship-service/blob/f731e2e7403b738d516a7a25b19c756cc32b04f3/pom.xml#L76)
2) If yes, continue with the next steps. If no, skip to the [Trace-X Release process](#trace-x-release-process)
3) Click on the [Update irs-registry-client Version workflow](https://github.com/eclipse-tractusx/item-relationship-service/actions/workflows/update-registry-library.yaml).
4) Select "Run workflow" select the type of version increment major, minor or patch (Can be adjusted on generated PR branch). Check the box to remove the snapshot. Click on "Run".
5) A pull request (name: Update irs-registry-client to "Version") will be generated in which you have to make sure the irs lib version is now correct (change it manually if necessary).
6) Merge the generated Pull request
7) The GitHub action [Upload to Central Maven Registry ](https://github.com/eclipse-tractusx/item-relationship-service/actions/workflows/maven-deploy.yaml) will automatically release the irs-registry-client library with the new version defined in step 4

### Trace-X Release process

1) Decide which to which version the release trace-x will be incremented. Following shows example for releasing a version 1.0.0
2) Create and checkout release branch /release/1.0.0. The name must always be exactly `/release/<releaseVersion>`.
3) Optional: If [IRS Library Release](#irs-library-release) was needed:
    1) If the action of [IRS Library Release](#irs-library-release) step 7 was executed successfully
    2) Update <irs-client-lib.version> in the above created release
4) Edit changelog: Align the new version (1.0.0) with the changes and add a new UNRELEASED section
5) Edit /charts/traceability-foss/CHANGELOG.md
6) Add an Entry for an incremented (patch) version (1.0.0 -> 1.0.1)
7) Update the [Compatability Matrix](https://github.com/eclipse-tractusx/traceability-foss/blob/main/COMPATIBILITY_MATRIX.md) with a new entry for the release version
8) Push onto /release/1.0.0
9) Open releases page: https://github.com/eclipse-tractusx/traceability-foss/releases
10) Draft a new release
11) On dropdown choose a tag - use the version 1.0.0 (Create new tag will appear - select it)
12) On dropdown target use your /release/1.0.0
13) Title = Version of app -> 1.0.0
14) Description = Changelog Content of app
15) Checkbox set as latest release
16) Verify that GitHub action [Release](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/release.yaml) generation has been triggered
17) Verify that this pull request has been opened: `Prepare Helm release for next version` and merge it
18) Execute GitHub action [Update OpenAPI spec](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/update-openapi-spec.yml)
19) Verify that this pull request has been opened: `chore: update OpenAPI spec` and merge it
20) Merge release branch into main (when merging make sure to restore release branch since it should stay)
21) Open the GitHub action for helm release generation: https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/helm-chart-release.yaml
22) Execute it from main branch
23) Validate that the helm charts release has been generated within the release page
24) Create a new branch from release/1.0.0 and name it release/helm-environments-1.0.0 (helm app version not chart version)
25) Create a message in the Trace-X channel of the Eclipse Foundation Chat to notify the community about the new release (add a link to the tractus-x release)
