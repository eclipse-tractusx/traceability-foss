<div style="display: flex; align-items: center;justify-content: center;align-content: center;">
   <img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/trace-x-logo.svg" alt="Product Traceability FOSS Release Guide" style="width:200px;"/>
   <h1 style="margin: 10px 0 0 10px">Product Traceability FOSS Release guide</h1>
</div>

## Release an app

Prerequisite:
Make sure eclipse / catena git repositories are in sync

1) Decide which version will be incremented. Following shows example for 1.0.0
2) Create a release branch in [IRS](https://github.com/eclipse-tractusx/item-relationship-service) and update the <irs-registry-client.version> to the desired version
3) Create a merge request to [IRS](https://github.com/eclipse-tractusx/item-relationship-service) with the above created release branch to release the irs-registry-client in a none snapshot version
4) Create and Checkout release branch on catena /release/1.0.0
5) Update <irs-client-lib.version> in the above created release branch to the created one in step 3.
6) Edit changelog: Align the new version (1.0.0) with the changes and add new UNRELEASED section
7) Edit /charts/traceability-foss/CHANGELOG.md
8) Add an Entry for an incremented (patch) version (1.0.0 -> 1.0.1)
9) Push onto /release/1.0.0 catena and eclipse
10) Open Release App Page Catena: https://github.com/catenax-ng/tx-traceability-foss/releases
11) Draft a new release
12) On dropdown choose a tag - use the version 1.0.0 (Create new tag will appear - select it)
13) On dropdown target use your /release/1.0.0
14) Title = Version of app -> 1.0.0
15) Description = Changelog Content of app
16) Checkbox set as latest release
17) Verify that github action [Release](https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/release.yaml) generation has been triggered
18) Verify that an automatic pull request has been opened (Prepare Helm release for next version)
19) Validate that the versions within that pull requests are correct
20) Merge pull request (Prepare Helm release for next version)
21) Merge release branch into main (when merging make sure to restore release branch since it should stay)
22) Open the github action for helm release generation: https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/helm-chart-release.yaml
23) Execute it from main branch
24) Validate that the helm charts release has been generated within the release page
25) Create a new branch from release/1.0.0 and name it release/helm-environments-1.0.0 (helm app version not chart version)
26) Repeat step 8 to 23 for tractus-x: [GitHub Releases page](https://github.com/eclipse-tractusx/traceability-foss/releases)
27) Sync catena and eclipse main branch
28) Create a message in the Trace-X channel of the Eclipse Foundation Chat to notify the community about the new release (add a link to the tractus-x release)
