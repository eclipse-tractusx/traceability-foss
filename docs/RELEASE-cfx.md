
# Release Guide for Cofinity-X

## General release conventions / guidelines

Please find [Release Guidelines Upstream](https://github.com/Cofinity-X/traceability-foss/blob/main/docs/RELEASE.md)

### Prerequisite:

- Make sure the Eclipse Git repository is in sync
- Before releasing Trace-X, it is required to go through the [IRS Library Release](#irs-library-release) steps

### IRS Library Release

The goal is to not use a -SNAPSHOT version in the Trace-X Release.

1) Check if the irs-registry-client version has a -SNAPSHOT suffix:  [IRS Repo](https://github.com/Cofinity-X/item-relationship-service/blob/main/pom.xml)
2) If yes, continue with the next steps. If no, skip to the [Trace-X Release process](#trace-x-release-process)
3) Click on the [Update irs-registry-client Version workflow](https://github.com/Cofinity-X/item-relationship-service/actions/workflows/update-registry-library.yaml).
4) Select "Run workflow" select the type of version increment major, minor or patch (Can be adjusted on generated PR branch). Check the box to remove the snapshot. Click on "Run".
5) A pull request (name: Update irs-registry-client to "Version") will be generated in which you have to make sure the irs lib version is now correct (change it manually if necessary).
6) Merge the generated Pull request
### TODO currently irs cannot publish library for cofinity project
7) The GitHub action [Upload to Central Maven Registry ](https://github.com/Cofinity-X/item-relationship-service/actions/workflows/maven-deploy.yaml) will automatically release the irs-registry-client library with the new version defined in step 4

### Trace-X Release process

1) Decide which to which version the release trace-x will be incremented. Following shows example for releasing a version 1.0.0-cfx-1
2) Create and checkout release branch /release/1.0.0-cfx-1. The name must always be exactly `/release/<releaseVersion>-cfx-<version>`.
3) Optional: If [IRS Library Release](#irs-library-release) was needed:
    1) If the action of [IRS Library Release](#irs-library-release) step 7 was executed successfully
    2) Update <irs-client-lib.version> in the above created release
4) Edit changelog: Align the new version (1.0.0-cfx-1) with the changes and add a new UNRELEASED section
5) Update the [Compatability Matrix](https://github.com/Cofinity-X/traceability-foss/blob/main/COMPATIBILITY_MATRIX.md) with a new entry for the release version
6) Push onto /release/1.0.0-cfx-1
9) Open releases page: https://github.com/Cofinity-X/traceability-foss/releases
10) Draft a new release
11) On dropdown choose a tag - use the version 1.0.0-cfx-1 (Create new tag will appear - select it)
12) On dropdown target use your /release/1.0.0-cfx-1
13) Title = Version of app -> 1.0.0-cfx-1
14) Description = Changelog Content of app
15) Verify that GitHub action [Release](https://github.com/Cofinity-X/traceability-foss/actions/workflows/cofinity-docker-image.yml) generation has been triggered
## TODO currently the pull request of the follwing github action cannot be opened because of permission errors.
16) Execute GitHub action (from main branch) [Update OpenAPI spec](https://github.com/Cofinity-X/traceability-foss/actions/workflows/update-openapi-spec.yml)
17) Verify that this pull request has been opened: `chore: update OpenAPI spec` and merge it
18) Merge release branch into main (when merging make sure to restore release branch since it should stay)
