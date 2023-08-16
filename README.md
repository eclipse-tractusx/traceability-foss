<h1><img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/frontend/src/assets/images/logo.svg" alt="Trace-X - Product Traceability FOSS Frontend (TRACE-FOSS)" style="width:200px;"/>Traceability FOSS</h1>

[![Apache 2 License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://github.com/eclipse-tractusx/traceability-foss/LICENSE)
[![Quality Gate Backend](https://sonarcloud.io/api/project_badges/measure?project=eclipse-tractusx_traceability-foss-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=eclipse-tractusx_traceability-foss-backend)
[![Quality Gate Frontend](https://sonarcloud.io/api/project_badges/measure?project=eclipse-tractusx_traceability-foss-frontend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=eclipse-tractusx_traceability-foss-frontend)
[![Kics](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/kics.yml/badge.svg)](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/kics.yml)
[![Trivy](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/trivy.yml/badge.svg)](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/trivy.yml)
[![VeraCode Backend](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/veracode_backend.yml/badge.svg)](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/veracode_backend.yml)
[![VeraCode Frontend](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/veracode_frontend.yml/badge.svg)](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/veracode_frontend.yml)
[![Eclipse DASH IP Check](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/eclipse-dash.yml/badge.svg)](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/eclipse-dash.yml)

## Trace-X is a system for tracking parts along the supply chain.
#### A high level of transparency across the supplier network enables faster intervention based on a recorded event in the supply chain. This saves costs by seamlessly tracking parts and creates trust through clearly defined and secure data access by the companies and persons involved in the process.

## Table of Contents
- [Trace-X](https://github.com/eclipse-tractusx/traceability-foss/#trace-x-is-a-system-for-tracking-parts-along-the-supply-chain)
  - [How to contribute](https://github.com/eclipse-tractusx/traceability-foss/#how-to-contribute)
  - [Releasing](https://github.com/eclipse-tractusx/traceability-foss/#releasing)
  - [Environments](https://github.com/eclipse-tractusx/traceability-foss/#environments)
  - [The frontend application](https://github.com/eclipse-tractusx/traceability-foss/#the-frontend-application)
    - [Challenges and solutions](https://github.com/eclipse-tractusx/traceability-foss/#challenges-and-solutions)
    - [Prerequisites](https://github.com/eclipse-tractusx/traceability-foss/#frontend-prerequisites)
    - [Installation](https://github.com/eclipse-tractusx/traceability-foss/#frontend-installation)
    - [Getting started](https://github.com/eclipse-tractusx/traceability-foss/#getting-started)
    - [Application authentication](https://github.com/eclipse-tractusx/traceability-foss/#application-authentication)
    - [Application architecture & patterns](https://github.com/eclipse-tractusx/traceability-foss/#application-architecture--patterns)
    - [User guide](https://github.com/eclipse-tractusx/traceability-foss/#user-guide)
  - [The backend application](https://github.com/eclipse-tractusx/traceability-foss/#the-backend-application)
    - [Prerequisites](https://github.com/eclipse-tractusx/traceability-foss/#backend-prerequisites)
    - [Installation](https://github.com/eclipse-tractusx/traceability-foss/#backend-installation)
    - [Running tests](https://github.com/eclipse-tractusx/traceability-foss/#running-tests)
      - [Unit tests](https://github.com/eclipse-tractusx/traceability-foss/#unit-tests)
      - [Integration tests](https://github.com/eclipse-tractusx/traceability-foss/#integration-tests)
      - [Running all tests](https://github.com/eclipse-tractusx/traceability-foss/#running-all-tests)
  - [API documentation](https://github.com/eclipse-tractusx/traceability-foss/#api-documentation)
  - [Container Image](https://github.com/eclipse-tractusx/traceability-foss/#container-image)
  - [License](https://github.com/eclipse-tractusx/traceability-foss/#license)

## How to contribute

A detailed guide on how to contribute can be found [here](https://github.com/eclipse-tractusx/traceability-foss/blob/main/CONTRIBUTING.md).

## Releasing
Here is our [Releasing guide](https://github.com/eclipse-tractusx/traceability-foss/blob/main/docs/RELEASE.md).

## The frontend application

This application serves as a user entry point to the Catena-X network.

It's written in Typescript based on the `Angular` framework.
We decided on using Angular because of two important aspects.
Firstly, Angular comes with `strict guidelines`, which makes it harder to start working on for new developers, but for established developers it is `easy to start working with`.
Secondly, Angular `scales` perfectly in the long run. Because of the restricted possibilities and stricted guidelines it is hard to implement multiple solutions for the same problem. e.g. Storing data or routing.
With that in mind it made sense it chose Angular for an `open source` project.

Source files are exposed statically through the NGINX web server.

### Challenges and solutions

"Visualisation of traceability" is one of our most important feature, but once was one of our biggest problems.
We wanted to achieve something that is `visually pleasing`, is `easy to use` and `performs` very good in a `browser application`.
Our first approach was to use HTML canvas. But it turned out it is hard to perform accurate actions inside a canvas. That is why we decided on using the [D3 library](https://d3js.org/).
D3.js is a JavaScript library for manipulating documents based on data. D3 helps you bring data to life using HTML, SVG, and CSS.
Because it uses SVGs, we knew it will perform great. And we are able to have pinpoint accuracy when it comes to user actions.

### Frontend Prerequisites

* [Node.js 18](https://nodejs.org/en)
* [Angular CLI](https://angular.io/cli)
* [YARN](https://yarnpkg.com/)

### Frontend Installation

* see [Installation guide](https://github.com/eclipse-tractusx/traceability-foss/blob/main/frontend/INSTALL.md)

### Getting started

Clone the source locally:

```sh
$ git clone git@github.com:eclipse-tractusx/traceability-foss.git
$ cd traceability-foss/frontend
```

Install prerequisites:
1. install [node.js](https://nodejs.org/en/download/package-manager)
```sh
$ npm install --global yarn
$ npm install -g @angular/cli
$ yarn install
```

Start application with``yarn start`` and navigate to ``http://localhost:4200``.

### Application authentication

Please find [here](https://github.com/eclipse-tractusx/traceability-foss/blob/main/frontend/AUTHENTICATION.md) some important information about the app authentication.

### Application architecture & patterns

This [architecture](https://github.com/eclipse-tractusx/traceability-foss/blob/main/frontend/ARCHITECTURE.md) gives you a roadmap and best practices to follow when building an application
so that you end up with a well-structured app.

### User guide

A detailed [explanation](https://github.com/eclipse-tractusx/traceability-foss/blob/main/docs/src/docs/user/user-manual.adoc) of how to use the application.

## The backend application

### Backend Prerequisites

* JDK 17
* [Docker Engine](https://docs.docker.com/engine/)

### Backend Installation

* see [Installation guide](https://github.com/eclipse-tractusx/traceability-foss/blob/main/tx-backend/INSTALL.md)

### Running tests

#### Unit tests

To run unit tests invoke following command:

```sh
mvn clean test
```

#### Integration tests

Product Traceability FOSS Backend relies on [Testcontainers library](https://www.testcontainers.org/) in order to provide
persistence layer, thus [Docker Engine](https://docs.docker.com/engine/) is required to be running.

If you want to execute the integration tests with IntelliJ you must mark the src/integration/groovy folder as "Test Sources Root".

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

## API documentation
The project follows [OpenAPI Specification](https://swagger.io/specification/) in order to document implemented REST Endpoints. The documentation can be found under [/openapi directory](https://github.com/eclipse-tractusx/traceability-foss/blob/main/tx-backend/openapi/traceability-foss-backend.json)
or can be viewed in the Swagger UI accessing the url: `{projectBasePath}/api/swagger-ui/index.html`

## License

[Apache License 2.0](https://github.com/eclipse-tractusx/traceability-foss/blob/main/LICENSE)

## Notice for Docker image

This application provides container images for demonstration purposes.

Eclipse Tractus-X product(s) installed within the image:

DockerHub Backend: https://hub.docker.com/r/tractusx/traceability-foss
DockerHub Frontend: https://hub.docker.com/r/tractusx/traceability-foss-frontend

- GitHub: https://github.com/eclipse-tractusx/traceability-foss
- Project home: https://projects.eclipse.org/projects/automotive.tractusx
- Dockerfile Backend: https://github.com/eclipse-tractusx/traceability-foss/blob/main/Dockerfile
- Dockerfile Frontend: https://github.com/eclipse-tractusx/traceability-foss/blob/main/frontend/Dockerfile
- Project license: [Apache License, Version 2.0](https://github.com/eclipse-tractusx/traceability-foss/blob/main/LICENSE)

**Used base image**
- [eclipse-temurin:20-jre-alpine](https://github.com/adoptium/containers)
- Official Eclipse Temurin DockerHub page: https://hub.docker.com/_/eclipse-temurin
- Eclipse Temurin Project: https://projects.eclipse.org/projects/adoptium.temurin
- Additional information about the Eclipse Temurin images: https://github.com/docker-library/repo-info/tree/master/repos/eclipse-temurin

As with all Docker images, these likely also contain other software which may be under other licenses (such as Bash, etc from the base distribution, along with any direct or indirect dependencies of the primary software being contained).

As for any pre-built image usage, it is the image user's responsibility to ensure that any use of this image complies with any relevant licenses for all software contained within.
