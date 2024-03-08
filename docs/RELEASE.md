<div style="display: flex; align-items: center;justify-content: center;align-content: center;">
   <img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/trace-x-logo.svg" alt="Product Traceability FOSS Release Guide" style="width:200px;"/>
   <h1 style="margin: 10px 0 0 10px">Product Traceability FOSS Release guide</h1>
</div>

## Release an app

Prerequisite:
Make sure eclipse / catena git repositories are in sync

1) Decide which version will be incremented. Following shows example for 1.0.0
2) Create a release branch in [IRS](https://github.com/eclipse-tractusx/item-relationship-service) named release/trace-x-irs-client-lib-release
3) Remove the "-SNAPSHOT" suffix from <irs-registry-client.version> in the root pom.xml - if there is no snapshot version, you can skip the release of the irs-registry-client library
4) Create a merge request to the [IRS](https://github.com/eclipse-tractusx/item-relationship-service) main branch with the above created release branch
5) After merge, the release branch must be deleted
6) In [IRS](https://github.com/eclipse-tractusx/item-relationship-service), the gitHub action [Upload to Central Maven Registry ](https://github.com/eclipse-tractusx/item-relationship-service/actions/workflows/maven-deploy.yaml) will release the irs-registry-client library with the version defined in step 3
7) Create and Checkout release branch on catena /release/1.0.0
8) Update <irs-client-lib.version> in the above created release branch to the created one in step 3.
9) Edit changelog: Align the new version (1.0.0) with the changes and add new UNRELEASED section
10) Edit /charts/traceability-foss/CHANGELOG.md
11) Add an Entry for an incremented (patch) version (1.0.0 -> 1.0.1)
12) Push onto /release/1.0.0 catena and eclipse
13) Open Release App Page Catena: https://github.com/catenax-ng/tx-traceability-foss/releases
14) Draft a new release
15) On dropdown choose a tag - use the version 1.0.0 (Create new tag will appear - select it)
16) On dropdown target use your /release/1.0.0
17) Title = Version of app -> 1.0.0
18) Description = Changelog Content of app
19) Checkbox set as latest release
20) Verify that github action [Release](https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/release.yaml) generation has been triggered
21) Verify that an automatic pull request has been opened (Prepare Helm release for next version)
22) Validate that the versions within that pull requests are correct
23) Merge pull request (Prepare Helm release for next version)
24) Merge release branch into main (when merging make sure to restore release branch since it should stay)
25) Open the github action for helm release generation: https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/helm-chart-release.yaml
26) Execute it from main branch
27) Validate that the helm charts release has been generated within the release page
28) Create a new branch from release/1.0.0 and name it release/helm-environments-1.0.0 (helm app version not chart version)
29) Repeat step 8 to 23 for tractus-x: [GitHub Releases page](https://github.com/eclipse-tractusx/traceability-foss/releases)
30) Sync catena and eclipse main branch
31) Create a message in the Trace-X channel of the Eclipse Foundation Chat to notify the community about the new release (add a link to the tractus-x release)
