# GitHub Workflows for Cofinity-X - WIP

Collects an overview about all existing workflows in this folder and available for this project.
Marks those WF which are owned by upstream (Tractus-X) or us (CFX).

Get a local output of all active workflows by using [GitHub CLI](https://github.com/cli/cli): `gh workflow list` or `gh workflow list --json name,path |jq .`

Find the recommended security workflows here: [Github Advance Security](
https://cofinity-x.atlassian.net/wiki/spaces/CXO/pages/156205057/Github+Advance+Security#6.-cofinity-x-ba-traceability-foss)

| Filename                                                                                         | owned by   | enabled for CFX |
|--------------------------------------------------------------------------------------------------|------------|-----------------|
| [argo.yml](argo.yml)                                                                             | Tractus-X  | no              |
| [codeql.yml](codeql.yml)                                                                         |            | yes             |
| [cfx_image-backend-build_and_push.yml](cfx_image-backend-build_and_push.yml)                     | CFX        | yes             |
| [cfx_image-cfx-frontend-main.yml](cfx_image-cfx-frontend-main.yml)                               | CFX        | yes             |
| [cfx_image-tractusx-frontend-build-and-push.yml](cfx_image-tractusx-frontend-build-and-push.yml) | CFX        | yes             |
| [cfx_sonar-scan-frontend.yml](cfx_sonar-scan-frontend.yml)                                       | CFX        | yes             |
| [dependencies.yaml](dependencies.yaml)                                                           |            | yes             |
| [dependency-check.yml](dependency-check.yml)                                                     |            | no              |
| [docker-image-branch_frontend.yml](docker-image-branch_frontend.yml)                             | Tractus-X  | no              |
| [docker-image-main_backend.yml](docker-image-main_backend.yml)                                   | Tractus-X  | no              |
| [docker-image-main_frontend.yml](docker-image-main_frontend.yml)                                 | Tractus-X  | no              |
| [docker-image-tag-release.yaml](docker-image-tag-release.yaml)                                   | Tractus-X  | no              |
| [e2e-tests-xray_frontend.yml](e2e-tests-xray_frontend.yml)                                       |            | no              |
| [eclipse-dash.yml](eclipse-dash.yml)                                                             |            | yes             |
| [helm-chart-release.yaml](helm-chart-release.yaml)                                               |            | yes             |
| [helm-test-backwards-compatability.yaml](helm-test-backwards-compatability.yaml)                 |            | yes             |
| [helm-test.yaml](helm-test.yaml)                                                                 |            | yes             |
| [helm-upgrade.yaml](helm-upgrade.yaml)                                                           |            | no              |
| [jira-publish-release.yaml](jira-publish-release.yaml)                                           |            | no              |
| [kics.yml](kics.yml)                                                                             | TractusX   | yes             |
| [linting_frontend.yml](linting_frontend.yml)                                                     |            | yes             |
| [publish-documentation.yaml](publish-documentation.yaml)                                         |            | no              |
| [pull-request_backend.yml](pull-request_backend.yml)                                             |            | yes             |
| [pull_request_issue.yml](pull_request_issue.yml)                                                 |            | no              |
| [quality-checks.yaml](quality-checks.yaml)                                                       |            | yes             |
| [release.yaml](release.yaml)                                                                     |            | yes             |
| [snyk.yaml](snyk.yaml)                                                                           | CFX        | yes             |
| [sonar-scan-backend.yml](sonar-scan-backend.yml )                                                |            | yes             |
| [sonar-scan-frontend.yml](sonar-scan-frontend.yml)                                               | Tractus-X  | no              |
| [spotbugs.yml](spotbugs.yml)                                                                     |            | yes             |
| [trivy.yml](trivy.yml)                                                                           |            | yes             |
| [unit-test_frontend.yml](unit-test_frontend.yml)                                                 | Tractus-X  | no              |
| [update-helm-environment.yml](update-helm-environment.yml)                                       |            | no              |
| [veracode_backend.yml](veracode_backend.yml)                                                     | Tractus-X  | no              |
| [veracode_frontend.yml](veracode_frontend.yml)                                                   | Tractus-X  | no              |
| [workflow.json](workflow.json)                                                                   |            | no              |
| [xray-cucumber.yaml](xray-cucumber.yaml)                                                         |            | no              |
