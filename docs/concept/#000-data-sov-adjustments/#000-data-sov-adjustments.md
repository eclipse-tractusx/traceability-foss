# \[Concept\] \[#ID#\] Summary

| Key           | Value             |
|---------------|-------------------|
| Author        | <name>            |
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
8. [Concept](#concept)
9. [Glossary](#glossary)
10. [References](#references)
11. [Additional Details](#additional-details)


# Overview

# Summary

# Problem Statement

# Requirements

# NFR

# Out of scope

# Assumptions

# Concept

* Company BMW (BPNL0000BMW)
* Company ZF (BPNL0000ZF)
* Company BOSCH (BPNL0000BOSCH)


## Assets-Flow

### Company BMW (BPNL0000BMW) publishes a transient Asset

````mermaid
sequenceDiagram
    %%{init: {'theme': 'dark', 'themeVariables': { 'fontSize': '15px'}}}%%
    autonumber

    actor Superior
    participant BMW_Trace_X
    participant EDC

    Superior ->> BMW_Trace_X: Publish Asset with Transient Status
    BMW_Trace_X -->> Superior: request for policy
    Superior ->> BMW_Trace_X: Select Policy for Asset Publication
    BMW_Trace_X ->> BMW_Trace_X : Publish Asset for defined Policy
    BMW_Trace_X ->> EDC: Create CatalogOffer for Asset and defined Policy
    EDC -->> BMW_Trace_X : CatalogOffer created
    BMW_Trace_X -->> Superior: ok asset published

````

#### Company ZF (BPNL0000ZF) consumes an asset

````mermaid
sequenceDiagram
    %%{init: {'theme': 'dark', 'themeVariables': { 'fontSize': '15px'}}}%%
    autonumber

    participant ZF_Trace_X
    participant IRS
    participant EDC
    participant PolicyStore

    ZF_Trace_X ->> IRS : initiate Sync of assets

    IRS ->> EDC : Catalog offers for BMW assets are requested
    EDC -->> IRS: Catalog offers
    IRS ->> PolicyStore :  Request for policy for  BPNL of BMW
    PolicyStore -->> IRS : policy for BPNL of BMW or default policy
    IRS ->> IRS: compare policy of CatalogOffer and requested policy
    alt valid
        IRS->>EDC: Start Contract Negotiation
        EDC -->> IRS: return asset
        IRS -->> ZF_Trace_X : return asset (JobResponse)
    else Negative
        IRS->>IRS: create Tombstone with policy
        IRS -->> ZF_Trace_X : return tombstonre with policy (JobResponse)
    end

````

1. Scheduled bases sync of assets started in IRS-
2. Catalog offer of the BMW asset is requested
3. Response wir CatalogOffer for asset
4.  Policy for the BPNL of BMW is searched for. If none exists, the default policy is used.
5. Return policy for BPNL or default policy in case there is no policy defined for BPNL.
6. Policy of the CatalogOffer is compared with the policy of the policy to be checked
7. Valid: contract negotiation is started
8. return asset (JobResponse)
9. return asset to Trace-X  in JobResponse
10. Not valid: no contract negotiation - tombstone is generated

### Company BMW (BPNL0000BMW): Superior edits a policy (Assets)

````mermaid
sequenceDiagram
    %%{init: {'theme': 'dark', 'themeVariables': { 'fontSize': '15px'}}}%%
    autonumber

    actor Superior
    participant Trace_X
    participant PolicyStore
    participant EDC


    Superior ->> Trace_X: Edit an existing policy
    Superior ->> Trace_X: Check whether the policy is linked to a published asset.
    alt true
        Trace_X -->> EDC : Update policy for linkes DataOffers
        EDC -->> Trace_X : ok
        Trace_X ->> PolicyStore : save policy
        PolicyStore -->> Trace_X : ok
        Trace_X -->> Superior : policy saved
    else false
        Trace_X ->> PolicyStore : save policy
        PolicyStore -->> Trace_X : ok
        Trace_X -->> Superior : policy saved
    end
````

### Company BMW (BPNL0000BMW): Admin deletes a policy for BPNL ZF (Assets)

````mermaid
sequenceDiagram
    %%{init: {'theme': 'dark', 'themeVariables': { 'fontSize': '15px'}}}%%
    autonumber

    actor Superior
    participant Trace_X
    participant PolicyStore
    participant EDC


    Superior ->> Trace_X: Delete an existing policy
    Superior ->> Trace_X: Check whether the policy is linked to a published asset.
    alt true
       Trace_X -->> EDC : Update to default policy for linked DataOffers
        EDC -->> Trace_X : ok
        Trace_X ->> PolicyStore : delete policy
        PolicyStore -->> Trace_X : ok
        Trace_X -->> Superior : policy delete
    else false
        Trace_X ->> PolicyStore : delete policy
        PolicyStore -->> Trace_X : ok
        Trace_X -->> Superior : policy deleted
    end

````

## Notification-Flow

#### Company BMW (BPNL0000BMW) sends notification to ZF (BPNL0000ZF)


````mermaid
sequenceDiagram
%%{init: {'theme': 'dark', 'themeVariables': { 'fontSize': '15px'}}}%%
autonumber

actor Superior
participant Trace_X_FE as "Trace-X"
participant Trace_X as "Trace-X BMW"
participant EDC_S as "EDC Supplier"
participant  PolicyStore

Superior ->> Trace_X_FE: Send notification
Trace_X_FE ->> Trace_X : Send notification
Trace_X ->> EDC_S : Get Notification CatalogOffer of EDC_S
EDC_S -->> Trace_X : return CatalogOffer
Trace_X -->> PolicyStore : Get Policy for BPNL or default policy
Trace_X -->> Trace_X: check Policy of CatalogOffer with Policy from PolicyStore
alt valid
Trace_X ->> EDC_S : negotiateContract
EDC_S -->> Trace_X: contract negotation successful
Trace_X ->> EDC_S :  send notification
EDC_S --> Trace_X: ok
else
Trace_X -> Trace_X : create tombstone
Trace_X -->> Trace_X_FE : show  policy mismatch
Trace_X_FE -->> Superior : show  policy mismatch
end

````

#### Company Superior BMW edits policy (not default)

````mermaid
sequenceDiagram
    %%{init: {'theme': 'dark', 'themeVariables': { 'fontSize': '15px'}}}%%
    autonumber

    actor Superior
    participant Trace_X
    participant PolicyStore
    participant EDC


    Superior ->> Trace_X: Edit an existing policy
    Superior ->> Trace_X: Check whether the policy is linked to a published notification CatlogOffer.
    alt true
        Trace_X -->> EDC : Update notification DataOffers to new policy (PUT /v2/contractdefinitions)
        EDC -->> Trace_X : ok
        Trace_X ->> PolicyStore : save policy
        PolicyStore -->> Trace_X : ok
        Trace_X -->> Superior : policy saved
    else false
        Trace_X ->> PolicyStore : edit policy
        PolicyStore -->> Trace_X : ok
        Trace_X -->> Superior : policy saved
    end

````

#### Company Superior BMW deletes  policy (not default)

````mermaid
sequenceDiagram
    %%{init: {'theme': 'dark', 'themeVariables': { 'fontSize': '15px'}}}%%
    autonumber

    actor Superior
    participant Trace_X
    participant PolicyStore
    participant EDC


    Superior ->> Trace_X: Delete an existing policy
    Superior ->> Trace_X: Check whether the policy is linked to a notification .
    alt true
       Trace_X -->> EDC : Delete outdated DataOffer in EDC
        EDC -->> Trace_X : ok
        Trace_X -->> EDC : Update  DataOffer to defaul policy in EDC (PUT /v2/contractdefinitions)
        EDC -->> Trace_X : ok
        Trace_X ->> PolicyStore : delete policy
        PolicyStore -->> Trace_X : ok
        Trace_X -->> Superior : policy delete
    else false
        Trace_X ->> PolicyStore : delete policy
        PolicyStore -->> Trace_X : ok
        Trace_X -->> Superior : policy deleted
    end

````
#### Validate Policy of CatalogOffer for Notifications

````mermaid
sequenceDiagram
    %%{init: {'theme': 'dark', 'themeVariables': { 'fontSize': '15px'}}}%%
    autonumber

    actor Superior
    participant Trace_X_FE as "Trace-X"
    participant Trace_X as "Trace-X BMW"
    participant EDC_S as "EDC Supplier"
    participant  PolicyStore

    Superior ->> Trace_X_FE: Send notification
    Trace_X_FE ->> Trace_X : Send notification
    Trace_X ->> EDC_S : Get Notification CatalogOffer of EDC_S
    EDC_S -->> Trace_X : return CatalogOffer
    Trace_X -->> PolicyStore : Get Policy for BPNL or default policy
    Trace_X -->> Trace_X: check Policy of CatalogOffer with Policy from PolicyStore
    alt valid
          Trace_X ->> EDC_S : negotiateContract
          EDC_S -->> Trace_X: contract negotation successful
          Trace_X ->> EDC_S :  send notification
          EDC_S --> Trace_X: ok
    else
          Trace_X -> Trace_X : create tombstone
          Trace_X -->> Trace_X_FE : show  policy mismatch
          Trace_X_FE -->> Superior : show  policy mismatch
    end


````



> **_NOTE:_**   INCLUDE IMAGE Data-consumption-from-suppliers-asBuilt.puml

1. Trace-X Backend starts the synchronization process of companies digital twins.  The synchronization process of digital twins is initiated.
1. Trace-X Backend requests own parts in the "dDTR" component. A request is sent to get the relevant parts.
1. dDTR returns the globalAssetIds: The identifiers of the parts to be synchronized are returned.
1. Trace-X Backend registers a job with the globalAssetIds in the "IRS" component: The job is registered with the given globalAssetIds.
1. IRS starts the job with the globalAssetIds: The job begins, processing the provided globalAssetIds.
1. IRS loads parts with the globalAssetIds from the "dDTR" component: The parts identified by the globalAssetIds are loaded.
1. dDTR returns the own digital twins (AAS): The corresponding digital twins are returned.
1. IRS requests the SingleLevelBomAsBuilt aspect from the "SubmodelServer" component: A request is made to retrieve the SingleLevelBomAsBuilt aspect.
1. SubmodelServer returns the SingleLevelBomAsBuilt aspect: The requested aspect is returned to the IRS.
1. IRS requests the supplier's digital twin from the "SubmodelServer" component: A request is made to get the supplier's digital twin.
1. IRS requests the EDC for BPNL from the "EDC Discovery Service": The EDC Discovery Service is queried for the supplier's BPNL.
1. EDC Discovery Service returns the EDC of the company BPNL: The EDC associated with the company's BPNL is returned.
1. IRS requests the CatalogItem for the supplier twins from the "EDC": A request is made to get the CatalogItem for the supplier's digital twins.
1. EDC returns the CatalogItem with the policy included: The CatalogItem, including its policy, is returned.
1. IRS searches for policies for the supplier's BPNL in the "PolicyStore": The PolicyStore is queried for any policies related to the supplier's BPNL.
1. PolicyStore returns the policies for the BPNL or null: The relevant policies or a null value (if no policies exist) are returned.
1. IRS checks the policy by calling the "PolicyChecker": The policies are checked against the company's own policies.
1. PolicyChecker retrieves policies for the BPNL from the "PolicyStore": The required policies for the BPNL are fetched.
1. PolicyStore returns the policies: The fetched policies are returned.
1. If no policy is found for the BPNL, PolicyChecker uses the company's default policy: The default policy is applied if no specific policy is found.
1. PolicyChecker checks own policies against the CatalogItem policies: The company's own policies are compared with the CatalogItem's policies.
1. PolicyChecker returns whether the policies are valid: The result of the policy check (valid or not) is returned.
1. If the policies match, IRS starts the contract negotiation subprocess: The contract negotiation with the supplier is initiated.
1. IRS returns the supplier's digital twin: The supplier's digital twin is returned after successful negotiation.
1. If the policies do not match, IRS throws a UsagePolicyException: An exception is thrown indicating a policy mismatch.
1. IRS creates a tombstone with the CatalogItem policy: A tombstone record is created with the CatalogItem's policy details.
1. Trace-X Backend executes a callback to indicate the process completion: A callback is made to signal the end of the process.
1. Trace-X Backend requests the job status from the "IRS": The status of the job is queried.
1. IRS returns the job response: The job's response/status is returned to the Trace-X Backend




# Glossary

| Abbreviation | Name | Description   |
|--------------|------|---------------|
|              |      |               |
|              |      |               |

# References

# Additional Details
Given the dynamic nature of ongoing development, there might be variations between the conceptualization and the current implementation. For the latest status, refer to the documentation.
