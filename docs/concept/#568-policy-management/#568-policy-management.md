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
3. [Requirements](#requirements)
4. [Out of scope](#out-of-scope)
5. [Concept](#concept)
6. [Glossary](#glossary)
7. [References](#references)
8. [Additional Details](#additional-details)

# Overview
In the Catena-X ecosystem every partner can potentially communicate to every other connected partner.
To remain in control of their own data, each partner can use policies to place conditions on the access of their data.
In order to use these policies they must first be created and attached to the assets of a partner.

# Summary
It must be possible for an Administrator of Trace-X to create, read, update and delete policies, which are then stored in the IRS policy store.

# Requirements
- [ ] Frontend UI is implemented (see https://miro.com/app/board/uXjVO5JVoho=/?moveToWidget=3458764577267183586&cot=14)
- [ ] CRUD operations for policies are implemented
- [ ] Communication to IRS policy store is implemented
- [ ] Policies are used when sending notifications

# Out of scope
- Policies used to define which assets to be consumed over the IRS -> IRS team

# Concept

## CREATE policies

|             |               |
|-------------|---------------|
| HTTP method | POST          |
| Endpoint    | /api/policies |
| Parameters  | -             |

### Request body
#### Example
```json
[
    {
        "permissions": [
            {
                "action": "USE",
                "constraint": {
                    "and": [
                        {
                            "leftOperand": "string",
                            "odrl:rightOperand": "string",
                            "operator": {
                                "@id": "odrl:eq"
                            }
                        }
                    ],
                    "or": [
                        {
                            "leftOperand": "string",
                            "odrl:rightOperand": "string",
                            "operator": {
                                "@id": "odrl:eq"
                            }
                        }
                    ]
                }
            }
        ],
        "policyId": "f253718e-a270-4367-901b-9d50d9bd8462",
        "validUntil": "2024-02-19T12:48:23.786Z"
    }
]
```

### Responses

| Code | Definition                                |
|------|-------------------------------------------|
| 201  | Policy created                            |
| 400  | Policy registration failed                |
| 401  | No valid authentication credentials found |
| 403  | Authorization refused by server           |
| 404  | Not found                                 |

___
**Creation of policies:**
```mermaid
sequenceDiagram
    participant FE
    participant BE
    participant IRSPolicyStore
    participant DB
    FE->>BE: POST /api/policies
    activate BE
    BE->>BE: Verify policy
    alt Valid policy
        BE->>IRSPolicyStore: POST /irs/policies
        activate IRSPolicyStore
        IRSPolicyStore->>DB: Save policy
        IRSPolicyStore-->>BE: Success
        deactivate IRSPolicyStore
        BE-->>FE: 200 Success
    else Invalid policy
        BE-->>FE: 400 Bad request
    deactivate BE
    end
```

## GET policies

|             |               |
|-------------|---------------|
| HTTP method | GET           |
| Endpoint    | /api/policies |
| Parameters  | -             |

### Responses

| Code | Definition                                |
|------|-------------------------------------------|
| 200  | Returns all policies                      |
| 401  | No valid authentication credentials found |
| 403  | Authorization refused by server           |
| 404  | Not found                                 |

#### Examples
**200**
```json
[
    {
        "permissions": [
            {
                "action": "USE",
                "constraint": {
                    "and": [
                        {
                            "leftOperand": "string",
                            "odrl:rightOperand": "string",
                            "operator": {
                                "@id": "odrl:eq"
                            }
                        }
                    ],
                    "or": [
                        {
                            "leftOperand": "string",
                            "odrl:rightOperand": "string",
                            "operator": {
                                "@id": "odrl:eq"
                            }
                        }
                    ]
                }
            }
        ],
        "policyId": "f253718e-a270-4367-901b-9d50d9bd8462",
        "validUntil": "2024-02-19T12:48:23.786Z"
    }
]
```
___
**Getting policies:**
```mermaid
sequenceDiagram
    participant FE
    participant BE
    participant DB
    FE->>BE: GET /api/policies
    activate BE
    BE->>DB: GET /api/policies
    activate DB
    DB-->>BE: Policies (JSON)
    deactivate DB
    BE-->>FE: Policies (JSON)
    deactivate BE
```

## UPDATE policies

|             |                             |
|-------------|-----------------------------|
| HTTP method | PUT                         |
| Endpoint    | /api/policies/{policy_id}   |
| Parameters  | policy_id (required string) |

### Request body
#### Example
```json
[
    {
        "permissions": [
            {
                "action": "USE",
                "constraint": {
                    "and": [
                        {
                            "leftOperand": "string",
                            "odrl:rightOperand": "string",
                            "operator": {
                                "@id": "odrl:eq"
                            }
                        }
                    ],
                    "or": [
                        {
                            "leftOperand": "string",
                            "odrl:rightOperand": "string",
                            "operator": {
                                "@id": "odrl:eq"
                            }
                        }
                    ]
                }
            }
        ],
        "policyId": "f253718e-a270-4367-901b-9d50d9bd8462",
        "validUntil": "2024-02-19T12:48:23.786Z"
    }
]
```

### Responses

| Code | Definition                                |
|------|-------------------------------------------|
| 200  | Policy updated                            |
| 400  | Policy update failed                      |
| 401  | No valid authentication credentials found |
| 403  | Authorization refused by server           |
| 404  | Not found                                 |

___
**Updating policies:**
```mermaid
sequenceDiagram
    participant FE
    participant BE
    participant IRSPolicyStore
    participant DB
    FE->>BE: PUT tracex/policy (&policy_id="ABC123")
    BE->>BE: Verify policy
    activate BE
    alt Valid policy
        BE->>IRSPolicyStore: PUT /irs/policy (&policy_id="ABC123")
        activate IRSPolicyStore
        IRSPolicyStore->>DB: Update policy
        IRSPolicyStore-->>BE: Success
        deactivate IRSPolicyStore
        BE-->>FE: 200 Success
    else Invalid policy
        BE-->>FE: 400 Bad request
    deactivate BE
    end
```

## DELETE policies

|             |                             |
|-------------|-----------------------------|
| HTTP method | DELETE                      |
| Endpoint    | /api/policies/{policy_id}   |
| Parameters  | policy_id (required string) |

### Responses

| Code | Definition                                |
|------|-------------------------------------------|
| 200  | Policy deleted                            |
| 400  | Policy deletion failed                    |
| 401  | No valid authentication credentials found |
| 403  | Authorization refused by server           |
| 404  | Not found                                 |

___
**Deleting policies:**
```mermaid
sequenceDiagram
    participant FE
    participant BE
    participant IRSPolicyStore
    participant DB
    FE->>BE: DELETE /api/policy (&policy_id="ABC123")
    activate BE
    BE->>IRSPolicyStore: DELETE /irs/policy (&policy_id="ABC123")
    activate IRSPolicyStore
    IRSPolicyStore->>DB: Delete policy
    IRSPolicyStore-->>BE: Success
    deactivate IRSPolicyStore
    BE-->>FE: 200 Success
    deactivate BE
```

## Sending notifications

![send-notification-policy-verification](send-notification-policy-verification.svg)

# Glossary

| Abbreviation | Name | Description   |
|--------------|------|---------------|
|              |      |               |
|              |      |               |

# References
EDC policy definition: https://github.com/eclipse-tractusx/ssi-docu/blob/main/docs/architecture/cx-3-2/edc/policy.definitions.md

# Additional Details
Given the dynamic nature of ongoing development, there might be variations between the conceptualization and the current implementation. For the latest status, refer to the documentation.
