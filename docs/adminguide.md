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
* pgadmin4
* IRS
* EDC consumer

Everything else needs to be provided externally.

![adminguide_000](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/adminguide/adminguide_000.png)

### Rights and role matrix of Trace-X

Currently, Trace-X API handles three roles: ***'User'*** and ***'Supervisor'*** and ***'Admin'***:

|     |     |     |     |     |
| --- | --- | --- | --- | --- |
| Category | Action | User | Supervisor | Admin |
| View | View Dashboard | x | x | x |
|  | View Parts | x | x | x |
|  | View Other parts | x | x | x |
|  | View Quality notifications | x | x | x |
|  | View Administration |  |  | x |
| Edit | Edit Quality notifications |  | x |  |
| Quality notification | Create | x | x |  |
|  | Send (Approve) |  | x |  |
|  | Read | x | x | x |
|  | Cancel | x | x |  |
|  | Acknowledge | x | x |  |
|  | Accept | x | x |  |
|  | Decline | x | x |  |
|  | Close |  | x |  |
| Administration panel | Access "BPN EDC config panel" |  |  | x |
|  | Access "Registry lookup Panel" |  |  | x |
|  | Access "Data Import Interface" |  |  | x |

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

Following values needs to match in order for application to start and have valid PostgreSQL connection:

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

values.yaml <https://github.com/eclipse-tractusx/traceability-foss/blob/main/charts/traceability-foss/values.yaml>

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

The tls settings of the app.

##### livenessProbe

Following Tractus-X Helm Best Practices <https://eclipse-tractusx.github.io/docs/release/>

##### readinessProbe

Following Tractus-X Helm Best Practices <https://eclipse-tractusx.github.io/docs/release/>

## Backend configuration

Take the following template and adjust the configuration parameters (&lt;placeholders> mark the relevant spots).
You can define the URLs as well as most of the secrets yourself.

The OAuth2, Vault configuration / secrets depend on your setup and might need to be provided externally.

### Helm configuration Trace-X backend (values.yaml)

```yaml
                - key: app.kubernetes.io/name
                  operator: DoesNotExist
            topologyKey: kubernetes.io/hostname

  ingress:
    enabled: false
    className: ""
    annotations: {}
    hosts: []
    tls: []

  # Following Catena-X Helm Best Practices @url: https://catenax-ng.github.io/docs/kubernetes-basics/helm
  # @url: https://github.com/helm/charts/blob/master/stable/nginx-ingress/values.yaml#L210
  livenessProbe:
    failureThreshold: 6
    initialDelaySeconds: 180
    periodSeconds: 10
    successThreshold: 1
    timeoutSeconds: 1
  readinessProbe:
    failureThreshold: 6
    initialDelaySeconds: 180
    periodSeconds: 10
    successThreshold: 1
    timeoutSeconds: 1

#########################
# Backend Chart Values configuration     #
#########################
backend:
  # Default values for k8s-helm-example.
  # This is a YAML-formatted file.
  # Declare variables to be passed into your templates.
  nameOverride: "tx-backend"
  fullnameOverride: "tx-backend"
  replicaCount: 1

  image:
    repository: tractusx/traceability-foss
    pullPolicy: Always

  springprofile: dev  # will be set as dev

  ##
  ## Image pull secret to create to obtain the container image
  ## Note: 'imagePullSecret.dockerconfigjson' takes precedence if configured together with 'imagePullSecrets'
  ##
  imagePullSecret:
    dockerconfigjson: ""

  ##
  ## Existing image pull secret to use to obtain the container image
  ##
  imagePullSecrets: []

  serviceAccount:
    ##
    ## Specifies whether a service account should be created per release
    ##
    create: true
    ##
    ## Annotations to add to the service account
    ##
    annotations: {}
    ##
    ## The name of the service account to use.
    ## If not set and create is true, a name is generated using the fullname template
    ##
    name: ""
  ## dd

  podAnnotations: {}


  podSecurityContext:
    runAsUser: 10000
    seccompProfile:
      type: RuntimeDefault

  # Following Catena-X Helm Best Practices @url: https://catenax-ng.github.io/docs/kubernetes-basics/helm
  # @url: https://kubernetes.io/docs/tasks/configure-pod-container/security-context/#set-the-security-context-for-a-pod
  securityContext:
    allowPrivilegeEscalation: false
    runAsNonRoot: true
    runAsUser: 10000
    runAsGroup: 1000
    capabilities:
      drop:
        - ALL
    readOnlyRootFilesystem: false

  service:
    type: ClusterIP
    port: 8080
    trustedPort: 8181

  autoscaling:
    enabled: false

  # Following Catena-X Helm Best Practices @url: https://catenax-ng.github.io/docs/kubernetes-basics/helm
  # @url: https://cloud.google.com/blog/products/containers-kubernetes/kubernetes-best-practices-resource-requests-and-limits
  resources:
    limits:
      cpu: 500m
      memory: 512Mi
    requests:
      cpu: 250m
      memory: 512Mi

  nodeSelector: {}

  tolerations: []

  # Following Catena-X Helm Best Practices @url: https://catenax-ng.github.io/docs/kubernetes-basics/helm
  # @url: https://kubernetes.io/docs/concepts/scheduling-eviction/assign-pod-node/#affinity-and-anti-affinity
  affinity:
    podAntiAffinity:
      preferredDuringSchedulingIgnoredDuringExecution:
        - weight: 100
          podAffinityTerm:
            labelSelector:
              matchExpressions:
                - key: app.kubernetes.io/name
                  operator: DoesNotExist
            topologyKey: kubernetes.io/hostname

  # Following Catena-X Helm Best Practices @url: https://catenax-ng.github.io/docs/kubernetes-basics/helm
  # @url: https://github.com/helm/charts/blob/master/stable/nginx-ingress/values.yaml#L210
  livenessProbe:
    failureThreshold: 6
    initialDelaySeconds: 180
    periodSeconds: 10
    successThreshold: 1
    timeoutSeconds: 1
  readinessProbe:
    failureThreshold: 6
    initialDelaySeconds: 180
    periodSeconds: 10
    successThreshold: 1
    timeoutSeconds: 1

  ingress:
    hosts:
      - "<https://replace.me">
    tls:
      - hosts:
          - "<https://replace.me">
        secretName: tls-secret

  healthCheck:
    enabled: true  # <healthCheck.enabled>

  traceability:
    bpn: "CHANGEME"  # <traceability.bpn>
    url: ""  # backend url example: https://<backend.ingress.hosts>
    leftOperand: "PURPOSE"
    operatorType: "eq"
    rightOperand: "ID 3.0 Trace"
    leftOperandSecond: "anything"
    operatorTypeSecond: "eq"
    rightOperandSecond: "anything"
    validUntil: "2024-09-30T23:59:59.99Z"
  datasource:
    url: jdbc:postgresql://tx-backend-postgresql:5432/trace
    username: "traceuser"
    password: "CHANGEME"  # <datasource.password>

  oauth2:
    clientId: "CHANGEME"  # <oauth2.clientId>
    clientSecret: "CHANGEME"  # <oauth2.clientSecret>
    clientTokenUri: "<https://changeme.com">  # <oauth2.clientTokenUri>
    jwkSetUri: "<https://changeme.com">  # <oauth2.jwkSetUri>
    resourceClient: "CHANGEME"  # application id created on portal

  edc:
    apiKey: "CHANGEME"  # <tractusx-connector.controlplane.endpoints.management.authKey>
    providerUrl: "CHANGEME"  #  example: https://<tractusx-connector.controlplane.ingress.hosts>
    callbackUrl: "CHANGEME"  # example: http://<irs-helm.nameOverride>:8181/internal/endpoint-data-reference
    callbackUrlEdcClient: "CHANGEME"  # example: https://<backend.ingress.hosts>/api/internal/endpoint-data-reference
    dataEndpointUrl: "CHANGEME"  # example: https://<tractusx-connector.controlplane.ingress.hosts>/management"
    partsProviderControlplaneUrl: "CHANGEME"  # host of the parts provider EDC
    partsProviderDataplaneUrl: "CHANGEME"

  discoveryfinder:
    baseUrl: "CHANGEME"  # example: https://discoveryfinder.net/discoveryfinder/api/administration/connectors/discovery/search

  irs:
    baseUrl: "<https://replace.me">  # https://<irs-helm.ingress.host>
  registry:
    urlWithPath: "<https://replace.me">  # digitalTwinRegistry /semantics/registry/api/v3.0
    allowedBpns: "BPN1,BPN2"  # "," separated list of allowed bpns for creating shells
  portal:
    baseUrl: "<https://replace.me">

  config:
    allowedCorsOriginFirst: "<https://replace.me">
    allowedCorsOriginSecond: "<https://replace.me">

  #  required for init containers checking for dependant pod readiness before starting up backend
  dependencies:
    enabled: false  # enable dependency check init containers
    irs: "CHANGEME"  # <irs-helm.nameOverride>
    edc: "CHANGEME"  # <tractusx-connector.nameOverride

  bpdm:
    url: "<https://replace.me">

#########################
# PG Admin configuration     #
#########################
pgadmin4:
  nameOverride: "tracex-pgadmin4"
  fullnameOverride: "tracex-pgadmin4"
  enabled: false  # <pgadmin4.enabled>
  ingress:
    enabled: false   # <pgadmin4.ingress.enabled>

  resources:
    limits:
      cpu: 1000m
      memory: 512Mi
    requests:
      cpu: 256m
      memory: 512Mi

#########################
# Postgres configuration     #
#########################
postgresql:
  enabled: true

  nameOverride: "tx-backend-postgresql"
  fullnameOverride: "tx-backend-postgresql"

  auth:
    postgresPassword: "CHANGEME"
    password: "CHANGEME"
    database: "trace"
    username: "traceuser"

  resources:
    limits:
      cpu: 500m
      memory: 512Mi
    requests:
      cpu: 250m
      memory: 512Mi

#########################
# IRS configuration     #
#########################
item-relationship-service:
  enabled: false  # <irs-helm.enabled>
  nameOverride: "tracex-irs"
  fullnameOverride: "tracex-irs"

  resources:
    limits:
      cpu: 500m
      memory: 512Mi
    requests:
      cpu: 250m
      memory: 512Mi
###################################
# EDC Consumer configuration  #
###################################
tractusx-connector:
  enabled: false
  nameOverride: "tracex-consumer-edc"
  fullnameOverride: "tracex-consumer-edc"
  participant:
    id: "BPN"

  resources:
    limits:
      cpu: 500m
      memory: 512Mi
    requests:
      cpu: 250m
      memory: 512Mi

  ##################################
  # EDC Postgres Configuration #
  ##################################
  postgresql:
    enabled: false
    auth:
      username: "CHANGEME"
      password: "CHANGEME"
    username: "CHANGEME"
    password: "CHANGEME"
    jdbcUrl: "CHANGEME"

###################################
# IRS EDC Consumer configuration  #
###################################
edc-postgresql:
  nameOverride: "tracex-consumer-edc-postgresql"
  fullnameOverride: "tracex-consumer-edc-postgresql"
  enabled: false
  auth:
    postgresPassword: "CHANGEME"
    password: "CHANGEME"
    database: "trace"
    username: "traceuser"

  resources:
    limits:
      cpu: 500m
      memory: 512Mi
    requests:
      cpu: 250m
      memory: 512Mi
```

#### Values explained

##### springprofile

The different profiles for the different supported environments to bootstrap the resources which are required for the respective environment.

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

The username of the datasource e.g. "trace"

##### datasource.password

The password of the datasource or the path to the vault which contains the secret. &lt;path:../data/int/database#tracePassword>

##### oauth2.clientId

Client ID for OAuth2 (KeyCloak). Request this from your Keycloak operator.

##### oauth2.clientSecret

Client secret for OAuth2. Request this from your OAuth2 operator.

##### oauth2.clientTokenUri

The URL of the Keycloak token API. Used by Trace-X for token creation to authenticate with other services.

##### oauth2.jwkSetUri

The URL of the Keycloak JWK Set. Used by Trace-X to validate tokens when Trace-X API is called.

##### oauth2.resourceClient

The client which is used to authenticate the backend.

##### edc.apiKey

The EDC api key or the path to the secret inside a vault. e.g. &lt;path:../data/int/edc/controlplane#edc.api.control.auth.apikey.value>

#### postgresql.enabled

Enables &lt;true> or disables &lt;false> PostgresSQL database.

#### postgresql.auth.postgresPassword

Database password for the **postgres** user or the path to the secret inside a vault. &lt;path:.../data/int/database#password>

#### postgresql.auth.password

Database password for the application user of the path to the secret inside a vault. &lt;path:.../data/int/database#password>

#### postgresql.auth.database

The name  of the database instance.

#### postgresql.auth.username

The user for the database instance.

#### global.enablePrometheus

Enables &lt;true> or disables &lt;false> the prometheus instance.

#### global.enableGrafana

Enables &lt;true> or disables &lt;false> the grafana instance used for resource and application monitoring.

#### irs-helm.enabled

Enables &lt;true> or disables &lt;false> irs helm charts.

#### pgadmin4.enabled

Enables &lt;true> or disables &lt;false> pgadmin4 console for the PostgreSQL database instance

#### pgadmin4.ingress.enabled

Enables &lt;true> or disables &lt;false> a K8S Ingress for the pgadmin4 console

## Portal configuration

The following process is required to successfully connect to the portal:

* Company registration with BPN, Company Name
* User Registration with E-Mail
* Get e-mail to reset your password
* Reset the password and log in
* Make sure your user has the role App Management
* Navigate to App Overview
* Create app
* Choose a selection of managed roles which is necessary (currently: BPDM, Dataspace Discovery, Semantic Model Management, Identity Wallet Management)
* Wait for app approval by portal
* Subscribe to the app
* As app creator navigate to subscription management - click on configure
* Add the frontend url of the application and click approve
* Save technical user and secret
* Navigate to Register Connector
* Add managed connector
* Select existing technical user (from app subscription)
* Enter name "EDC Provider A"
* Enter base url of controlplane (EDC)
* Confirm
* Go to other company which want to participate (subscribe)
* Login and navigate to app overview
* Search for the app created
* Subscribe to the app
* Go to the app creator company
* Navigate to the inbox of the portal
* Click on the nav link to give approval for the company which want to subscribe
* Enter name "EDC Provider B"
* Enter base url of controlplane (EDC)
* Make sure to popuplate the new client id, secrets and app id within trace-x for each company to let it run properly with the new portal configuration.

### Company registration

[How-to](https://portal.int.demo.catena-x.net/documentation)

#### Additional info

Each instance of trace-x reflects an own company, which is associated with one BPN.

### User registration

[How-to](https://portal.int.demo.catena-x.net/documentation/)

#### Additional info

The user registration is a self-service. Each user can have one or multiple Trace-X roles assigned.

### Connector registration

[How-to](https://portal.int.demo.catena-x.net/documentation/)

#### Additional info

A connector in the context of trace-x is a Eclipse-Dataspace-Connector. This connector needs to be configured by the public controlplane URL.

### App registration

[How-to](https://portal.int.demo.catena-x.net/documentation/)

#### Additional info

A connector in the context of trace-x is a Eclipse-Dataspace-Connector. This connector needs to be configured by the public controlplane URL.

### Create app subscription

[How-to](https://portal.int.demo.catena-x.net/documentation/)

#### Additional info

An app subscription is necessary to be able to setup a frontend url which will be authorized through keycloak and accessible with the portal.

### Activate App subscription

[How-to](https://portal.int.demo.catena-x.net/documentation/)

#### Additional info

The app subscription needs to be activated from all instances which want to participate in the Trace-X use case.

### Retrieve wallet configuration

[How-to](https://portal.int.demo.catena-x.net/documentation/)

## Troubleshooting

Coming soon...
