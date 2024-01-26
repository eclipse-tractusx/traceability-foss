# IndustryCore Changes #437

| Key           | Value             |
|---------------|-------------------|
| Autor         | @ds-mkanal        |
| Creation date | 26.01.2024        |
| Ticket Id     | https://github.com/eclipse-tractusx/traceability-foss/issues/437        |
| State         | WIP |

# Table of Contents
1. [Overview](#overview)
2. [Summary](#summary)
3. [Problem Statement](#problem-statement)
4. [Requirements](#requirements)
5. [NFR](#nfr)
6. [Out of scope](#out-of-scope)
7. [Assumptions](#assumptions)
8. [Definition of the Industry Core](#definition-of-the-industry-core)
9. [Glossary](#glossary)
10. [References](#references)
11. [Additional Details](#additional-details)


# Overview

The "Industry Core" is a fundamental element in the C-X network or system architecture.
It provides essential functions for identification, traversal, data flow, configuration, and communication in a modular and
interconnected environment.
The goal is to make components identifiable and discoverable at type and instance levels.
Enable seamless traversal across different tier levels.
Facilitate the creation of data chains.
Crucial role in configuring enablement services for component-based data exchange
Notification base message exchange.

1. Identifiability and findability
2. Traversing Across multiple tier levels
3. Facilitate Data Chains
4. Configuration for Enablement Services
5. Components-Based Data Exchange
The core enables the sending of notifications, indicating that there's a mechanism for alerting or informing relevant parties about specific events or changes within the system.

# Summary

# Problem Statement

# Requirements

# NFR

# Out of scope

# Assumptions

# Definition of the Industry Core

- [CX-0127-IndustryCorePartInstance#1.0.0](https://github.com/catenax-eV/product-standardization-prod/tree/CX-0127-IndustryCorePartInstance-v1.0.0/standards/CX-0127-IndustryCorePartInstance)

## Standards to be complied with

| Fullfilled | Standard                                         | Description                                                                     |
|------------|--------------------------------------------------|---------------------------------------------------------------------------------|
| [x]       | CX-0002 Digital Twins in Catena-X 2.2.0          | Trace-Xs notification process and consumption of assets is bases on DT standards |
| [x]       | CX-0018 Eclipse Data Space Connector (EDC) 2.1.0 | Data consumption and data provision as well as the sending and receiving of messages takes place exclusively via the EDC                   |
|

- [x]
CX-0018 Eclipse Data Space Connector (EDC) 2.1.0

## Describe Part at type and instance level

| Level          | Name          | Description                                                                      | Standard             |
|----------------|---------------|----------------------------------------------------------------------------------|----------------------|
| Type Level     | CatalogueType | Type Level (part number, material number, catalog part)                          |                      |
| Instance Level | SerialPart    | Instance Level (vehicle, ECUs, serialized parts, batches, etc)                   | CX-0019 Aspect Model |
| Instance Level | Batch         | Instance Level (vehicle, ECUs, serialized parts, batches, etc)                   | CX-0021 Aspect Model                     |
| Instance Level | JiS / JustInSequencePart        | Instance Level (vehicle, ECUs, serialized parts, batches, etc)                   |                      |
|Instance Level | SingleLevelBomAsBuilt| RBuilts relationsship between parts in downwards direction (customer > supplier) |    CX-0020 Aspect Model                  |
| Instance Level | PartSiteInformationAsBuilt  | (shared aspect)                                                                  |                      |


# Changes in AAS
| Key        | Availability                             | Description                                                                                                                                                                                                                                           |
|------------|------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| manufacturerId | Mandatory                                | The Business Partner Number (BPNL) of the manufacturer of the part.                                                                                                                                                                                   |
|manufacturerPartId | Mandatory                                |                                                                                                                                                                                                                                                       |
|customerPartId | Optional                                 |                                                                                                                                                                                                                                                       |
|assetLifecyclePhase | asPlanned(Mandatory) / asBuilt(Optional) | @Deprecated For serialized parts, batches, and JIS parts, use the value AsBuilt. For catalog parts in a Digital Twin As-Planned lifecycle phase, use the value AsPlanned.                                                                             |
|digitalTwinType | Mandatory                                | digitalTwinType="PartType" OR digitalTwinType="PartInstance" For parts on an instance level (e.g. serialized parts, batches, and JIS parts), use the value PartInstance. For parts on a part type level (e.g. catalog parts), use the value PartType. |                                                                                                                                                                       | |

# Changes in SerialPart
| Key        | Availability                             | Description                                                                                                                                                                                                                                           |
|------------|------------------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| partInstanceId | Mandatory | |
| van | Optioonal | |


## Discoverable in the network

- [ ] re-definition if the "specific asset ids"

## Enable traversing across serveral tier levels
- Industry Core aspect models
- DataChain KIT

## Configure the necessary enablement services
- EDC Setup (BPN-S/BPN-L, Policy Handling of the Core)

## Sending of notifications
- Header
- EDC-Assets

## Core Elements
- [CX-0002-DigitalTwinsInCatenaX](https://github.com/catenax-eV/product-standardization-prod/tree/CX-0127-IndustryCorePartInstance-v1.0.0/standards/CX-0002-DigitalTwinsInCatenaX)


# Glossary

| Abbreviation | Name                    | Description                                              |
|--------------|-------------------------|----------------------------------------------------------|
| ECU          | Electronic Control Unit | An ECU's main job is to keep the engine working smoothly |
| JIS          | Just In Sequence        | Just-in-sequence is a delivery concept where parts are delivered to the production plant at a requested time in the exact order of installation, typically for a 1:1 dependency on the manufactured product. A just-in-sequence-part is a part for which this concept and order of delivery applies and which does not have a dedicated serial number (then it would be considered a serialized part). Examples for JIS-parts are seats and bumpers.                                    |
| BPN | Business Partner Number | A BPN is the unique identifier of a partner within Catena-x. |
|  |Part Instance|A part instance is a physically produced instance (e.g. serialized part, batch, just-in-sequence-part) of a part type. |
|  |Part Type| A part type is a generic (not physically produced) part on material- or catalog-level as a representation for a designed part.|
| VAN | Vehicle Anonymised Number | A number mapped 1:1 to VIN, but pseudonomised.|

# References

# Additional Details
Given the dynamic nature of ongoing development, there might be variations between the conceptualization and the current implementation. For the latest status, refer to the documentation.
