# Administration guide

### Notice

This work is licensed under the [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0).

* SPDX-License-Identifier: Apache-2.0
* Licence Path: <https://creativecommons.org/licenses/by/4.0/legalcode>
* Copyright (c) 2021,2022,2023, 2024 Contributors to the Eclipse Foundation
* Copyright (c) 2022, 2023 ZF Friedrichshafen AG
* Copyright (c) 2022 ISTOS GmbH
* Copyright (c) 2022, 2023, 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
* Copyright (c) 2022,2023 BOSCH AG
* Source URL: <https://github.com/eclipse-tractusx/traceability-foss>

## System overview

The deployment contains the components required to connect Trace-X to an existing Catena-X network. This includes:

* Trace-X frontend
* Trace-X backend

Optionally these components can be installed using the Trace-X backend Helm chart as well:

* PostgreSQL for Trace-X backend
* pgAdmin 4
* IRS
* EDC consumer

Everything else needs to be provided externally.

![undefined](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/adminguide/undefined)
```bash
Unresolved directive in system-overview.adoc - include::../../uml-diagrams/arc42/deployment-view/level-0-int.puml[]
```

### Rights and role matrix of Trace-X

Currently, Trace-X API handles three roles: ***'User'*** and ***'Supervisor'*** and ***'Admin'***:

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| Category | Action | User | Supervisor | Admin |
| View | View Dashboard | x | x | x |
|  | View Parts | x | x | x |
|  | View Quality notifications | x | x | x |
|  | View Administration panel |  |  | x |
| Quality notification | Create | x | x |  |
|  | Edit | x | x |  |
|  | Send (Approve) |  | x |  |
|  | Read | x | x | x |
|  | Cancel | x | x |  |
|  | Acknowledge | x | x |  |
|  | Accept | x | x |  |
|  | Decline | x | x |  |
|  | Close |  | x |  |
| Administration panel | Access "BPN EDC config panel" |  |  | x |
|  | Access "Registry lookup panel" |  |  | x |
|  | Access "Data import interface" |  |  | x |

## Installation

The Trace-X Helm repository can be found here:

<https://eclipse-tractusx.github.io/traceability-foss/index.yaml>

Use the latest release of the "trace-x-helm" chart.
It contains all required dependencies.

Supply the required configuration properties (see chapter [Configuration](configuration.adoc#_configuration)) in a values.yaml file or override the settings directly.

### Deployment using Helm

Add the Trace-X Helm repository:

$ helm repo add traceability-foss <https://eclipse-tractusx.github.io/traceability-foss>

Then install the Helm chart into your cluster:

$ helm install -f your-values.yaml traceability-foss traceability-foss/traceability-foss

### Dependent values

Following values need to match in order for application to start and have a valid PostgreSQL connection:

datasource:
    password: # database password

postgresql:
    auth:
        password: # database password

### Deployment using ArgoCD

Create a new Helm chart and use Trace-X as a dependency.

dependencies:
  - name: traceability-foss
    alias: traceability-foss
    version: x.x.x
    repository: "<https://eclipse-tractusx.github.io/traceability-foss/">

Then provide your configuration as the values.yaml of that chart.

Create a new application in ArgoCD and point it to your repository / Helm chart folder.

## Configuration

## Frontend configuration

Take the following template and adjust the configuration parameters (&lt;placeholders> mark the relevant spots).
You can define the URLs as well as most of the secrets yourself.

The OAuth2, Vault configuration / secrets depend on your setup and might need to be provided externally.

### Helm configuration Trace-X frontend (values.yaml)

<https://github.com/eclipse-tractusx/traceability-foss/blob/main/charts/traceability-foss/values.yaml>

#### Values explained

##### ingress.enabled

Enables &lt;true> or disables &lt;false> the ingress proxy for the frontend app.

##### ingress.className

The class name of the ingress proxy. E.g. `nginx`

##### ingress.annotations

Annotation for the ingress. E.g. `cert-manager.io/cluster-issuer: letsencrypt-prod`

##### ingress.hosts

The hostname of the app.

##### ingress.tls

The TLS settings of the app.

##### livenessProbe

Following Tractus-X Helm Best Practices <https://eclipse-tractusx.github.io/docs/release/>

##### readinessProbe

Following Tractus-X Helm Best Practices <https://eclipse-tractusx.github.io/docs/release/>

## Backend configuration

Take the following template and adjust the configuration parameters (&lt;placeholders> mark the relevant spots).
You can define the URLs as well as most of the secrets yourself.

The OAuth2, Vault configuration / secrets depend on your setup and might need to be provided externally.

### Helm configuration Trace-X backend (values.yaml)

[github.com/eclipse-tractusx/traceability-foss/blob/main/charts/traceability-foss/values.yaml](https://github.com/eclipse-tractusx/traceability-foss/blob/main/charts/traceability-foss/values.yaml)

#### Values explained

##### springprofile

The profiles for the different supported environments to bootstrap the resources which are required for the respective environment.

| Springprofile | Description |
| --- | --- |
| dev | Development environment |
| int | Integration environment |

##### healthCheck.enabled

Enables &lt;true> or disables &lt;false>  [livenessProbe](https://cloud.google.com/blog/products/containers-kubernetes/kubernetes-best-practices-setting-up-health-checks-with-readiness-and-liveness-probes)
and [readinessProbe](https://cloud.google.com/blog/products/containers-kubernetes/kubernetes-best-practices-setting-up-health-checks-with-readiness-and-liveness-probes)

##### traceability.bpn

BPN (Business Partner Number) for the traceability app used to identify the partner in the network.

##### datasource.url

The jdbc connection string to the database. jdbc:postgresql://${url}:${port}/trace

##### datasource.username

The username of the datasource e.g. "trace".

##### datasource.password

The password of the datasource or the path to the vault which contains the secret. &lt;path:../data/int/database#tracePassword>

##### oauth2.clientId

Client ID for OAuth2 (Keycloak). Request this from your Keycloak operator.

##### oauth2.clientSecret

Client secret for OAuth2. Request this from your OAuth2 operator.

##### oauth2.clientTokenUri

The URL of the Keycloak token API. Used by Trace-X for token creation to authenticate with other services.

##### oauth2.jwkSetUri

The URL of the Keycloak JWK Set. Used by Trace-X to validate tokens when Trace-X API is called.

##### oauth2.resourceClient

The client which is used to authenticate the backend.

##### edc.apiKey

The EDC api key or the path to the secret inside a vault. E.g. &lt;path:../data/int/edc/controlplane#edc.api.control.auth.apikey.value>

#### postgresql.enabled

Enables &lt;true> or disables &lt;false> the PostgresSQL database.

#### postgresql.auth.postgresPassword

Database password for the **postgres** user or the path to the secret inside a vault. &lt;path:.../data/int/database#password>

#### postgresql.auth.password

Database password for the application user of the path to the secret inside a vault. &lt;path:.../data/int/database#password>

#### postgresql.auth.database

The name of the database instance.

#### postgresql.auth.username

The user for the database instance.

#### global.enablePrometheus

Enables &lt;true> or disables &lt;false> the Prometheus instance.

#### global.enableGrafana

Enables &lt;true> or disables &lt;false> the Grafana instance used for resource and application monitoring.

#### irs-helm.enabled

Enables &lt;true> or disables &lt;false> IRS helm charts.

#### pgadmin4.enabled

Enables &lt;true> or disables &lt;false> pgAdmin 4 console for the PostgreSQL database instance.

#### pgadmin4.ingress.enabled

Enables &lt;true> or disables &lt;false> a K8S Ingress for the pgAdmin 4 console.

## Portal configuration

The following process is required to successfully connect to the portal:

* Company registration with BPN and company name
* User registration with e-mail
* Get e-mail to reset your password
* Reset the password and log in
* Make sure your user has the role 'App Management'
* Navigate to 'App Overview'
* Create app
* Choose a selection of managed roles which is necessary (currently: BPDM, Dataspace Discovery, Semantic Model Management, Identity Wallet Management)
* Wait for app approval by the portal team
* Subscribe to the app
* As app creator navigate to subscription management and click on configure
* Add the frontend url of the application and click approve
* Save technical user and secret
* Navigate to 'Register Connector'
* Add managed connector
* Select existing technical user (from app subscription)
* Enter name "EDC Provider A"
* Enter base url of control plane (EDC)
* Confirm
* Go to other company which wants to participate (subscribe)
* Login and navigate to app overview
* Search for the created app
* Subscribe to the app
* Go to the app creator company
* Navigate to the inbox of the portal
* Click on the link to give approval for the company which wants to subscribe
* Enter name "EDC Provider B"
* Enter base url of control plane (EDC)
* Make sure to populate the new client id, secrets and app id within Trace-X for each company to let it run properly with the new portal configuration.

### Company registration

[How-to](https://portal.int.demo.catena-x.net/documentation)

#### Additional info

Each instance of Trace-X reflects an own company, which is associated with one BPN.

### User registration

[How-to](https://portal.int.demo.catena-x.net/documentation/)

#### Additional info

The user registration is a self-service. Each user can have one or multiple Trace-X roles assigned.

### Connector registration

[How-to](https://portal.int.demo.catena-x.net/documentation/)

#### Additional info

A connector in the context of Trace-X is a Eclipse-Dataspace-Connector. This connector needs to be configured by the public control plane URL.

### App registration

[How-to](https://portal.int.demo.catena-x.net/documentation/)

#### Additional info

A connector in the context of trace-x is a Eclipse-Dataspace-Connector. This connector needs to be configured by the public control plane URL.

### Create app subscription

[How-to](https://portal.int.demo.catena-x.net/documentation/)

#### Additional info

An app subscription is necessary to be able to set up a frontend url which will be authorized through Keycloak and accessible with the portal.

### Activate App subscription

[How-to](https://portal.int.demo.catena-x.net/documentation/)

#### Additional info

The app subscription needs to be activated from all instances which want to participate in the Trace-X use case.

### Retrieve wallet configuration

[How-to](https://portal.int.demo.catena-x.net/documentation/)

## Troubleshooting

Coming soon...
