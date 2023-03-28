<div style="display: flex; align-items: center;justify-content: center;align-content: center;">
   <img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/trace-x-logo.svg" alt="Product Traceability FOSS Release Guide" style="width:200px;"/>
   <h1 style="margin: 10px 0 0 10px">Product Traceability FOSS Release guide</h1>
</div>

## Release an app

In order to release an app go to [GitHub Releases page](https://github.com/eclipse-tractusx/traceability-foss-backend/releases)
and create a new release. While creating it create new tag that follows [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

Please update the [CHANGELOG.md](https://github.com/eclipse-tractusx/traceability-foss/blob/main/CHANGELOG.md) file with release changes that follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/) format.

## Release helm charts

In order to release helm charts, update helm charts *version* property from
[backend Chart.yaml file](https://github.com/eclipse-tractusx/traceability-foss/blob/main/backend/charts/traceability-foss-backend/Chart.yaml) and from
[frontend Chart.yaml file](https://github.com/eclipse-tractusx/traceability-foss/blob/main/frontend/charts/traceability-foss-backend/Chart.yaml).
Please update *appVersion* property if there were changes to app version as well.

Next proceed to the [Release Charts workflow](https://github.com/eclipse-tractusx/traceability-foss-backend/actions/workflows/helm-chart-release.yaml)
and invoke the workflow manually for the *main* branch. The workflow will compare previously stored helm charts version and if it detects new version, it will release it in GitHub and will create an appropriate git tag.
