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
1) Create a new Release in catena: https://github.com/catenax-ng/tx-traceability-foss/releases/new  with the release version of the application.
2) Before accepting the Pull Request which will be created based on Step 1) please make sure to create a Pull Request from catena to eclipse and merge it.
3) Then create a new Release in tractusx: https://github.com/eclipse-tractusx/traceability-foss/releases/new with the release version of the application.
4) A Release Action ( Release Trace-X ) will bump the helm versions and create a Pull request that needs to be merged for catena and tractusx.
5) Please accept the PRs on both repositories.
6) Invoke the [Release Charts workflow](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/helm-chart-release.yaml) on main branch
7) Invoke the [Release Charts workflow](https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/helm-chart-release.yaml) on main branch
