# \[Concept\] \[#ID#\] Summary

| Key           | Value             |
|---------------|-------------------|
| Autor         | <name>            |
| Creation date | <DD.MM.YYYY>      |
| Ticket Id     | <ID> <url>        |
| State         | <DRAFT,WIP, DONE> |

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

1. Identifiability and findability:
2. Traversing Across mutliple tier levels:
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



## Describe Part at type and instance level

| Level          | Name          | Description                                                    |
|----------------|---------------|----------------------------------------------------------------|
| Type Level     | CatalogueType | Type Level (part number, material number, catalog part)        |
| Instance Level | SerialPart    | Instance Level (vehicle, ECUs, serialized parts, batches, etc) |
| Instance Level | Batch         | Instance Level (vehicle, ECUs, serialized parts, batches, etc) |
| Instance Level | JiS           | Instance Level (vehicle, ECUs, serialized parts, batches, etc) |

CX-0019 Aspect Model SerialPart
CX-0020 Aspect Model SingleLevelBomAsBuilt
CX-0021 Aspect Model: Batch


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
| JIS          | Just In Sequence        | Just in sequence part                                    |

# References

# Additional Details
Given the dynamic nature of ongoing development, there might be variations between the conceptualization and the current implementation. For the latest status, refer to the documentation.
