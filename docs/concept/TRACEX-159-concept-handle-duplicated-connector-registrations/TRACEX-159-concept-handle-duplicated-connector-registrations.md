# \[Concept\] \[TRACEX-159\] Handle duplicated connector registrations

| Key           | Value      |
|---------------|------------|
| Author        | mkanal     |
| Creation date | 16.12.2024 |
| Ticket Id     | TRACEX-159 |
| State         | WIP        |

# Table of Contents
1. [Overview](#overview)
2. [Summary](#summary)
3. [Problem Statement](#problem-statement)
4. [Requirements](#requirements)
5. [NFR](#nfr)
6. [Out of scope](#out-of-scope)
7. [Assumptions](#assumptions)
8. [Concept](#concept)
9. [Glossary](#glossary)
10. [References](#references)
11. [Additional Details](#additional-details)


# Overview
Specification of the EDC Discovery Process

# Summary
To ensure reliable and consistent functionality of the EDC Discovery process,
it is essential to define a clear and robust specification.
This specification should address all critical aspects of retrieving EDC URLs from EDC discovery service and those stored database .

# Problem Statement
The current implementation of the EDC Discovery process lacks a well-defined specification,
leading to inconsistencies and operational issues.
Specifically, multiple identical URLs have been registered for the same Business Partner Number (BPN), resulting in an invalid state.

# Requirements
Currently, it is possible to register multiple EDC connectors with the same URL. This leads to an issue where Trace-X, when attempting to send notifications, considers the duplicate connectors and reports success for the same URL twice. As a result, two notifications are created and sent to the same address, which should probably not happen.

# Concept

```mermaid
sequenceDiagram
    actor User
    User ->> DiscoveryServiceImpl: getDiscoveryByBPN(bpn)
    activate DiscoveryServiceImpl

    alt try
        DiscoveryServiceImpl ->> DiscoveryRepository: retrieveDiscoveryByFinderAndEdcDiscoveryService(bpn)
        activate DiscoveryRepository
        DiscoveryRepository -->> DiscoveryServiceImpl: Optional<Discovery>
        deactivate DiscoveryRepository
    else catch
        DiscoveryServiceImpl -->> User: throw DiscoveryFinderException("DiscoveryFinder could not determine result.")
    end

    DiscoveryServiceImpl ->> DiscoveryServiceImpl: checkDuplicateUrlsForBpn
    alt Duplicate URLs registered for BPN
        DiscoveryServiceImpl -->> User: throw DiscoveryFinderException
        Note left of User: DiscoveryFinder detected an invalid state:\nMultiple identical URLs have been registered\nfor the same BPN. Ensure each BPN has a unique URL.
    else No duplicates
        DiscoveryServiceImpl -->> DiscoveryServiceImpl: return false
    end

    Note left of DiscoveryServiceImpl: Check if duplicate URLs are registered\nfor the same BPN.

    DiscoveryServiceImpl ->> DiscoveryServiceImpl: getDiscoveryFromBpnDatabase(bpn)
    DiscoveryServiceImpl ->> BpnRepository: existsWhereUrlNotNull(bpn)

    alt BpnEdcMapping exists
        BpnRepository -->> DiscoveryServiceImpl: return BpnEdcMapping
    else BPN not found
        DiscoveryServiceImpl -->> User: throw BpnNotFoundException
    end
    activate BpnRepository
    BpnRepository -->> DiscoveryServiceImpl: boolean
    deactivate BpnRepository

    alt BPN exists and URL is not null
        DiscoveryServiceImpl ->> BpnRepository: findByIdOrThrowNotFoundException(bpn)
        Note left of DiscoveryServiceImpl: If BpnNotFoundException is thrown,\nthe BPN URL from DiscoveryService\nis never used.
        activate BpnRepository
        BpnRepository -->> DiscoveryServiceImpl: BpnRecord
        deactivate BpnRepository
        DiscoveryServiceImpl ->> Discovery: toDiscovery(receiverUrl, senderUrl)
    else DiscoveryFinderException occurs
        DiscoveryServiceImpl -->> User: throw DiscoveryFinderException
    end

    activate Discovery
    DiscoveryServiceImpl ->> DiscoveryServiceImpl: mergeDiscoveriesAndRemoveDuplicates
    DiscoveryServiceImpl ->> DiscoveryServiceImpl: removeDuplicates
    deactivate Discovery

    deactivate DiscoveryServiceImpl

```

# Additional Details
Given the dynamic nature of ongoing development, there might be variations between the conceptualization and the current implementation. For the latest status, refer to the documentation.
