# ![Product Traceability FOSS Backend Installation Guide](./catena-x-logo.svg) Product Traceability FOSS Backend Installation guide

## Clone the source locally:

```sh
$ git clone git@github.com:catenax-ng/product-traceability-foss-backend.git
$ cd product-traceability-foss-backend
```

## Local deployment
* Start the necessary infrastructure by running: ```docker-compose up``` inside [docker folder](../docker)
* Export environment variables required by the service:
  * `SPRING_DATASOURCE_URL` - with value `jdbc:postgresql://localhost:5432/trace`
  * `SPRING_DATASOURCE_USERNAME` - with value `trace` [see database initialization script](../docker/db-init/create_db.sql)
  * `SPRING_DATASOURCE_PASSWORD` - with value `docker` [see docker-compose file](../docker/docker-compose.yml)
  * `KEYCLOAK_OAUTH2_CLIENT_ID` - with Keycloak client registration id specific value
  * `KEYCLOAK_OAUTH2_CLIENT_SECRET` - with Keycloak client registration secret specific value
  * `MAILSERVER_HOST` - with host for the email server of your choice
  * `MAILSERVER_PORT` - with port for the email server of your choice
  * `MAILSERVER_USERNAME` - with username for the email server of your choice
  * `MAILSERVER_PASSWORD` - with password for the email server of your choice
  * `SPRING_PROFILES_ACTIVE` - with profile to be picked when starting the service. One of `[dev|int]`.
* Start the service by invoking following command in project root directory `./gradlew bootRun`

## Keycloak configuration
Product Traceability FOSS Backend relies on properly configured Keycloak instance. In order to work, Keycloak must be configured with proper realm, clients and it's roles.
Product Traceability FOSS Backend uses `CX-Central` realm with `Cl10-CX-Part` resource. Users should have one of the following roles assigned:
* User
* Admin
* Supervisor

## Helm secrets configuration

Product Traceability FOSS Backend ships with helm charts and utilize [helm dependency](https://helm.sh/docs/helm/helm_dependency/) functionality for 3rd party components.
In order to deploy the service following secrets needs to be provided for specific environment [see project helm environment specifc files](../charts/product-traceability-foss-backend):

### Keycloak

* `keycloak.oauth2.clientId` - Keycloak client registration id credentials
* `keycloak.oauth2.clientSecret` - Keycloak client registration secret credentials

### Database

* `postgresql.secret.initUserDbSql` - database initialization script, contains username and password for databases used by the service.
Please note that the final script should be encoded using Base64 encoding and then added to a secret. Sample command:
```sh
echo -n 'CREATE ROLE trace WITH LOGIN PASSWORD 'yourPassword';\nCREATE DATABASE trace;\nGRANT ALL PRIVILEGES ON DATABASE trace TO trace;' | base64
```


* `postgresql.auth.postgresPassword` - PostgreSQL master password
* `datasource.password` - `trace` database password configured in `initUserDbSql` script
* `pgAdmin4.env.password` - pgAdmin4 master password

### Email server

* `mailserver.host` - host for the email server of your choice
* `mailserver.port` - port for the email server of your choice
* `mailserver.username` - username for the email server of your choice
* `mailserver.password` - password for the email server of your choice

## API sample endpoints

* Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
* API docs: `http://localhost:8080/api/v3/api-docs`
* Application health liveness endpoint: `http://localhost:8081/actuator/health/liveness`
* Application health readiness endpoint: `http://localhost:8081/actuator/health/readiness`
