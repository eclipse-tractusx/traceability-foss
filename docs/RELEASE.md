<div style="display: flex; align-items: center;justify-content: center;align-content: center;">
   <img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/trace-x-logo.svg" alt="Product Traceability FOSS Release Guide" style="width:200px;"/>
   <h1 style="margin: 10px 0 0 10px">Product Traceability FOSS Release guide</h1>
</div>

## Release an app
Prerequisite:
Make sure eclipse / catena git repositories are in sync

1) Decide which version will be incremented. Following shows example for 1.0.0
2) Create and Checkout release branch on catena /release/1.0.0
4) Edit changelog: Align the new version (1.0.0) with the changes and add new UNRELEASED section
5) Edit /charts/traceability-foss/CHANGELOG.md
- Add an Entry for an incremented (patch) version (1.0.0 -> 1.0.1)
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
- Edit the app release and set checkbox to latest release
13) Repeat step 7 to 12 for tractus-x: [GitHub Releases page](https://github.com/eclipse-tractusx/traceability-foss/releases)
14) Merge release branch into catena main branch
15) Sync catena and eclipse main branch

