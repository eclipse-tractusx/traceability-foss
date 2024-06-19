<div style="display: flex; align-items: center;justify-content: center;align-content: center;">
   <img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/trace-x-logo.svg" alt="Product Traceability FOSS Backend Installation Guide" style="width:200px;"/>
   <h1 style="margin: 10px 0 0 10px">Product Traceability FOSS Backend Installation guide</h1>
</div>

## Clone the source locally:

```sh
$ git clone https://github.com/eclipse-tractusx/traceability-foss.git
$ cd traceability-foss
```

## Local deployment
* Start the necessary infrastructure by running: ```docker-compose up```
* Export environment variables required by the service:
  * `SPRING_DATASOURCE_URL` - with value `jdbc:postgresql://localhost:5432/trace`
  * `SPRING_DATASOURCE_USERNAME` - with value `trace`
  * `SPRING_DATASOURCE_PASSWORD` - with value `docker`
  * `OAUTH2_CLIENT_ID` - with OAuth2 provider client registration id specific value
  * `OAUTH2_CLIENT_SECRET` - with OAuth2 provider client registration secret specific value
  * `OAUTH2_PROVIDER_TOKEN_URI` - with OAuth2 provider url to obtain tokens
  * `OAUTH2_JWK_SET_URI` - with OAuth2 provider certs url
  * `JWT_RESOURCE_CLIENT` - with JWT resource client name
  * `SPRING_PROFILES_ACTIVE` - with profile to be picked when starting the service. One of `[dev|int]`.
  * `EDC_PROVIDER_URL` - with url for the EDC provider
  * `TRACEABILITY_URL` - with url for the backend
* Start the service by invoking following command in project root directory `mvn spring-boot:run`

## OAuth2 configuration
Product Traceability FOSS Backend relies on properly configured OAuth2 instance. In order to work, it must be configured with proper realm, clients and roles.
Users should have one of the following roles assigned:
* User
* Admin
* Supervisor

## Helm secrets configuration
Product Traceability FOSS Backend ships with helm charts and utilize [helm dependency](https://helm.sh/docs/helm/helm_dependency/) functionality for 3rd party components.
In order to deploy the service following secrets needs to be provided for specific environment [see project helm environment specifc files](https://github.com/eclipse-tractusx/traceability-foss/blob/main/charts/traceability-foss-backend):

### OAuth2
* `oauth2.clientId` - OAuth2 client registration id credentials
* `oauth2.clientSecret` - OAuth2 client registration secret credentials

### Database
To start a database for local development, go to the docker directory and run.

```sh
docker compose up -d
```

Add the database configuration to your [application-local.yml](https://github.com/eclipse-tractusx/traceability-foss/blob/main/backend/src/main/ressouces/application-local.yml)

```yaml
spring:
    datasource:
        url: jdbc:postgresql://localhost:5432/trace
        username: postgres
        password: docker
    flyway:
        clean-on-validation-error: false
```
Database scripts are executed with Flyway. Put the scripts at [migration](https://github.com/eclipse-tractusx/traceability-foss/blob/main/backend/src/main/resources/db/migration)

* `postgresql.secret.initUserDbSql` - database initialization script, contains username and password for databases used by the service.
Please note that the final script should be encoded using Base64 encoding and then added to a secret. Sample command:
```sh
echo -n 'CREATE ROLE trace WITH LOGIN PASSWORD 'yourPassword';\nCREATE DATABASE trace;\nGRANT ALL PRIVILEGES ON DATABASE trace TO trace;' | base64
```

* `postgresql.auth.postgresPassword` - PostgreSQL master password
* `datasource.password` - `trace` database password configured in `initUserDbSql` script
* `pgAdmin4.env.password` - pgAdmin4 master password


## Helm installation
Add the Trace-X Backend Helm repository:


```sh
$ helm repo add traceability-foss-backend https://eclipse-tractusx.github.io/traceability-foss-backend
```
Then install the Helm chart into your cluster:

```sh
$ helm install -f your-values.yaml traceability-foss backend/traceability-foss-backend
```

== Deployment using ArgoCD

Create a new Helm chart and use Trace-X as a dependency.

```yaml
dependencies:
- name: traceability-foss-backend
  alias: backend
  version: x.x.x
  repository: "https://eclipse-tractusx.github.io/traceability-foss-backend/"
```

Then provide your configuration as the values.yaml of that chart.

Create a new application in ArgoCD and point it to your repository / Helm chart folder.

## API sample endpoints
* Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
* API docs: `http://localhost:8080/api/v3/api-docs`
* Application health liveness endpoint: `http://localhost:8081/actuator/health/liveness`
* Application health readiness endpoint: `http://localhost:8081/actuator/health/readiness`
