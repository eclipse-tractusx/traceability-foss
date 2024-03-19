# Backend Testing Strategy

### Unit Tests

Unit tests are executed automatically during the GitHub workflow
[pull-request_backend.yml](./github/workflows/pull-request_backend.yml)
[sonar-scan-backend.yml](./workflows/sonar-scan-backend.yml)

#### Test Reports

https://sonarcloud.io/project/issues?resolved=false&id=eclipse-tractusx_traceability-foss-backend
[sonar-scan-backend.yml](./workflows/sonar-scan-backend.yml)

#### Test Coverage

- The current test coverage is [![Coverage](https://sonarcloud.io/project/overview?id=eclipse-tractusx_traceability-foss-backend&metric=coverage)](https://sonarcloud.io/project/overview?id=eclipse-tractusx_traceability-foss-backend).

- For more information on coverage see [Architecture Documentation - Development concepts](https://eclipse-tractusx.github.io/traceability-foss/docs/arc42/full.html#_development_concepts).

#### Testing Frameworks and Libraries

- The Trace-X project uses [JUnit5](https://junit.org/junit5/) with [AssertJ](https://github.com/assertj/assertj)
  and [Mockito](https://site.mockito.org/) and the testing capabilities of the
  [Spring Boot Framework](https://spring.io/projects/spring-boot) for unit testing.


### Integration Tests

Besides the Spring Boot features testing features the following frameworks and tools are used for integation testing:
- [Testcontainers](https://java.testcontainers.org/) for bootstrapping integration tests with real services wrapped in Docker containers
- [Wiremock](https://wiremock.org/) for building mock APIs in order to simulate dependencies

The Wiremock tests are intended to cover the Trace-X flow and communication
without the need of a running environment that includes all dependencies.
Wiremock Tests and their corresponding utilities are marked by the suffix `WiremockTest` respectively `WiremockSupport`.

- Furthermore, the following libraries are utilized:
    - [Awaitility](http://www.awaitility.org/) for expressing expectations of asynchronous code
      in a concise and easy to read manner.
    - [DataFaker](https://www.datafaker.net/) for creating fake data.

### Smoke Tests

n.a.

### Regression Tests

[Cucumber](https://cucumber.io/) for regression testing.

#### Cucumber Tests

- There are [Cucumber](https://cucumber.io/) that verify the response bodies in more detail.
- See the module `tx-cucumber-tests` and the [Cucumber Tests README](tx-cucumber-tests/README.md) for more information.

### Load Tests

n.a.

### Running tests

#### Unit tests

To run unit tests invoke following command:

```sh
mvn clean test
```

#### Integration tests

Product Traceability FOSS Backend relies on [Testcontainers library](https://www.testcontainers.org/) in order to provide
persistence layer, thus [Docker Engine](https://docs.docker.com/engine/) is required to be running.

To run integration tests via command line, invoke following command:

```sh
mvn -pl tx-models,tx-backend,tx-coverage -Dorg.slf4j.simpleLogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn -B verify
```

#### Running all tests

To run all tests invoke following command:

```sh
mvn -DskipTests=false clean verify
```

*Please note that this task depends on `integrationTest` task, so it's required to have [Docker Engine](https://docs.docker.com/engine/) running.*

