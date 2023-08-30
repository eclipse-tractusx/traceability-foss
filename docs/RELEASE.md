<div style="display: flex; align-items: center;justify-content: center;align-content: center;">
   <img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/trace-x-logo.svg" alt="Product Traceability FOSS Release Guide" style="width:200px;"/>
   <h1 style="margin: 10px 0 0 10px">Product Traceability FOSS Release guide</h1>
</div>

## Release an app

In order to release an app go to [GitHub Releases page](https://github.com/eclipse-tractusx/traceability-foss/releases)
and create a new release. While creating it create new tag that follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Please update the [CHANGELOG.md](https://github.com/eclipse-tractusx/traceability-foss/blob/main/CHANGELOG.md) file with release changes that follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) format.

## Release helm charts
Hint: You need to stricly follow this guide to make sure the helm chart releaser will be able to detect a new helm chart version!
1) Create a GIT Tag with the release version of the application.
2) A Release Action ( Release Trace-X ) will bump the helm versions and create a Pull request that needs to be merged.
3) Check if the Action ran successfully and the following properties in the Chart.yamls and package.json were updated with the corresponding new versions from your created tag and the latest version entry from the [charts/CHANGELOG.md](https://github.com/catenax-ng/tx-traceability-foss/blob/main/charts/traceability-foss/CHANGELOG.md) file:
   - *version* & *appVersion* property from
     - [backend Chart.yaml file](https://github.com/eclipse-tractusx/traceability-foss/blob/main/charts/traceability-foss/charts/backend/Chart.yaml) and from
     - [frontend Chart.yaml file](https://github.com/eclipse-tractusx/traceability-foss/blob/main/charts/traceability-foss/charts/frontend/Chart.yaml).
     - [Umbrella Helm Chart.yaml file](https://github.com/eclipse-tractusx/traceability-foss/blob/main/charts/traceability-foss/Chart.yaml).
     - [frontend package.json file](https://github.com/eclipse-tractusx/traceability-foss/blob/main/frontend/package.json)
3) Proceed with merging into the main branch and check the [Release Charts workflow](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/helm-chart-release.yaml)
   - It should trigger automatically, if not invoke the workflow manually for the *main* branch. The workflow will compare previously stored helm charts version and if it detects new version, it will release it in GitHub and will create an appropriate git tag.
