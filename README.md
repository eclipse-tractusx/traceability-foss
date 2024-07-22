<h1><img src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/frontend/src/assets/images/logo.svg" alt="Trace-X - Product Traceability FOSS Frontend (TRACE-FOSS)" style="width:200px;"/>Traceability FOSS</h1>

[![Apache 2 License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](https://github.com/eclipse-tractusx/traceability-foss/blob/main/LICENSE)
[![CC BY 4.0 License](https://img.shields.io/badge/Non--code_license-CC%20BY%204.0-orange.svg)](https://github.com/eclipse-tractusx/traceability-foss/blob/main/LICENSE_non-code)
[![QG Backend](https://sonarcloud.io/api/project_badges/measure?project=eclipse-tractusx_traceability-foss-backend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=eclipse-tractusx_traceability-foss-backend)
[![QG Frontend](https://sonarcloud.io/api/project_badges/measure?project=eclipse-tractusx_traceability-foss-frontend&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=eclipse-tractusx_traceability-foss-frontend)
[![Coverage Backend](https://sonarcloud.io/api/project_badges/measure?project=eclipse-tractusx_traceability-foss-backend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=eclipse-tractusx_traceability-foss-backend)
[![Coverage Frontend](https://sonarcloud.io/api/project_badges/measure?project=eclipse-tractusx_traceability-foss-frontend&metric=coverage)](https://sonarcloud.io/summary/new_code?id=eclipse-tractusx_traceability-foss-frontend)
[![Kics](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/kics.yml/badge.svg)](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/kics.yml)
[![Trivy](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/trivy.yml/badge.svg)](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/trivy.yml)
[![Eclipse DASH IP Check](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/eclipse-dash.yml/badge.svg)](https://github.com/eclipse-tractusx/traceability-foss/actions/workflows/eclipse-dash.yml)
[![[BE] Dependency check](https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/dependency-check.yml/badge.svg)](https://github.com/catenax-ng/tx-traceability-foss/actions/workflows/dependency-check.yml)

## Trace-X is a system for tracking parts along the supply chain.

A high level of transparency across the supplier network enables faster intervention based on a recorded event in the supply chain. This saves costs by seamlessly tracking parts and creates trust through clearly defined and secure data access by the companies and persons involved in the process.

## Table of Contents
- [Trace-X](#trace-x-is-a-system-for-tracking-parts-along-the-supply-chain)
  - [Introduction](#introduction)
    - [Vision and Mission Statement](#vision-and-mission-statement)
    - [Trace-X Feature list](#trace-x-feature-list)
  - [How to contribute](#how-to-contribute)
  - [Releasing](#releasing)
  - [Environments](#environments)
  - [Frontend Application](#frontend-application)
    - [Challenges and solutions](#challenges-and-solutions)
    - [Prerequisites](#frontend-prerequisites)
    - [Installation](#frontend-installation)
    - [Getting started](#getting-started)
    - [Application authentication](#application-authentication)
    - [Application architecture & patterns](#application-architecture--patterns)
    - [User guide](#user-guide)
    - [Frontend Testing strategy](#frontend-testing-strategy)
  - [The backend application](#the-backend-application)
    - [Prerequisites](#backend-prerequisites)
    - [Installation](#backend-installation)
    - [Backend Testing strategy](#backend-testing-strategy)
  - [API documentation](#api-documentation)
  - [Container Image](#container-image)
  - [Licenses](#licenses)
  - [Notice for Docker image](#notice-for-docker-image)
  - [Contact](#contact)

## Introduction

### Vision and Mission Statement
Trace-X empowers all companies from SMEs to large OEMs to participate in parts traceability with an Open-Source solution.

The Open-Source Traceability application is developed within the Catena-X project and enables all companies to participate in Parts Traceability.
Trace-X offers capabilities to ingest data for serialized parts and batches as well as their child components. Within CX, we strive to establish a standardized, data-sovereign and interoperable exchange of traceability data along the value creation chain.

The application gives an overview of the supplier network and the supply chain. A high level of transparency across the supplier network enables faster intervention based on recorded events in the supply chain. Additionally, automated massages regarding Quality related incidents and a tool for inspecting the supply chain helps companies in these fast-moving times.

All this saves' costs by seamlessly tracking parts as well as creates trust through clearly defined and secure data access by the companies and persons involved in the process.

### Trace-X Feature list
Trace-X as the Open-Source solution for Parts Traceability offers the following functionalities:

* List and view manufactured parts based on BoM AsBuilt
* List and view planned parts based on BoM AsPlanned
* Filter and search functionality on part views
* Show detailed information on manufactured parts
  * Asset Administration Shell description
  * Submodel description such as
     * SerialPart
     * SingleLevelBomAsBuilt
     * SingleLevelUsageAsBuilt
     * Batch
     * JustInSequence
     * PartAsPlanned
     * SingleLevelBomAsPlanned
     * SingleLevelUsageAsPlanned
* List and view Supplier parts based on BoM As Built
* List of Supplier parts based on SingleLevelBomAsBuilt/SingleLevelUsageAsBuilt Aspect
* View parts and parts relations in parts tree
* Send and receive quality investigations along the supply chain
  * Quality Investigations (Company to Supplier)
  * Quality Alerts (Company to Customers)

## How to contribute

A detailed guide on how to contribute can be found [here](CONTRIBUTING.md).

## Releasing
Here is our [Releasing Guide](docs/RELEASE.md).

## Frontend Application

This application serves as a user entry point to the Catena-X network.

It's written in Typescript based on the [Angular](https://angular.dev/) framework.
We decided on using Angular because of two important aspects.
Firstly, Angular comes with `strict guidelines`, which makes it harder to start working on for new developers, but for established developers it is `easy to start working with`.
Secondly, Angular `scales` perfectly in the long run. Because of the restricted possibilities and strict guidelines it is hard to implement multiple solutions for the same problem. e.g. Storing data or routing.
With that in mind it made sense it chose Angular for an `open source` project.

Source files are exposed statically through the NGINX web server.

### Challenges and Solutions

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

* see [Installation guide](frontend/INSTALL.md)

### Getting started

Clone the source locally:

```bash
$ git clone git@github.com:eclipse-tractusx/traceability-foss.git
$ cd traceability-foss/frontend
```

#### Install prerequisites:

install [node.js](https://nodejs.org/en/download/package-manager)
```bash
$ npm install --global yarn
$ npm install -g @angular/cli
$ yarn install
```

Start application with ``yarn start`` and navigate to ``http://localhost:4200``.

### Application Authentication

Please find [here](frontend/AUTHENTICATION.md) some important information about the app authentication.

### Application Architecture & Patterns

This [architecture](frontend/ARCHITECTURE.md) gives you a roadmap and best practices to follow when building an application
so that you end up with a well-structured app.

### User Guide

A detailed [explanation](docs/user/user-manual.adoc) of how to use the application.

### Frontend Testing Strategy
See [TESTING](frontend/TESTING.md).

## Backend Application

### Backend Prerequisites

* JDK 17
* [Docker Engine](https://docs.docker.com/engine/)

### Backend Installation

* see [Installation guide](tx-backend/INSTALL.md)

### Backend Testing Strategy
See [TESTING](tx-backend/TESTING.md).

## API Documentation
The project follows [OpenAPI Specification](https://swagger.io/specification/) in order to document implemented REST Endpoints. The documentation can be found under [/openapi directory](https://github.com/eclipse-tractusx/traceability-foss/blob/main/tx-backend/openapi/traceability-foss-backend.json)
or can be viewed in the Swagger UI accessing the url: `{projectBasePath}/api/swagger-ui/index.html`

## Licenses

* [Apache License 2.0](LICENSE)
* [CC BY 4.0 License](LICENSE_non-code)

## Notice for Docker Image

Below you can find the information regarding Docker Notice for this application.

- [Traceability Backend Docker Notice](DOCKER_NOTICE.md)
- [Traceability Frontend Docker Notice](frontend/DOCKER_NOTICE.md)

## Contact

Contact the Eclipse Tractus-X developers via the developer mailing list.

* https://accounts.eclipse.org/mailing-list/tractusx-dev

Contact the project developers via eclipse matrix chat.

* Eclipse Matrix Chat https://chat.eclipse.org/#/room/#tractusx-trace-x:matrix.eclipse.org
