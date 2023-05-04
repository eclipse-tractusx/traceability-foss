# Administration Guide

## System Overview

The deployment contains the components required to connect Trace-X to an existing Catena-X network. This includes:

* Trace-X Frontend
* Trace-X Backend

Optionally these components can be installed using the Trace-X backend Helm chart as well:

* PostgreSQL for Trace-X Backend
* pgadmin4
* IRS
* EDC Consumer

Everything else needs to be provided externally.

![adminguide_000](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/adminguide/adminguide_000.png)

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

## Frontend Configuration

Take the following template and adjust the configuration parameters (&lt;placeholders> mark the relevant spots).
You can define the URLs as well as most of the secrets yourself.

The OAuth2, Vault configuration / secrets depend on your setup and might need to be provided externally.

### Helm configuration Trace-X Frontend (values.yaml)

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

## Backend Configuration

Take the following template and adjust the configuration parameters (&lt;placeholders> mark the relevant spots).
You can define the URLs as well as most of the secrets yourself.

The OAuth2, Vault configuration / secrets depend on your setup and might need to be provided externally.

### Helm configuration Trace-X Backend (values.yaml)

```yaml
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
    failureThreshold: 3
    initialDelaySeconds: 60
    periodSeconds: 10
    successThreshold: 1
    timeoutSeconds: 1
  readinessProbe:
    failureThreshold: 3
    initialDelaySeconds: 60
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

  replicaCount: 1

  image:
    repository: ghcr.io/catenax-ng/tx-traceability-foss
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

  nameOverride: "traceability-foss-backend"
  fullnameOverride: "traceability-foss-backend"

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

  autoscaling:
    enabled: false

  # Following Catena-X Helm Best Practices @url: https://catenax-ng.github.io/docs/kubernetes-basics/helm
  # @url: https://cloud.google.com/blog/products/containers-kubernetes/kubernetes-best-practices-resource-requests-and-limits
  resources:
    limits:
      cpu: 500m
      memory: 512Mi
    requests:
      cpu: 500m
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
    failureThreshold: 3
    initialDelaySeconds: 80
    periodSeconds: 10
    successThreshold: 1
    timeoutSeconds: 1
  readinessProbe:
    failureThreshold: 3
    initialDelaySeconds: 80
    periodSeconds: 10
    successThreshold: 1
    timeoutSeconds: 1

  ingress:
    enabled: false
    className: ""
    annotations: {}
    hosts:
      - "<https://replace.me">
    tls:
      - hosts:
          - "<https://replace.me">
        secretName: tls-secret

  healthCheck:
    enabled: false  # <healthCheck.enabled>

  traceability:
    bpn: "CHANGEME"  # <traceability.bpn>
    url: ""  # <traceability.url>

  datasource:
    url: jdbc:postgresql://traceability-foss-backend-postgresql:5432/trace
    username: "traceuser"
    password: "CHANGEME"  # <datasource.password>

  oauth2:
    clientId: "CHANGEME"  # <oauth2.clientId>
    clientSecret: "CHANGEME"  # <oauth2.clientSecret>
    clientTokenUri: "<https://changeme.com">  # <oauth2.clientTokenUri>
    jwkSetUri: "<https://changeme.com">  # <oauth2.jwkSetUri>
    resourceClient: "CHANGEME"  # <oauth2.resourceClient>

  edc:
    apiKey: ""  # <edc.apiKey>
    providerUrl: ""  # <edc.providerUrl>
    dataplane:
      url: "<https://replace.me">
    controlplane:
      url: "<https://example.com">

    edc-dataplane:
      ingresses:
        - enabled: false
          annotations: {}
          className: ""
          tls:
            - hosts:
                - "<https://replace.me">
              secretName: tls-secret


#########################
# PG Admin configuration     #
#########################
pgadmin4:
  enabled: false  # <pgadmin4.enabled>
  ingress:
    enabled: false   # <pgadmin4.ingress.enabled>

  resources:
    limits:
      cpu: 1000m
      memory: 1Gi
    requests:
      cpu: 256m
      memory: 512Mi

#########################
# Postgres configuration     #
#########################
postgresql:
  enabled: true

  nameOverride: "traceability-foss-backend-postgresql"
  fullnameOverride: "traceability-foss-backend-postgresql"

  auth:
    postgresPassword: "CHANGEME"
    password: "CHANGEME"
    database: "trace"
    username: "traceuser"

#########################
# IRS configuration     #
#########################
irs-helm:
  enabled: false  # <irs-helm.enabled>

###################################
# IRS EDC Consumer configuration  #
###################################
irs-edc-consumer:
  enabled: false  # <irs-edc-consumer.enabled>
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

## Troubleshooting

Coming soon...
