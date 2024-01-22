<div style="display: flex; align-items: center;justify-content: center;align-content: center;">
   <img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/trace-x-logo.svg" alt="Product Traceability FOSS Release Guide" style="width:200px;"/>
   <h1 style="margin: 10px 0 0 10px">Product Traceability FOSS Release guide</h1>
</div>

## Release an app
Prerequisite:
Make sure eclipse / catena git repositories are in sync

1) Decide which version will be incremented. Following shows example for 1.0.0
2) Create a release branch on catena /release/1.0.0
3) Check out release branch from catena /release/1.0.0
4) Edit changelog: Align the new version (1.0.0) with the changes and add new UNRELEASED section
5) Edit /charts/traceability-foss/CHANGELOG.md
- Make sure that an entry is existing for the helm-chart version you want to create
5) Push onto /release/1.0.0 catena and eclipse
6) Open Release App Page Catena: https://github.com/catenax-ng/tx-traceability-foss/releases
7) Draft a new release
8) On dropdown choose a tag - use the version 1.0.0 (Create new tag will appear - select it)
9) On dropdown target use your /release/1.0.0
10) Title = Version of app -> 1.0.0
11) Description = Changelog Content of app
12) Checkbox set as latest release
- Verify that github action release generation has been triggered
- Verify that an automatic pull request has been opened (Prepare Helm release for next version)
- Validate that the versions within that pull requests are correct
- Merge pull request
- Open the github action for helm release generation: https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/helm-chart-release.yaml
- Execute it from main branch
- Validate that the helm charts release has been generated within the release page
13) Repeat step 7 to 12 for tractus-x: [GitHub Releases page](https://github.com/eclipse-tractusx/traceability-foss/releases)
14) Edit /release/1.0.0 branch: package.json version field with your version 1.0.0
15) Open /charts/Chart.yaml and edit the fields:
- version, frontend.version, backend.version (set only one minor version higher) - you need to verify that this version does not exist, see release page
- appVersion (needs to be your app version) -> 1.0.0
16) Open /charts/backend/Chart.yaml and set same version / appVersion
17) Open /charts/frontend/Chart.yaml and set same version / appVersion
18) Push to catena and eclipse
19) Open Github Action on Catena and run action ON release/1.0.0: https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/helm-chart-release.yaml
- Verify that helm-chart release has been generated and is on release page
20) Edit the app release and set checkbox to latest release
21)Open Github Action on Eclipse and run action ON release/1.0.0: https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/helm-chart-release.yaml and run on your release branch
- Verify that helm-chart release has been generated and is on release page
22) Edit the app release and set checkbox to latest release
23) Merge release branch into catena main branch
24) Sync catena and eclipse main branch

Please update the [CHANGELOG.md](https://github.com/eclipse-tractusx/traceability-foss/blob/main/CHANGELOG.md) file with release changes that follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) format.

