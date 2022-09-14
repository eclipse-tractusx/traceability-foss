# Product traceability foss backend

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=catenax-ng_product-traceability-foss-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=catenax-ng_product-traceability-foss-backend)
[![Kics](https://github.com/catenax-ng/product-traceability-foss-backend/actions/workflows/kics.yml/badge.svg)](https://github.com/catenax-ng/product-traceability-foss-backend/actions/workflows/kics.yml)
[![Trivy](https://github.com/catenax-ng/product-traceability-foss-backend/actions/workflows/trivy.yml/badge.svg)](https://github.com/catenax-ng/product-traceability-foss-backend/actions/workflows/trivy.yml)
[![VeraCode](https://github.com/catenax-ng/product-traceability-foss-backend/actions/workflows/veracode.yaml/badge.svg)](https://github.com/catenax-ng/product-traceability-foss-backend/actions/workflows/veracode.yaml)


## Running tests

### Unit tests

```
./gradlew test
```

### Integration tests

```
./gradlew integrationTest
```

## Generating OpenAPI clients
OpenAPI clients are generated based on openapi specification see [dir](./openapi) and correlated gradle tasks see [configuration](build.gradle.kts).

OpenAPI tasks have dependency on JavaCompile task that is OpenAPI client classes are automatically generated once you build the project
and there should not be a need to generate them manually, however following command can be used for such purpose:
```
./gradlew generateBpnApi
```

## API documentation
The project follows OpenAPI specification in order to document implemented REST Endpoints. The documentation can be found under [/openapi directory](/openapi/product-traceability-foss-backend.json)
or can be viewed in the Swagger UI accessing the url: `{projectBasePath}/api/swagger-ui/index.html`

## Coding styles

To maintain coding styles we utilize [EditorConfig](https://editorconfig.org/) tool, see [configuration](.editorconfig)
file for the details.

### IDE plugins

* IntelliJ IDEA ships with built-in support for .editorconfig files
* [Eclipse plugin](https://github.com/ncjones/editorconfig-eclipse#readme)
* [Visual studio code plugin](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig)
