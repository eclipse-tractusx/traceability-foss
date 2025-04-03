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
6) A pull request (name: Update irs-registry-client to "Version") will be generated in which you have to make sure the irs lib version is now correct (change it manually if necessary).
   1) If the pipeline fails, create a PR manually with the irs-registry-client set to the correct version.
7) Merge the generated Pull request
### TODO currently irs cannot publish library for cofinity project
7) The GitHub action [Upload to Central Maven Registry ](https://github.com/Cofinity-X/item-relationship-service/actions/workflows/maven-deploy.yaml) will automatically release the irs-registry-client library with the new version defined in step 4

### Trace-X Release process

1) Decide which to which version the release trace-x will be incremented. Following shows example for releasing a version 1.0.0-cfx-1
2) Create and checkout release branch /release/1.0.0-cfx-1. The name must always be exactly `/release/<releaseVersion>-cfx-<version>`.
3) Optional: If [IRS Library Release](#irs-library-release) was needed:
    1) If the action of [IRS Library Release](#irs-library-release) step 7 was executed successfully
    2) Update <irs-client-lib.version> in the above created release
4) Optional: If there have been changes to the Helm charts
   1) Set the version in Chart.yaml in charts/ to one higher than the previous cfx-version. The previous version can be found in the git history. Also do this for the frontend and backend subcharts
   2) In the main Chart.yaml file, update the version of the frontend and backend dependencies to the version created in the previous step
5) Edit changelog: Align the new version (1.0.0-cfx-1) with the changes and add a new UNRELEASED section
6) Update the [Compatability Matrix](https://github.com/Cofinity-X/traceability-foss/blob/main/COMPATIBILITY_MATRIX.md) with a new entry for the release version
7) Push onto /release/1.0.0-cfx-1
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

# Provide Update to Upstream Repository and Publish Package to Registry

## 1. Check Merged Changes in IRS

1. Verify if the changes are merged in the IRS.
2. Update the changelog accordingly.

> **Note:** For more details, refer to [Traceability FOSS Docs/Release-cfx.md](#irs-library-release).

## 2. Manage Remote Repositories in IntelliJ

1. Add the entry repository as `remote` in your remote settings.
2. Ensure the `entry` repository's `main` branch is checked out.

## 3. Create a Branch and Cherry-Pick Commits

1. Create a new branch from the `entry` repository's `main` branch.
2. Cherry-pick the commit you want to merge from `cfx irs` repo `main` branch.

## 4. Push Changes to Entry Repository

1. Push your new branch to the `entry` repository.

## 5. Create a Pull Request Upstream

1. Navigate to the upstream repository.
2. Create a Pull Request (PR) using the "compare across forks" option for the branch created in step 3 from the `entry`.
3. Request a review from a contributor of the upstream repository.
4. Merge the branch after approval.

## 6. Create Release Version and Run Workflow

1. IRS registry Client Version Action
2. Determine the nature of the change introduced (e.g., major, minor, patch).
3. In the Workflow Check create release version.
4. Run the workflow (to be executed by an Eclipse committer).
5. A PR will be created and reviewed by an Eclipse committer.
6. The package will be published to Maven Central.

## 7. Sync Back Repositories

1. Access the sync enablement services workflow:

    - [Sync Enablement Services Entry Repos](https://github.com/Cofinity-X/entry-pipelines/actions/workflows/sync-enablement-services-entry-repos.yaml)

2. **Note:** Contact the infra team for permission if required.

## 8. Cleanup in Cofinity-X IRS Repository

1. Check out the `cfx irs` repository.
2. Create a `cleanup` branch.
3. Revert the `cfx` contributed commit on this branch.
4. Create a PR to merge into `main`.

## 9. Merge Entry Main into Cofinity IRS Main

1. Create a new branch from `cfx irs main`.
2. Merge `entry/main` into your branch.
3. Push the changes to `origin/main`.
4. Create a PR for the merge.

## 10. Update Version in Trace-X Repository

1. Navigate to the Trace-X repository.
2. Update the version in the root `pom.xml` file of the repository.
