# Compatibility matrix Trace-X

## Catena-X Release?
- [x] yes -> Catena-X Release 9.0.0-rc2
- [ ] no


### (Trace-X Release)  [9.0.0-rc2| Release Notes](https://github.com/catenax-ng/tx-traceability-foss/releases/tag/9.0.0-rc2)

#### Trace-X version 9.0.0-rc2
#### Helm Version 1.3.21


| Dependency        | Name of Service               | Version             | Helm  | Comments                                            |
|-------------------|-------------------------------|---------------------|-------|-----------------------------------------------------|
| EDC               | edc-postgresql                | 12.1.6              | 2.0.0 | Enterprise Data Connector for PostgreSQL            |
| IRS               | irs-helm                      | 4.0.1               | 6.9.1 | Helm charts for Item Relationship Service           |
| EDC               | tractusx-connector            | 0.5.3               | 2.0.0 | Connector for Data Transfer and Registration        |
| Discovery Service | discovery service             | 1.16.0              | 0.1.0 | Service for discovering and registering artifacts   |
| Portal            | portal                        | 1.7.0               | 1.7.0 | Web portal for interacting with Trace-X             |
| SD-Factory        | SD-Factory                    | 2.1.7               | 2.1.8 | Service Discovery Factory for managing dependencies |
| Wallet            | wallet                        | 0.3.0               | 0.3.0 | Secure storage for sensitive information            |
| Aspect Model      | SerialPart                    | 1.0.0, 1.0.1, 1.1.0 | -     |                                                     |
| Aspect Model      | Batch                         | 1.0.0, 1.0.2, 2.0.0 | -     |                                                     |
| Aspect Model      | PartAsPlanned                 | 1.0.0, 1.0.1        | -     |                                                     |
| Aspect Model      | PartSiteInformationAsPlanned  | 1.0.0               | -     |                                                     |
| Aspect Model      | JustInSequencePart            | 1.0.0               | -     |                                                     |
| Aspect Model      | TractionBatteryCode           | 1.0.0               | -     |                                                     |
