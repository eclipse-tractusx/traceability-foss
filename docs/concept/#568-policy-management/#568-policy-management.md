# #568 Policy management

| Key           | Value                                                                    |
|---------------|--------------------------------------------------------------------------|
| Autor         | @ds-crehm                                                                |
| Creation date | 08.02.2024                                                               |
| Ticket Id     | [#568](https://github.com/eclipse-tractusx/traceability-foss/issues/568) |
| State         | WIP                                                                      |

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
In the Catena-X ecosystem every partner can potentially communicate to every other connected partner.
To remain in control of their own data, each partner can use policies to place conditions on the access of their data.
In order to use these policies they must first be created and attached to the assets of a partner.


# Summary
It must be possible for an Administrator of Trace-X to create, read, update and delete policies, which are then stored in the IRS policy store.

# Problem Statement

# Requirements
- [ ] Frontend UI is implemented (see https://miro.com/app/board/uXjVO5JVoho=/?moveToWidget=3458764577267183586&cot=14)
- [ ] CRUD operations for policies are implemented
- [ ] Communication to IRS policy store is implemented
- [ ] Policies are used when sending notifications

# NFR

# Out of scope
- Policies used to define which assets to be consumed over the IRS -> IRS team

# Assumptions

# Concept

## CREATE policies

|             |                  |
|-------------|------------------|
| HTTP method | POST             |
| Endpoint    | /tracex/policies |
| Parameters  | -                |

### Responses

| Code | Definition                                |
|------|-------------------------------------------|
| 201  | Policy created                            |
| 400  | Policy registration failed                |
| 401  | No valid authentication credentials found |
| 403  | Authorization refused by server           |

#### Examples
**201**
*No response body*

**400**
```json
{
    "error": "Bad request",
    "messages": [
        "BadRequestException"
    ],
    "statusCode": "400 BAD_REQUEST"
}
```
___
**401**
```json
{
    "error": "Unauthorized",
    "messages": [
        "UnauthorizedException"
    ],
    "statusCode": "401 UNAUTHORIZED"
}
```
___
**403**
```json
{
    "error": "Forbidden",
    "messages": [
        "ForbiddenException"
    ],
    "statusCode": "403 FORBIDDEN"
}
```
___
**Creation of policies:**
```mermaid
sequenceDiagram
    participant FE
    participant BE
    participant IRSLib
    participant IRSPolicyStore
    participant DB
    FE->>BE: POST /tracex/policies
    activate BE
    alt Valid policy
        BE->>IRSLib: Verify policy
        activate IRSLib
        IRSLib-->>BE: Success
        deactivate IRSLib
        BE->>IRSPolicyStore: POST /irs/policies
        activate IRSPolicyStore
        IRSPolicyStore-->>BE: Success
        deactivate IRSPolicyStore
        BE->>DB: Save policy
        BE-->>FE: 200 Success
    else Invalid policy
        BE->>IRSLib: Verify policy
        activate IRSLib
        IRSLib-->>BE: Error
        deactivate IRSLib
        BE-->>FE: 400 Bad request
    deactivate BE
    end
```

## GET policies

|             |                  |
|-------------|------------------|
| HTTP method | GET              |
| Endpoint    | /tracex/policies |
| Parameters  | -                |

### Responses

| Code | Definition                                |
|------|-------------------------------------------|
| 200  | Returns all policies                      |
| 401  | No valid authentication credentials found |
| 403  | Authorization refused by server           |

#### Examples
**200**
```json
{
    "payload": [
        {
            "id": "8231-dfg-d234324-324-324",
            "createdOn": "2023-05-17T16:42:00Z",
            "validUntil": "2023-05-24T12:57:29Z",
            "permissions": [
                {
                    policytype: "USE",
                    "constraints": {
                        or [
                            {
                                "leftOperand": "Membership",
                                "operator: eq
                                "rightOperand": "active"
                            },
                            {
                                "leftOperand": "FrameworkAgreement.traceability",
                                "operator: in
                                "rightOperand": ["active"]
                            },
                            {

                                "leftOperand": "PURPOSE",
                                "operator: eq
                                "rightOperand": "ID 3.1 Trace"
                            }
                        ]
                    }
                }
            ]
        },
        {
            "id": "9231-dfg-d2654324-764-544",
            "createdOn": "2024-01-05T14:29:02Z",
            "validUntil": "2024-04-29T09:34:44Z",
            "permissions": [
                {
                    policytype: "USE",
                    "constraints": {
                        or [
                    {
                        "leftOperand": "Membership",
                        "operator: eq
                        "rightOperand": "active"
                    }
                ]
            }
            }
            ]
        }
    ]
}
```
___
**401**
```json
{
    "error": "Unauthorized",
    "messages": [
        "UnauthorizedException"
    ],
    "statusCode": "401 UNAUTHORIZED"
}
```
___
**403**
```json
{
    "error": "Forbidden",
    "messages": [
        "ForbiddenException"
    ],
    "statusCode": "403 FORBIDDEN"
}
```
___
**Getting policies:**
```mermaid
sequenceDiagram
    participant FE
    participant BE
    participant DB
    FE->>BE: GET /tracex/policies
    activate BE
    BE->>DB: GET /tracex/policies
    activate DB
    DB-->>BE: Policies (JSON)
    deactivate DB
    BE-->>FE: Policies (JSON)
    deactivate BE
```

## UPDATE policies

|             |                              |
|-------------|------------------------------|
| HTTP method | PUT                          |
| Endpoint    | /tracex/policies/{policy_id} |
| Parameters  | policy_id (required string)  |

### Request body
#### Example
```json
{
    "id": "8231-dfg-d234324-324-324",
    "createdOn": "2023-05-17T16:42:00Z",
    "validUntil": "2023-05-24T12:57:29Z",
    "permissions": [
        {
            policytype: "USE",
            "constraints": {
                or [
                    {
                        "leftOperand": "Membership",
                        "operator: eq
                        "rightOperand": "active"
                    },
                    {
                        "leftOperand": "FrameworkAgreement.traceability",
                        "operator: in
                        "rightOperand": ["active"]
                    },
                    {

                        "leftOperand": "PURPOSE",
                        "operator: eq
                        "rightOperand": "ID 3.1 Trace"
                    }
                ]
            }
        }
    ]
}
```

### Responses

| Code | Definition                                |
|------|-------------------------------------------|
| 200  | Policy updated                            |
| 400  | Policy update failed                      |
| 401  | No valid authentication credentials found |
| 403  | Authorization refused by server           |

#### Examples
**200**
*No response body*

**400**
```json
{
    "error": "Bad request",
    "messages": [
        "BadRequestException"
    ],
    "statusCode": "400 BAD_REQUEST"
}
```
___
**401**
```json
{
    "error": "Unauthorized",
    "messages": [
        "UnauthorizedException"
    ],
    "statusCode": "401 UNAUTHORIZED"
}
```
___
**403**
```json
{
    "error": "Forbidden",
    "messages": [
        "ForbiddenException"
    ],
    "statusCode": "403 FORBIDDEN"
}
```
___
**Updating policies:**
```mermaid
sequenceDiagram
    participant FE
    participant BE
    participant IRSLib
    participant IRSPolicyStore
    participant DB
    FE->>BE: PUT tracex/policy (&policy_id="ABC123")
    activate BE
    alt Valid policy
        BE->>IRSLib: Verify policy
        activate IRSLib
        IRSLib-->>BE: Success
        deactivate IRSLib
        BE->>IRSPolicyStore: PUT /irs/policy (&policy_id="ABC123")
        activate IRSPolicyStore
        IRSPolicyStore-->>BE: Success
        deactivate IRSPolicyStore
        BE->>DB: Update policy
        BE-->>FE: 200 Success
    else Invalid policy
        BE->>IRSLib: Verify policy
        activate IRSLib
        IRSLib-->>BE: Error
        deactivate IRSLib
        BE-->>FE: 400 Bad request
    deactivate BE
    end
```

## DELETE policies

|             |                              |
|-------------|------------------------------|
| HTTP method | DELETE                       |
| Endpoint    | /tracex/policies/{policy_id} |
| Parameters  | policy_id (required string)  |

### Responses

| Code | Definition                                |
|------|-------------------------------------------|
| 200  | Policy deleted                            |
| 400  | Policy deletion failed                    |
| 401  | No valid authentication credentials found |
| 403  | Authorization refused by server           |

#### Examples
**200**
*No response body*

**400**
```json
{
    "error": "Bad request",
    "messages": [
        "BadRequestException"
    ],
    "statusCode": "400 BAD_REQUEST"
}
```
___
**401**
```json
{
    "error": "Unauthorized",
    "messages": [
        "UnauthorizedException"
    ],
    "statusCode": "401 UNAUTHORIZED"
}
```
___
**403**
```json
{
    "error": "Forbidden",
    "messages": [
        "ForbiddenException"
    ],
    "statusCode": "403 FORBIDDEN"
}
```
___
**Deleting policies:**
```mermaid
sequenceDiagram
    participant FE
    participant BE
    participant IRSPolicyStore
    participant DB
    FE->>BE: DELETE /tracex/policy (&policy_id="ABC123")
    activate BE
    BE->>IRSPolicyStore: DELETE /irs/policy (&policy_id="ABC123")
    activate IRSPolicyStore
    IRSPolicyStore-->>BE: Success
    deactivate IRSPolicyStore
    BE->>DB: Delete policy
    BE-->>FE: 200 Success
    deactivate BE
```

## Sending notifications

```mermaid
sequenceDiagram
    participant FE
    participant BE
    participant EDCDiscoveryService
    participant IRSPolicyStore
    FE->>BE: Create notification
    activate BE
    BE-->>FE: Notification created
    deactivate BE
    FE->>BE: Send notification
    activate BE
    BE->>EDCDiscoveryService: GET BPN
    activate EDCDiscoveryService
    EDCDiscoveryService-->>BE: BPN
    deactivate EDCDiscoveryService
    BE->>IRSPolicyStore: GET /irs/policies (BPN)
    activate IRSPolicyStore
    IRSPolicyStore-->>BE: Policies
    deactivate IRSPolicyStore
    BE->>BE: Verify if notification may be sent
    alt Notification may be sent
        BE->>EDC: Send notification
        activate EDC
        EDC-->>BE: Notification sent
        deactivate EDC
        BE-->>FE: Notification sent (Status = "Sent")
    else Notification may not be sent
        BE-->>FE: PolicyException (Status = "Exception")
    end
    deactivate BE
```

# Glossary

| Abbreviation | Name | Description   |
|--------------|------|---------------|
|              |      |               |
|              |      |               |

# References

# Additional Details
Given the dynamic nature of ongoing development, there might be variations between the conceptualization and the current implementation. For the latest status, refer to the documentation.
