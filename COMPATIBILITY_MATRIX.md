# Compatibility matrix Trace-X

## Catena-X Release?
- [x] yes -> Catena-X Release 9.0.0-rc2
- [ ] no


### (Trace-X Release)  [9.0.0-rc2| Release Notes](https://github.com/catenax-ng/tx-traceability-foss/releases/tag/9.0.0-rc2)

#### Trace-X version 9.0.0-rc2
#### Helm Version 1.3.21


| Dependency   | Name of Service               | Version             | Helm | Comments                                            |
|--------------|-------------------------------|---------------------|------|-----------------------------------------------------|
| PostgreSQL   | postgresql                    | 12.1.6              | -    | Database server for storing Trace-X data            |
| EDC          | edc-postgresql                | 12.1.6              | -    | Enterprise Data Connector for PostgreSQL            |
| pgAdmin      | pgadmin4                      | 1.13.6              | -    | PostgreSQL administration and management tool       |
| Helm         | irs-helm                      | 6.9.1               | -    | Helm charts for Item Relationship Service           |
| EDC          | tractusx-connector            | 0.5.3               | -    | Connector for Data Transfer and Registration        |
| EDC          | discovery service             | 0.1.0               | -    | Service for discovering and registering artifacts   |
| Portal       | portal                        | ?                   | ?    | Web portal for interacting with Trace-X             |
| SD-Factory   | SD-Factory                    | ?                   | ?    | Service Discovery Factory for managing dependencies |
| Wallet       | wallet                        | ?                   | ?    | Secure storage for sensitive information            |
| Aspect Model | SerialPart                    | 1.0.0, 1.0.1, 1.1.0 | -    |                                                     |
| Aspect Model | Batch                         | 1.0.0, 1.0.2, 2.0.0 | -    |                                                     |
| Aspect Model | PartAsPlanned                 | 1.0.0, 1.0.1        | -    |                                                     |
| Aspect Model | PartSiteInformationAsPlanned  | 1.0.0               | -    |                                                     |
| Aspect Model | JustInSequencePart            | 1.0.0               | -    |                                                     |
| Aspect Model | TractionBatteryCode           | 1.0.0               | -    |                                                     |
