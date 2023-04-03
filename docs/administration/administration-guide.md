Administration Guide
====================

Table of Contents

-   [System Overview](#system-overview)
-   [Installation](#installation)
    -   [Deployment using Helm](#deployment-using-helm)
    -   [Dependent values](#dependent-values)
    -   [Deployment using ArgoCD](#deployment-using-argocd)
-   [Configuration](#configuration)
    -   [Frontend Configuration](#frontend-configuration)
    -   [Backend Configuration](#backend-configuration)
-   [Troubleshooting](#troubleshooting)

System Overview
---------------

The deployment contains the components required to connect Trace-X to an existing Catena-X network. This includes:

-   Trace-X Frontend

-   Trace-X Backend

Optionally these components can be installed using the Trace-X backend Helm chart as well:

-   PostgreSQL for Trace-X Backend

-   pgadmin4

-   IRS

-   EDC Consumer

Everything else needs to be provided externally.

<img src="integrated-overview.svg" width="746" height="568" alt="integrated overview" />

Installation
------------

The Trace-X Helm Backend repository can be found here:

<a href="https://eclipse-tractusx.github.io/traceability-foss-backend/index.yaml" class="bare">https://eclipse-tractusx.github.io/traceability-foss-backend/index.yaml</a>

The Trace-X Helm Frontend repository can be found here:

<a href="https://eclipse-tractusx.github.io/traceability-foss-frontend/index.yaml" class="bare">https://eclipse-tractusx.github.io/traceability-foss-frontend/index.yaml</a>

Use the latest release of the "trace-x-helm" chart.
It contains all required dependencies.

Supply the required configuration properties (see chapter [Configuration](#configuration)) in a values.yaml file or override the settings directly.

### Deployment using Helm

Add the Trace-X Backend Helm repository:

    $ helm repo add traceability-foss-backend https://eclipse-tractusx.github.io/traceability-foss-backend
    $ helm repo add traceability-foss-frontend https://eclipse-tractusx.github.io/traceability-foss-frontend

Then install the Helm chart into your cluster:

    $ helm install -f your-values.yaml traceability-foss-backend traceability-foss-backend/traceability-foss-backend
    $ helm install -f your-values.yaml traceability-foss-frontend traceability-foss-frontend/traceability-foss-frontend

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
      - name: traceability-foss-frontend
        alias: frontend
        version: x.x.x
        repository: "https://eclipse-tractusx.github.io/traceability-foss-frontend/"
      - name: traceability-foss-backend
        alias: backend
        version: x.x.x
        repository: "https://eclipse-tractusx.github.io/traceability-foss-backend/"

Then provide your configuration as the values.yaml of that chart.

Create a new application in ArgoCD and point it to your repository / Helm chart folder.

Configuration
-------------

### Frontend Configuration

Take the following template and adjust the configuration parameters (&lt;placeholders&gt; mark the relevant spots).
You can define the URLs as well as most of the secrets yourself.

The OAuth2, Vault configuration / secrets depend on your setup and might need to be provided externally.

#### Helm configuration Trace-X Frontend (values.yaml)

values.yaml <a href="https://github.com/eclipse-tractusx/traceability-foss-frontend/blob/main/charts/traceability-foss-frontend/values.yaml" class="bare">https://github.com/eclipse-tractusx/traceability-foss-frontend/blob/main/charts/traceability-foss-frontend/values.yaml</a>

##### Values explained

###### &lt;ingress.enabled&gt;

Enables &lt;true&gt; or disables &lt;false&gt; the ingress proxy for the frontend app.

###### &lt;ingress.className&gt;

The class name of the ingress proxy. E.g. `nginx`

###### &lt;ingress.annotations&gt;

Annotation for the ingress. E.g. `cert-manager.io/cluster-issuer: letsencrypt-prod`

###### &lt;ingress.hosts&gt;

The hostname of the app.

###### &lt;ingress.tls&gt;

The tls settings of the app.

###### &lt;livenessProbe&gt;

Following Catena-X Helm Best Practices <a href="https://catenax-ng.github.io/docs/kubernetes-basics/helm" class="bare">https://catenax-ng.github.io/docs/kubernetes-basics/helm</a>

###### &lt;readinessProbe&gt;

Following Catena-X Helm Best Practices <a href="https://catenax-ng.github.io/docs/kubernetes-basics/helm" class="bare">https://catenax-ng.github.io/docs/kubernetes-basics/helm</a>

### Backend Configuration

Take the following template and adjust the configuration parameters (&lt;placeholders&gt; mark the relevant spots).
You can define the URLs as well as most of the secrets yourself.

The OAuth2, Vault configuration / secrets depend on your setup and might need to be provided externally.

#### Helm configuration Trace-X Backend (values.yaml)

    Unresolved directive in backend-configuration.adoc - include::../../../../charts/traceability-foss-backend/values.yaml[lines=121..-1]

##### Values explained

###### &lt;springprofile&gt;

The different profiles for the different supported environments to bootstrap the resources which are required for the respective environment.

<table>
<colgroup>
<col style="width: 50%" />
<col style="width: 50%" />
</colgroup>
<thead>
<tr class="header">
<th>Springprofile</th>
<th>Description</th>
</tr>
</thead>
<tbody>
<tr class="odd">
<td><div class="content">
<div class="paragraph">
<p>dev</p>
</div>
</div></td>
<td><div class="content">
<div class="paragraph">
<p>Development environment</p>
</div>
</div></td>
</tr>
<tr class="even">
<td><div class="content">
<div class="paragraph">
<p>int</p>
</div>
</div></td>
<td><div class="content">
<div class="paragraph">
<p>Integration environment</p>
</div>
</div></td>
</tr>
</tbody>
</table>

###### &lt;healthCheck.enabled&gt;

Enables &lt;true&gt; or disables &lt;false&gt; [livenessProbe](https://cloud.google.com/blog/products/containers-kubernetes/kubernetes-best-practices-setting-up-health-checks-with-readiness-and-liveness-probes)
and [readinessProbe](https://cloud.google.com/blog/products/containers-kubernetes/kubernetes-best-practices-setting-up-health-checks-with-readiness-and-liveness-probes)

###### &lt;traceability.bpn&gt;

BPN (Business Partner Number) for the traceability app used to identify the partner in the network.

###### &lt;datasource.url&gt;

The jdbc connection string to the database. jdbc:postgresql://${url}:${port}/trace

###### &lt;datasource.username&gt;

The username of the datasource e.g. "trace"

###### &lt;datasource.password&gt;

The password of the datasource or the path to the vault which contains the secret. &lt;path:../data/int/database\#tracePassword&gt;

###### &lt;oauth2.clientId&gt;

Client ID for OAuth2 (KeyCloak). Request this from your Keycloak operator.

###### &lt;oauth2.clientSecret&gt;

Client secret for OAuth2. Request this from your OAuth2 operator.

###### &lt;oauth2.clientTokenUri&gt;

The URL of the Keycloak token API. Used by Trace-X for token creation to authenticate with other services.

###### &lt;oauth2.jwkSetUri&gt;

The URL of the Keycloak JWK Set. Used by Trace-X to validate tokens when Trace-X API is called.

###### &lt;oauth2.resourceClient&gt;

The client which is used to authenticate the backend.

###### &lt;edc.apiKey&gt;

The EDC api key or the path to the secret inside a vault. e.g. &lt;path:../data/int/edc/controlplane\#edc.api.control.auth.apikey.value&gt;

##### &lt;postgresql.enabled&gt;

Enables &lt;true&gt; or disables &lt;false&gt; PostgresSQL database.

##### &lt;postgresql.auth.postgresPassword&gt;

Database password for the **postgres** user or the path to the secret inside a vault. &lt;path:…​/data/int/database\#password&gt;

##### &lt;postgresql.auth.password&gt;

Database password for the application user of the path to the secret inside a vault. &lt;path:…​/data/int/database\#password&gt;

##### &lt;postgresql.auth.database&gt;

The name of the database instance.

##### &lt;postgresql.auth.username&gt;

The user for the database instance.

##### &lt;global.enablePrometheus&gt;

Enables &lt;true&gt; or disables &lt;false&gt; the prometheus instance.

##### &lt;global.enableGrafana&gt;

Enables &lt;true&gt; or disables &lt;false&gt; the grafana instance used for resource and application monitoring.

##### &lt;irs-helm.enabled&gt;

Enables &lt;true&gt; or disables &lt;false&gt; irs helm charts.

##### &lt;pgadmin4.enabled&gt;

Enables &lt;true&gt; or disables &lt;false&gt; pgadmin4 console for the PostgreSQL database instance

##### &lt;pgadmin4.ingress.enabled&gt;

Enables &lt;true&gt; or disables &lt;false&gt; a K8S Ingress for the pgadmin4 console

Troubleshooting
---------------

Coming soon…​

Last updated 2023-04-03 07:14:52 UTC
