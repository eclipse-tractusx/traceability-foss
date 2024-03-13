<div style="display: flex; align-items: center;justify-content: center;align-content: center;">
   <img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/trace-x-logo.svg" alt="Product Traceability FOSS Release Guide" style="width:200px;"/>
   <h1 style="margin: 10px 0 0 10px">Product Traceability FOSS Release guide</h1>
</div>

## Release an app

Prerequisite:
Make sure eclipse / catena git repositories are in sync

1) Decide which version will be incremented. Following shows example for 1.0.0
2) Create a release branch in [IRS](https://github.com/eclipse-tractusx/item-relationship-service) named release/trace-x-irs-client-lib-release
3) Click on the [Update irs-registry-client Version workflow](https://github.com/eclipse-tractusx/item-relationship-service/actions/workflows/update-registry-library.yaml).
4) The irs lib version in (irs)[https://github.com/eclipse-tractusx/item-relationship-service/blob/65a42336b7ec7ae50690ec7590d05d8e3b15555a/pom.xml#L76]  should align with the potential (newer one)[https://github.com/catenax-ng/tx-traceability-foss/blob/fb1130d4c1dd4f869e61d334310e99bc191fa0c7/pom.xml#L103]  in trace-x main
5) Select "Run workflow," choose the release branch and select the type of version increment major, minor or patch. Use the Semantic Versioning guidelines to accurately determine the specific type of version increment. [Semantic Versioning](https://semver.org/spec/v2.0.0.html). Don't forget to check the box to remove the snapshot.
6) A pull request (name: Update irs-registry-client to "Version") will be generated in which you have to make sure the irs lib version is now correct (change it manually if necessary). Check if Actions finish successfully. (If Dash IP Check fails, update the DEPENDENCIES file with the mvn command on the generated pull request branch)
7) Afterward, merge the created Pull Request and delete the release branch.
8) In [IRS](https://github.com/eclipse-tractusx/item-relationship-service), the gitHub action [Upload to Central Maven Registry ](https://github.com/eclipse-tractusx/item-relationship-service/actions/workflows/maven-deploy.yaml) will release the irs-registry-client library with the version defined in step 3
9) Create and Checkout release branch on catena /release/1.0.0
10) Update <irs-client-lib.version> in the above created release branch to the created one in step 3.
11) Edit changelog: Align the new version (1.0.0) with the changes and add new UNRELEASED section
12) Edit /charts/traceability-foss/CHANGELOG.md
13) Add an Entry for an incremented (patch) version (1.0.0 -> 1.0.1)
14) Push onto /release/1.0.0 catena and eclipse
15) Open Release App Page Catena: https://github.com/catenax-ng/tx-traceability-foss/releases
16) Draft a new release
17) On dropdown choose a tag - use the version 1.0.0 (Create new tag will appear - select it)
18) On dropdown target use your /release/1.0.0
19) Title = Version of app -> 1.0.0
20) Description = Changelog Content of app
21) Checkbox set as latest release
22) Verify that github action [Release](https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/release.yaml) generation has been triggered
23) Verify that an automatic pull request has been opened (Prepare Helm release for next version)
24) Validate that the versions within that pull requests are correct
25) Merge pull request (Prepare Helm release for next version)
26) Merge release branch into main (when merging make sure to restore release branch since it should stay)
27) Open the github action for helm release generation: https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/helm-chart-release.yaml
28) Execute it from main branch
29) Validate that the helm charts release has been generated within the release page
30) Create a new branch from release/1.0.0 and name it release/helm-environments-1.0.0 (helm app version not chart version)
31) Repeat step 8 to 23 for tractus-x: [GitHub Releases page](https://github.com/eclipse-tractusx/traceability-foss/releases)
32) Sync catena and eclipse main branch
33) Create a message in the Trace-X channel of the Eclipse Foundation Chat to notify the community about the new release (add a link to the tractus-x release)
