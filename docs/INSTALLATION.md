<div style="display: flex; align-items: center;justify-content: center;align-content: center;">
   <img src="./trace-x-logo.svg" alt="Product Traceability FOSS Backend Installation Guide" style="width:200px;"/>
   <h1 style="margin: 10px 0 0 10px">Product Traceability FOSS Backend Installation guide</h1>
</div>

## Clone the source locally:

```sh
$ git clone git@github.com:eclipse-tractusx/traceability-foss-backend.git
$ cd product-traceability-foss-backend
```

## Local deployment
* Start the necessary infrastructure by running: ```docker-compose up``` inside [docker folder](../docker)
* Export environment variables required by the service:
  * `SPRING_DATASOURCE_URL` - with value `jdbc:postgresql://localhost:5432/trace`
  * `SPRING_DATASOURCE_USERNAME` - with value `trace` [see database initialization script](../docker/db-init/create_db.sql)
  * `SPRING_DATASOURCE_PASSWORD` - with value `docker` [see docker-compose file](../docker/docker-compose.yml)
  * `OAUTH2_CLIENT_ID` - with OAuth2 provider client registration id specific value
  * `OAUTH2_CLIENT_SECRET` - with OAuth2 provider client registration secret specific value
  * `OAUTH2_PROVIDER_TOKEN_URI` - with OAuth2 provider url to obtain tokens
  * `OAUTH2_JWK_SET_URI` - with OAuth2 provider certs url
  * `JWT_RESOURCE_CLIENT` - with JWT resource client name
  * `SPRING_PROFILES_ACTIVE` - with profile to be picked when starting the service. One of `[dev|int]`.
* Start the service by invoking following command in project root directory `./gradlew bootRun`

## OAuth2 configuration
Product Traceability FOSS Backend relies on properly configured OAuth2 instance. In order to work, it must be configured with proper realm, clients and roles.
Users should have one of the following roles assigned:
* User
* Admin
* Supervisor

## Helm secrets configuration
Product Traceability FOSS Backend ships with helm charts and utilize [helm dependency](https://helm.sh/docs/helm/helm_dependency/) functionality for 3rd party components.
In order to deploy the service following secrets needs to be provided for specific environment [see project helm environment specifc files](../charts/product-traceability-foss-backend):

### OAuth2
* `oauth2.clientId` - OAuth2 client registration id credentials
* `oauth2.clientSecret` - OAuth2 client registration secret credentials

### Database
* `postgresql.secret.initUserDbSql` - database initialization script, contains username and password for databases used by the service.
Please note that the final script should be encoded using Base64 encoding and then added to a secret. Sample command:
```sh
echo -n 'CREATE ROLE trace WITH LOGIN PASSWORD 'yourPassword';\nCREATE DATABASE trace;\nGRANT ALL PRIVILEGES ON DATABASE trace TO trace;' | base64
```

* `postgresql.auth.postgresPassword` - PostgreSQL master password
* `datasource.password` - `trace` database password configured in `initUserDbSql` script
* `pgAdmin4.env.password` - pgAdmin4 master password

## API sample endpoints
* Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
* API docs: `http://localhost:8080/api/v3/api-docs`
* Application health liveness endpoint: `http://localhost:8081/actuator/health/liveness`
* Application health readiness endpoint: `http://localhost:8081/actuator/health/readiness`
