# Architecture documentation (arc42)

### Notice

This work is licensed under the [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0).

* SPDX-License-Identifier: Apache-2.0
* Licence Path: <https://creativecommons.org/licenses/by/4.0/legalcode>
* Copyright (c) 2021,2022,2023 Contributors to the Eclipse Foundation
* Copyright (c) 2022, 2023 ZF Friedrichshafen AG
* Copyright (c) 2022 ISTOS GmbH
* Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
* Copyright (c) 2022,2023 BOSCH AG
* Source URL: <https://github.com/eclipse-tractusx/traceability-foss>

## Introduction and goals

This chapter gives you an overview about the goals of the service,
in which context the service runs and which stakeholders are involved.

## Requirements overview

### What is the purpose of the Trace-X application

* Empower all companies from SMEs to large OEMs to participate in parts traceability with an open-source solution to easily take part in the Catena-X traceability use case.
* It is a standalone application which can be self-hosted.
* Display the relations of the automotive value chain based on a standardized IT model.
* Overview and transparency across the supplier network enable faster intervention based on recorded events in the supply chain.
* Notifications/Messages regarding quality-related incidents and a tool for inspecting the supply chain.

### Essential features

* List, view and publish manufactured parts based on BoM AsBuild
* List, view and publish planned parts based on BoM AsPlanned
* Filter and search functionality on part views
* Show detailed information on manufactured parts from AAS description assets and aspects
* Uses submodels SerialPart, AssemblyPartRelationship and Batch
* List and view supplier parts (AssemblyPartRelationship) based on BoM AsBuild lifecycle
* View parts and parts relations in a visualized parts tree
* Send and receive top-down notifications (quality investigations) along the supply chain
* Compliance with Catena-X guidelines

## Quality goals

The following table entries define overall Trace-X quality goals. The order of topics does not imply a priority of the quality goals.

| Quality goal | Motivation and description |
| --- | --- |
| Running reference application for Catena-X traceability | Consume traceability data, visualize it in a state-of-the-art frontend to the user and enable the exchange of notifications. |
| Accessible and easy to use | Enable SMEs to large enterprises. |
| Cloud agnostic solution | Trace-X is built as a reference architecture and able to be run on different cloud solutions. It uses Helm, i.e. Helm charts, so that it can easily be deployed on different systems. |
| Trustworthy application | Trace-X uses the Catena-X standards as a basis to fulfill the interoperability (with commercial as well as other solutions) and data sovereignty requirements. |
| Application reliability | The Trace-X architecture is set up so that provided part tree structures are consumed, aggregated and utilized to enable quality related actions such as notifications along the supply chain on which the costumers can rely. |
| Usability and user experience | Trace-X is aligned with the overarching UUX guidelines. This ensures ease of use for the user as well as a good user experience. |
| Security | Static Application Security Testing (SAST) and Dynamic Application Security Testing (DAST) are executed automatically and regularly. Findings are recorded and mitigated. |

## Stakeholder

The following table presents the stakeholders of Trace-X and their respective intentions.

| Who | Matters and concern |
| --- | --- |
| Software developer | * Make an impact on Catena-X by participating in open-source software. * Example implementation for own use cases and applications. |
| Operating environments | * Use Trace-X as a basis for industrialization. * Example implementation for own business applications, further features and fast ramp up. |
| Catena-X partners | * Take part in traceability use case. * See relationships in the value chain. * React on quality issues with standardized actions and notifications. |
| Larger OEMs / tiers | * Want to use this implementation for further development and integration into their system landscape. |
| SMEs | * Need a solution to view data provided into the Catena-X network. * Act in a standardized way with partners within CX. |

## Architecture constraints

### Technical constraints

| Name | Description |
| --- | --- |
| Interoperability | Trace-X must use the EDC together with Catena-X approved data models to guarantee interoperability between participants. |
| Data sovereignty | Data owners and data consumers have to use usage policies for offering, consuming and therefore transferring data. |
| Kubernetes for container orchestration | Catena-X requires the application to run in a Kubernetes environment, deployed via Helm charts. |
| Catena-X UUX guidance (CX style guide) | The frontend of Trace-X follows Catena-X-wide UUX consistency according to the CX style guide. |

### Organizational constraints

| Name | Description |
| --- | --- |
| Schedule | Start of development in July 2022. Further development in alignment with the foundation of the Catena-X Automotive Network e.V. requirements and timeline. |
| Process model | Iterative and incremental. The SAFe framework is used to align with Catena-X services, prerequisites, components and requirements to be Catena-X compatible. |
| Catena-X services / requirements | Trace-X needs to be Catena-X compliant and the application has to follow the CX standards as well as interact with the core services and components. |
| Release as open source | The source code - at least partially - is made available as open source and can be found in Github Catena-X ng as well as in Eclipse Tractus-X. |
| Technology Readiness Level (TRL) for products developed within the CX consortia | As Trace-X is a reference implementation, the Technology Readiness Level (TRL) must not be above TRL 8. |

### Political constraints

| Name | Description |
| --- | --- |
| Open source | FOSS licenses approved by the Eclipse foundation have to be used. |

### Development conventions

| Name | Description |
| --- | --- |
| Architecture documentation | Architectural documentation of Trace-X reference application in arc42-template terminology and structure. |
| Language | The project language is English to ensure the best possible accessibility for all participants. Therefore, classes, methods etc. are named in English and the documentation is written in English. |
| Test coverage | More than 80% of the complete source code has to be test covered. |

## System scope and context

Trace-X is an end-user application to visualize and utilize data provided to the Catena-X network. This includes the traceability of manufactured parts and batches as well as the shipped and supplied components. To utilize the CX open ecosystem it is necessary to exchange information on serialized parts and batches with supply chain partners in a standardized, data-sovereign and interoperable way. This section describes the environment of Trace-X, its intended users and which systems and components it interacts with.

## Business context

Trace-X exchanges data with any other traceability applications within the CX ecosystem. This is implemented by integrating the service into the CX network and the usage of required central components and services provided by CX.

![arc42_000](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_000.png)

### User

Trace-X can be deployed, hosted and run by every party that is part of the Catena-X network. They need to be registered, have a BPN and a technical user as well as provide valid credentials. An end-user in Trace-X can have roles provided by the CX portal (user, supervisor, admin).

### IRS

The Item Relationship Service is the component that is used by Trace-X to fetch data chains from Catena-X network. Data that is provided by IRS gets extracted, aggregated, transformed and is used for further actions in Trace-X. Further information can be found in the [IRS architecture documentation (arc42)](https://eclipse-tractusx.github.io/item-relationship-service/docs/arc42/).

### Catena-X network

Trace-X and IRS are retrieving necessary information and data from the Catena-X network (users, digital twins, aspects, submodels, business partner information). If CX services are unavailable, Trace-X will not be able to perform most of its work.

### Any other traceability app

Trace-X interacts with any other traceability app using the CX standards. This enables sovereign data exchange as well as receiving and sending notifications to interact between different parties.

## Technical context

### Component overview

#### Trace-X API

We provide a REST API that is consumed by Trace-X frontend in order to deliver Trace-X related features such as quality investigations or asset chain visibility.
Since the Trace-X component is the very last component in the Catena-X ecosystem we are mostly dependent on the other services and theirs APIs in other to deliver desired functionalities. The development of the services is not a part of the Trace-X application and each of the system that we utilize exposes a REST API that we consume and interact with directly.

Trace-X is a Spring Boot based application and is secured with the OpenID connector provider Keycloak and the OAuth2. This means for the companies, that utilize Trace-X component, it is required to obtain a technical user in order to be authorized to get access to the external components within Catena-X ecosystem.

In order to use the Trace-X frontend with the Trace-X backend, users need to authenticate themselves in order to be authorized to get access to the Trace-X.
In the frontend UI users provide valid credentials and the system generates a bearer token that it gets from Keycloak and attaches it to the HTTP header parameter Authorization.
Once a user is authorized and has a proper role in the Trace-X backend, the backend delegates HTTP calls to specific services on their behalf as technical user in order to fulfill specific functionalities.

#### [Outdated] Registry API

![arc42_001](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_001.png)

The Trace-X acts as a consumer of the asset administration shell registry component. Trace-X contains a restful client (REST template) that builds a REST call to the mentioned digital twin registry API based on its known URL (the AAS registry URL is configurable in Trace-X).
Requests contain "assetIds" provided by the component during asset synchronization. Like described in the above section, the security aspect is required in order to achieve a REST call against the AAS Registry. As a response, Trace-X gets the corresponding shells and shell descriptors utilized later for asset synchronization.
The HTTP(s) transport protocol is used for the REST call communication.

#### IRS API

![arc42_002](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_002.png)

Trace-X acts as a consumer of the IRS component. Trace-X contains a restful client (REST template) that build a REST call to the mentioned IRS API based on its known URL (the IRS URL is configurable in Trace-X).
The request contains details required to start an IRS fetch job provided by the component during asset synchronization. Like described in the above section, the security aspect is required in order to achieve a REST call against the IRS. As a response, Trace-X gets the created job id and periodically pulls for the job details that contains assets that will be uploaded to the system.
As mentioned above, the transport protocol HTTP(S) is used for the REST call communication.

#### [Outdated] Portal API

![arc42_003](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_003.png)

Trace-X acts as a consumer of the portal component.
Trace-X contains a restful client (REST template) that builds a REST call to the mentioned Portal API based on its known URL (the Portal URL is configurable in Trace-X).
The portal is used to authenticate users and requests against the backend.
As mentioned above, the transport protocol HTTP(S) is used for the REST call communication.

#### [Outdated] EDC API

![arc42_004](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_004.png)

The Trace-X acts as a consumer and provider of the EDC component.
In Trace-X we communicate with EDC directly only for the sake of fulfilling quality investigation functionality.
Specific use cases can be viewed in [Runtime view](../runtime-view/index.adoc) section.
For these purposes the integrated EDC clients in Trace-X are responsible for creating restful requests to the EDC component.
As mentioned above, the transport protocol HTTP(S) is used for the REST call communication.

## Solution strategy

This section contains summarized architectural overview. A comparison of the most important goals and the corresponding solution approaches.

## Introduction

Following table describes the quality goals of Trace-X (see chapter quality goals) and their matching solution approaches.

| Quality goal | Matching approaches in the solution |
| --- | --- |
| Running reference application for Catena-X traceability | * Published open-source, Trace-X application can be used as a reference by anyone. |
| Accessible and easy to use | * Established programming languages are used. * Backend written in Java. * Frontend written in Typescript based on the Angular framework. |
| Cloud agnostic solution | * Helm charts to support the deployment of the application in a Kubernetes environment. |
| Application reliability | * Data source is the Catena-X network. Data is fetched with IRS directly from the data owner and the digital twin registry of CX. * Trace-X can be hosted decentralized since it is an open-source reference implementation. |
| Security | * Static Application Security Testing (SAST) and Dynamic Application Security Testing (DAST) are executed automatically and regularly with tools as part of the pipeline. |

## Technology

Trace-X is developed using Java and the Spring Boot framework for the backend and Typescript based on the Angular framework for the frontend. This decision was taken due to the support of the frameworks as well as technical knowledge of the team members.

The application can be hosted using Docker and Kubernetes. This is commonly used and widespread. With this the application has no vendor lock in regarding the hosting provider.

The communication between frontend and backend is done using REST APIs. This is the standard method in the Catena-X landscape and makes the application components easy to use for any third party client.

For the database PostgreSQL is used.

## Structure

Trace-X is divided into two components: frontend and backend.
It roughly can be broken down into the following parts:

* Asset controllers to get asset information
* Dashboard controller to get dashboard related summed up information
* Registry controller to fetch assets from the digital twin registry
* Notification controller to get notification information and create EDC notification offers
* Submodel controller for providing asset data functionality
* Import controller for importing Trace-X data for data provisioning
* Contract controller to get information about contract agreements
* EDC controller to retrieve notifications
* IRS callback controller to retrieve asynchronous jobs completed by IRS
* Policy controller to retrieve information about policies
* BPN controller to retrieve information about business partners

The backend does a request to the digital twin registry utilizing the registry controller. Extracted data from the response is made available through the asset controller and the dashboard controller to the frontend.

## Building block view

## Blackbox overall system

### Component diagram

![arc42_005](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_005.png)
```bash
The interfaces show how the components interact with each other and which interfaces the TraceX is providing.

Component Diagram

```

### Component description

| Components | Description |
| --- | --- |
| IRS | The IRS consumes relationship information across the CX-network and builds the graph view. Within this documentation, the focus lies on the IRS. |
| EDC consumer | The EDC consumer component is there to fulfill the GAIA-X and IDSA-data sovereignty principles. The EDC consumer consists out of a control plane and a data plane. |
| EDC provider | The EDC provider component connects with EDC consumer component and forms the endpoint for the actual exchange of data. It handles automatic contract negotiation and the subsequent exchange of data assets for connected applications. |
| Submodel server | The submodel server offers endpoints for requesting the submodel aspects. |

## Level 1

### Component diagram

![arc42_006](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_006.png)

### Component description

| Components | Description |
| --- | --- |
| **Trace-X** | **Trace-X** is a system allowing the user to review the parts/assets catalogue, start a quality investigations and receive quality alerts. |
| **Trace-X API** | The **Trace-X API** is the interface over which the data consumer is communicating. |
| **AssetsController** | The **AssetsController** provides a REST interface for retrieving the parts/assets information. |
| **DashboardController** | The **DashboardController** provides a REST interface for retrieving overall statistics displayed on a dashboard screen. |
| **RegistryController** | The **RegistryController** provides a REST interface for retrieving the data from parts registry. |
| **ImportController** | The **ImportController** provides a REST interface for importing assets and publishing them in the Catena-X network. |
| **AssetRepository** | The **AssetRepository** is a component responsible for storing and getting assets from database. |
| **BPNRepository** | The **BPNRepository** is a component which stores BPN -> company name mappings. |
| **NotificationsRepository** | The **NotificationsRepository** is a component responsible for storing and holding status of sent/received notifications. |
| **Database** | The **database** is a place for storing assets, relations as well as sent/received notifications. |

## Runtime view

This section describes the different functionalities of Trace-X application.

## Assets

## Scenario 1: Return assets

This section describes what happens when user lists stored assets.
In this example, the user requests as built assets.
The same can be done with as planned assets.

![arc42_007](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_007.png)

### Overview

When a user requests stored assets, Trace-X checks if the user has an adequate role ('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER').
If yes, then the endpoint returns a pageable result of assets.

The returned pageable result can be empty if no suitable asset has been found.

## Scenario 2: Return specific assets

This section describes what happens when user searches for a specific asset.
This example shows the request of one as built asset.
The same can be done with as planned assets.

![arc42_008](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_008.png)

### Overview

When a user requests a specific asset, Trace-X checks if the user has an adequate role ('ROLE_ADMIN', 'ROLE_SUPERVISOR', 'ROLE_USER'). If yes, then the endpoint returns a precise asset for the given assetId, if it is found.

If no asset has been found for the given ID, an AssetNotFoundException is thrown.

## Notifications

## Receive quality notification

This sequence diagram describes the process of receiving a quality notification from another traceability partner.

![arc42_009](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_009.png)

### Overview

As for the sending of a quality notification also for receiving of a notification EDC is used to push data from a sender to a receiver.
To enable receiving a notification by a partner you need to

* Create notification endpoint for qualitynotifications/receive
* Create EDC assets
* Create EDC usage policies
* Create EDC contract definitions

Trace-X implements a functionality to create the assets and their corresponding policies in the admin panel.

With the notification asset it is possible to enable EDC contract negotiation and EDC data transfer based on access policies defined. Only if the sender is able to find the asset in the catalog offer and perform a successful contract negotiation there will be the possibility to push a notification to the specified http endpoint on the receiver side.

## Send quality notification

This sequence diagram describes the process of sending a quality notification between traceability applications.

![arc42_010](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_010.png)

### Overview

For the notification feature EDC is used to push data from a sender to a receiver.
To enable sending respective more precisely receiving a notification by a partner you need to

* Create notification endpoint for qualitynotifications/receive
* Create EDC assets
* Create EDC usage policies
* Create EDC contract definitions

Trace-X implements a functionality to create the assets and their corresponding policies in the admin panel. With the notification asset it is possible to enable EDC contract negotiation and EDC data transfer process so that the quality investigation can be pushed by the sender.

In the above UML sequence diagram the sending of quality notifications from Trace-X to a receiver (any other traceability application) is described.

## Data consumption

This sequence diagram describes the process of fetching data from a DTR and the Catena-X ecosystem.

![arc42_011](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_011.png)

### Overview

Data is fetched by a Trace-X instance using Digital Twin Registry (DTR), Item Relationship Service (IRS) and Trace-X consumer EDC.
For digital twins the Asset Administration Shell (AAS) standard is used. For fetching data with Trace-X, a Digital Twin Registry and an IRS instance are required. Data should represent parts, supplier and customer parts, parts tree / parts relations.

## Data Provisioning

This sequence diagrams describes the process of importing data from a Trace-X dataformat

### Module 1

Data will be imported by the Trace-X frontend into the Trace-X backend and will be persisted as an asset in a transient state.
The raw data which is needed for the shared services (DTR / EDC) will be persisted as well.

![arc42_012](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_012.png)
```bash

```

### Module 2

The frontend is able to select assets and publish / synchronize them with the shared services. DTR / EDC / submodel API.

![arc42_013](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_013.png)
```bash

```

### Module 3

The backend is able to persist the data in the DTR / EDC and allows to use IRS for resolving assets.

![arc42_014](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_014.png)
```bash

```

## Scenario 1: Receive import report

This section describes what happens when the user wants to get a report of the imported assets associated to a importJobId.
In this example, the user requests an import report.

### Overview

When a user requests an import report, Trace-X checks if the user has an adequate role ('ROLE_ADMIN', 'ROLE_SUPERVISOR').
If yes, then the endpoint returns an import report to the given importJobId.

If the importJobId is not known to Trace-X, an HTTP 404 error is returned.

## Scenario 2: Publish assets

This section describes user interaction when publishing assets

### Overview

When a user publishes assets, Trace-X checks if the user has an adequate role ('ROLE_ADMIN').
If yes, then the endpoint starts to publish assets to network.

## Scenario 3: Publish assets - error on EDC or DTR

This section describes user interaction when publishing assets fails due to EDC or DTR error (for example when the services are unavailable).

### Overview

When a user publishes assets, Trace-X checks if the user has an adequate role ('ROLE_ADMIN').
If yes, then the endpoint starts to publish assets to network.
If any of required services are not available or return an error response upon executing, flow assets are set to ERROR state and the user can retry publishing them at any time when services are available again.

## Data sovereignty

## Scenario 1: Return asset contract agreements

This section describes functionality and the behavior in case a user requests contract agreements from Trace-X via the Trace-X contracts API (/contracts).

### Overview

In case a user requests contract agreements, Trace-X checks if the user has required roles ('ROLE_ADMIN', 'ROLE_SUPERVISOR').
If yes, then the requested assets will be mapped to the related contract agreement id.
These contract agreement ids will be then requested on EDC side via POST (/management/v2/contractagreements/request) and GET (/management/v2/contractagreements/\{ContractAgreementId\}/negotiation) to get the relevant information.

The contract information is then returned by the endpoint as a pageable result.

If no asset ids are provided in the request, 50 contract agreement ids are handled by default.

## Policies

### Overview

#### Scenario 1: Startup interaction with the IRS policy store

The Trace-X instance defines a constraint which is required for data consumption and provisioning.
Trace-X retrieves all policies by IRS and validates if one of the policies contains the required constraint given by Trace-X.
If a policy with the constraint exists and is valid, the process ends. If the policy is not valid, it will create one with the given constraint.

This sequence diagram describes the process of retrieving or creating policies within the IRS policy store based on the constraint given by Trace-X.

![arc42_015](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_015.png)
```bash





```

#### Scenario 2: Startup interaction with EDC

The Trace-X instance uses the policy which includes the defined constraint and transforms it into a valid EDC policy request.
The EDC policy request will be used for creating a policy for the required notification contracts.

This sequence diagram describes the process of retrieving the correct policy by IRS policy store based on the constraint given by Trace-X and reuses it for creating an EDC policy.

![arc42_016](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_016.png)
```bash




```

#### Scenario 3: Provisioning of notifications

The Trace-X instance uses the policy which includes the defined constraint and reuses it for validation of catalog offers by the receiver EDC.

This sequence diagram describes the process of how the policy with the defined constraint will be used for validation of the catalog offers from the receiver EDC.

![arc42_017](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_017.png)

#### Scenario 4: Provisioning of assets

The Trace-X instance uses the policy which includes the defined constraint and reuses it for creating EDC assets.

This sequence diagram describes the process of how the policy with the defined constraint will be reused for registering EDC data assets.

![arc42_018](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_018.png)

## Scheduler

An overview of the scheduler tasks configured in the system.

|     |     |     |
| --- | --- | --- |
| Scheduler name | Execution interval | Description |
| PublishAssetsJob | Every hour at 30min | Publishes assets in IN_SYNCHRONIZATION state to core services. The process combines 'as-built' and 'as-planned' assets and initiates their publication for synchronization in the traceability system. |
| AssetsRefreshJob | Every 2 hours | Invokes the synchronization of asset shell descriptors with the decentralized registry. It ensures the latest asset information is fetched and updated in the system from external sources. |

## Deployment view

## Cross-cutting concepts

## Entity-relationship model

Please be informed that the 'as-planned' version currently lacks the database relations. However, kindly maintain the Entity-relationship model (ERM) in its current state.

```bash
image::./assets/arc42/arc42_019.png[]
```

#### Quality notifications

![arc42_020](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_020.png)
```bash

```

## Safety and security concepts

### Authentication / authorization

#### Trace-X API

The Trace-X API is secured using OAuth2.0 / Open ID Connect.
Every request to the Trace-X API requires a valid bearer token.
The JWT token should also contain two claims:

* 'bpn' which is equal to the configuration value from `API_ALLOWED_BPN` property
* 'resource_access' with the specific key for C-X environments.
The list of values will be converted to roles by Trace-X.
Currently, Trace-X API handles three roles: ***'User'*** and ***'Supervisor'*** and ***'Admin'.***

You can have a look at the [rights and role matrix](https://github.com/eclipse-tractusx/traceability-foss/blob/main/docs/src/docs/administration/system-overview.adoc#rights-and-role-matrix-of-trace-x) in the system overview of the administration guide.

#### Trace-X as EDC client

The Trace-X accesses the Catena-X network via the EDC consumer connector.
This component requires authentication via a Verifiable Credential (VC), which is provided to the EDC via the managed identity wallet.

The VC identifies and authenticates the EDC and is used to acquire access permissions for the data transferred via EDC.

### Credentials

Credentials must never be stored in Git!

## Architecture and design patterns

### Module / package structure

* Main domain name
  * Application
    * Optional: subdomain name (in case of inheritance) → following same structure as main domain
    * Mapper: holds the mapper of transforming domain models to response models
    * Service: holds the interface for implementation in the domain package
    * REST: holds the controller for providing the api for the domain
  * Domain
    * Optional: subdomain name (in case of inheritance)
    * Model: holds the domain model
    * Service: implementation of the interface provided in the application package
    * Repository: holds the interface for accessing the infrastructure package
  * Infrastructure
    * Optional: subdomain name (in case of inheritance)
    * Model: holds the technical entities
    * Repository: holds the data access layer
      * E.g. JPARepository / Impl
  * All models (request / response) used in the API should be saved in the tx-model project.
  To be reusable for cucumber testing.

## "Under-the-hood" concepts

### Exception and error handling

There are two types of potential errors in Trace-X:

#### Technical errors

Technical errors occur when there is a problem with the application itself, its configuration or directly connected infrastructure, e.g. the Postgres database.
Usually, the application cannot solve these problems by itself and requires some external support (manual work or automated recovery mechanisms, e.g. Kubernetes liveness probes).

These errors are printed mainly to the application log and are relevant for the health-checks.

#### Functional errors

Functional errors occur when there is a problem with the data that is being processed or external systems are unavailable and data cannot be sent / fetched as required for the process.
While the system might not be able to provide the required function at that moment, it may work with a different dataset or as soon as the external systems recover.

#### Rules for exception handling

##### Throw or log, don't do both

When catching an exception, either log the exception and handle the problem or rethrow it, so it can be handled at a higher level of the code.
By doing both, an exception might be written to the log multiple times, which can be confusing.

##### Write own base exceptions for (internal) interfaces

By defining a common (checked) base exception for an interface, the caller is forced to handle potential errors, but can keep the logic simple.
On the other hand, you still have the possibility to derive various, meaningful exceptions for different error cases, which can then be thrown via the API.

Of course, when using only RuntimeExceptions, this is not necessary - but those can be overlooked quite easily, so be careful there.

##### Central fallback exception handler

There will always be some exception that cannot be handled inside the code correctly - or it may just have been unforeseen.
A central fallback exception handler is required so all problems are visible in the log and the API always returns meaningful responses.
In some cases, this is as simple as a HTTP 500.

##### Dont expose too much exception details over API

It’s good to inform the user, why their request did not work, but only if they can do something about it (HTTP 4xx).
So in case of application problems, you should not expose details of the problem to the caller.
This way, we avoid opening potential attack vectors.

## Development concepts

### Build, test, deploy

Trace-X is built using Maven and utilizes all the standard concepts of it.
Test execution is part of the build process and a minimum test coverage of 80% is enforced.

The project setup contains a multi-module Maven build.
Commonly used classes (like the Trace-X data model) should be extracted into a separate submodule and reused across the project.
However, this is not a "one-size-fits-all" solution.
New submodules should be created with care and require a review by the team.

The Maven build alone only leads up to the JAR artifact of Trace-X.
To create Docker images, the Docker build feature is used.
This copies all resources into a builder image, builds the software and creates a final Docker image at the end that can then be deployed.

Although the Docker image can be deployed in various ways, the standard solution are the provided Helm charts, which describe the required components as well.

### Code generation

There are two methods of code generation in Trace-X:

#### Lombok

The Lombok library is heavily used to generate boilerplate code (like constructors, getters, setters, builders...).
This way, code can be written faster and this boilerplate code is excluded from test coverage, which keeps the test base lean.

#### Swagger / OpenAPI

The API uses OpenAPI annotations to describe the endpoints with all necessary information.
The annotations are then used to automatically generate the OpenAPI specification file, which can be viewed in the Swagger UI that is deployed with the application.

The generated OpenAPI specification file is automatically compared to a fixed, stored version of it to avoid unwanted changes of the API.

### Migration

Data migration is handled by flyway.

### Configurability

Trace-X utilizes the configuration mechanism provided by Spring Boot.
Configuration properties can be defined in the file `src/main/resources/application.yml`

Other profiles should be avoided.
Instead, the configuration can be overwritten using Spring’s external configuration mechanism (see <https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html).>
The operator must have total control over the configuration of Trace-X.

### Java style guide

We generally follow the [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html).

### API guide

We generally follow the [OpenAPI Specification](https://swagger.io/specification/).

### Docomentation style guide

We generally follow the [Google developer documentation style guide](https://developers.google.com/style).

### Unit and functional testing

#### General unit testing

* Code coverage >= 80%
* Writing methods which provide a response to be better testable (avoid void if feasible).
* Naming of unit tests are as follows:

![unit_test_naming](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/unit_test_naming.png)

* Use given/when/then pattern for unit test structuring.
E.g:

![given_when_then_pattern](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/given_when_then_pattern.png)

#### Integration testing

Each public API should have at least two integration tests (positive / negative).
For integration testing, the `tx-backend/src/main/resources/application-integration.yml` is used.
Additionally, you need to have a local Docker environment running.
For this, we recommend [Rancher Dektop](https://rancherdesktop.io/).

### Clean code

We follow the rules and behaviour of: <https://clean-code-developer.com/.>

### Secure coding standards

As there is no other guideline of C-X, we fix any vulnerabilities, exposures, flaw detected by one of our SAST, DAST, Pentesting tools which is higher than "Very Low".

![vulnerability_level](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/vulnerability_level.png)

### Trace-X technical class responsibilities

#### Controller

* Has only one dependency to a facade or a service or a validator annotation
* Has no own logic
* Includes the swagger documentation annotations
* Has an integration test class
* Uses a static mapper to transform a domain model into the response model
* Returns a ResponseEntity&lt;T>

#### Response object

* Should be a public version of the domain object
* Is a result of the transformation which will be done in the facade
* Is not necessary if the domain object can be fully public
* Is not allowed to be implemented in a repository or a DAO

#### Facade

* Should have multiple service classes injected
* Can be implemented in a controller

#### ServiceImpl

* Responsible for retrieving data from storage
* Performs business logic
* Can be a http client
* Returns a jpaEntity → domain object
* Should only be implemented in a controller through an interface

#### Repository

* Represents an interface to the underlying repository implementation which then uses the spring repository

#### Domain object

* Mapped from an entity or from received external data
* Will be used as a working model until it will finally be transformed to a response object or another domain which will be persisted later on

#### Config object

* Should have the suffix .config at the end of the class
* Includes beans which are automatically created by app startup

#### Constructing objects

* Using builder pattern
  * Currently, we are using the constructor to create objects in our application.
  Main reason is to provide immutable objects.
  * As the handling with big loaded constructors is not easy and error prone, it’s recommended to use the builder pattern to have a clear understanding about what we are creating at the point of implementation.
* Using lombok for annotation processing

## Operational concepts

### Administration

#### Configuration

Trace-X can be configured using two mechanisms:

##### application.yml

If you build Trace-X yourself, you can modify the application.yml config that is shipped with Trace-X.
This file contains all possible config entries for the application.
Once the Docker image has been built, these values can only be overwritten using the Spring external config mechanism (see <https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/boot-features-external-config.html),> e.g. by mounting a config file in the right path or using environment variables.

##### Helm chart

The most relevant config properties are exposed as environment variables and must be set in the Helm chart for the application to be able to run.
Check the Trace-X Helm chart in Git for all available variables.

### Scaling

If the number of consumers rises, Trace-X can be scaled up by using more resources for the deployment pod.
Those resources can be used to utilize more parallel threads to handle job execution.

### Clustering

Trace-X can run in clustered mode, as each running job is only present in one pod at a time.
Note: as soon as a resume feature is implemented, this needs to be addressed here.

### Logging

Logs are being written directly to stdout and are picked up by the cluster management.

### Monitoring

Currently, there is on monitoring supported in Trace-X.

## User experience

### User interface

#### Table design

Trace-X uses the following table design to build consistent and user-friendly tables:

![table-design](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-design.png)

| Component | Description | Example |
| --- | --- | --- |
| Actions | * Black icons * When an action cannot be executed for any reason, the icon turns grey * A tooltip is shown when hovering over an executable action to describe it * A tooltip is shown when hovering over a disabled action to describe the reason why it can’t be executed | ![table-actions](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-actions.png) |
| Action menus | * Opens when clicking on a three-dot menu * Disappears, when clicking anywhere outside the menu * List of action icons with text labels * The three-dot menu is sticky when used inside the table for single-item actions | ![table-action-menus](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-action-menus.png) |
| Multi-select box | * Clicking on it when no items are selected, selects all items on the current page * Clicking on it when some items are selected (but not all), selects all items on the current page * Clicking on it when all items are selected, deselects all items on the current page * Clicking on the small arrow opens a menu for clearing the page selection or the entire selection * The menu disappears, when clicking anywhere outside the menu * Part of the column header row -> Sticky on top next to column headers | ![table-multi-select-box](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-multi-select-box.png) |
| Selection box | * Clicking on it toggles item selection |  |
| Selection count | * Shows selection count of the current page |  |
| Column header | * Shows the column title * Hovering over it shows a tooltip that describes sorting behaviour * Clicking on it toggles sorting * Sticky on top | ![table-sorting-tooltip](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-sorting-tooltip.png) Ascending sorting: ![table-sorting-ascending](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-sorting-ascending.png) Descending sorting: ![table-sorting-descending](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-sorting-descending.png) |
| Filter | * Can search for any string * Results are shown directly without having to press enter * Results are shown as a sorted list (ascending) * Selected results are shown on top of unselected ones * The checkbox inside the search field selects or deselects every result * The cross inside the search field resets the search and the filter * When the filter is active, 'All' changes to the filtered value (+ the number of other filters) * For date values, the search is replaced with a date picker * Sticky on top below column headers | ![table-filter](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-filter.png) Date filter: ![table-filter-date](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-filter-date.png) |
| Quick filter | * Used to quickly filter for specific values * Independent of regular filters * Work exclusively - only one filter can be selected at a time * Click on the currently active quick filter to deactivate it |  |
| Table column settings | * Opens up as an overlay * Clicking on Save, the cross or anywhere outside the overlay will close the overlay (only saving changes when clicking Save) * Selecting/deselecting columns makes them visible/invisible in the table * The order of the columns can be changed by selecting the column title and using the arrow buttons * Using the circle arrow icon resets the column visibility and column order | ![table-column-settings](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-column-settings.png) |
| Full-width toggle | * Used to switch between full-width mode or variable-width mode |  |
| Page size selector | * Shows the currently selected page size * When clicking on it, a dropdown opens that shows available page size options * Clicking any option will select that option and close the dropdown * Clicking anywhere outside the dropdown closes the dropdown without applying any changes * Three options are always available | ![table-page-size-selector](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-page-size-selector.png) |
| Page information | * Shows information about the amount of items on the current page and the total amount of items |  |
| Page controls | * Used to switch between pages * Controls are: First page, previous page, next page, last page * Hovering over the controls shows a tooltip that labels each control | ![table-page-controls](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-page-controls.png) |

In addition, following tables are used within Trace-X:

When the data is not as complex and/or extensive and single-item actions are not needed:

![table-small](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-small.png)

When data must only be shown and no actions are needed:

![table-data-only](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/cross-cutting/user-experience/table-data-only.png)

## Quality requirements

This section includes concrete quality scenarios to better capture the key quality objectives but also other required quality attributes.

## Quality tree

The tree structure provides an overview for a sometimes large number of quality requirements.

![arc42_021](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/arc42/arc42_021.png)

## Quality scenarios

The initial letters of the scenario identifiers (IDs) in the following table each stand for the higher-level quality characteristic. For example M for maintainability. These identifiers are also used in the quality tree. The scenarios cannot always be clearly assigned to a characteristic. Therefore, they sometimes appear more than once in the quality tree.

| ID | Scenario |
| --- | --- |
| M01 | A developer with basic knowledge of the traceability use case looks for an introduction to the traceability architecture. He or she gets the idea of essential design very fast. |
| M02 | A senior developer looks for a reference implementation of the traceability use case functionalities. He or she gets it within the source code. |
| M03 | A developer wants to implement new features. He or she is able to add it to the source code easily. |
| M04 | A developer wants to implement a new frontend or change some components. The efforts can be reduced by using the standardized API endpoints to do so. |
| I01 | A user wants to switch from FOSS application to a COTS application or the other way round. This is possible since the application is interoperable with other applications within the CX network. |
| F01 | The application uses the Catena-X standards to ensure the correct interoperability and exchange with other participants. |
| F02 | An OEM or tier n supplier needs more information regarding specific parts. He can mark the parts in question and send a top-down notification (quality investigation) to the next entity / the partner. |
| F03 | A company wants to have more transparency and a visualized status of the supply / value chain. By using the application they are enabled to work with the structures and enable new features / functionalities. |
| F04 | Notifications are sent using the EDC to ensure interoperability and reliability to the CX network. |
| E01 | Notifications between traceability apps need to be send out and received within a specified timeframe to minimize the negative impact of e.g. recalled serialized products/batches on the value chain. |

## Glossary

| Term | Description |
| --- | --- |
| Aspect | Collection of various aspects from the digital twin. An aspect can, for example, bundle all information about a specific part. |
| AAS | ***A****sset **A****dministration **S***hell (Industry 4.0) is the implementation of the digital twin for Industry 4.0 and enables interoperability between different companies. |
| BoM | ***B****ill **o****f **M***aterial. BoM is a list of parts and materials that a product contains. Without them, manufacturing would not be possible. |
| BoM AsBuilt | Bill of material lifecycle of the built entity |
| BoM AsPlanned | Bill of material lifecycle of the planned entity |
| BPN | ***B****usiness **P****artner **N***umber. A BPN is used to identify a partner in the Catena-X network. |
| CX | Abbreviation for Catena-X |
| Data Sovereignty | Ability to keep control over own data, that is shared with other participants |
| EDC | ***E****clipse **D****ataspace **C***onnector. The connector version used in Catena-X |
| FOSS | ***F****ree and **O****pen **S****ource **S***oftware |
| Interoperability | Communication and interaction with other components or Catena-X partners/players |
| IRS | ***I****tem **R****elationship **S***ervice. Component to provide data chains. [IRS architecture documentation (arc42)](https://eclipse-tractusx.github.io/item-relationship-service/docs/arc42/) |
| SME | ***S****mall / **M****edium **E***nterprise |
| Trace-X | Proper name of open-source software application of Catena-X use case traceability |
| UI | ***U****ser **I***nterface |
