# \[Concept\] \[#849\] Quality notifications to multiple BPNs

| Key           | Value                                                                    |
|---------------|--------------------------------------------------------------------------|
| Author        | @ds-crehm                                                                |
| Creation date | 22.04.2024                                                               |
| Ticket Id     | [#849](https://github.com/eclipse-tractusx/traceability-foss/issues/849) |
| State         | DRAFT                                                                    |

# Table of Contents
1. [Overview](#overview)
2. [Requirements](#requirements)
3. [Out of scope](#out-of-scope)
4. [Concept](#concept)
5. [Additional Details](#additional-details)

# Overview
Instead of having "messages" that can be sent to multiple BPNs within one notification, multiple notifications must be created.
Those can be individually edited and sent to the specific BPN.

# Requirements
- For alerts, when selecting multiple BPNs, a duplicated alert for each of the selected BPNs is created.
- For investigations, when selecting parts from different BPNs, a duplicated investigation for each of the BPNs is created.
- When editing notifications and either an additional receiver BPN or part with a different BPN is added, a duplicate of the notification will be created.
- The data model is changed to make this process possible.
- Notifications are tied to single BPNs and cannot be sent to multiple BPNs anymore.
- In the frontend, the specified modals are created.
- The frontend modals appear when a user is creating/editing notifications and triggers a notification duplication.
- In the modal the user has the chance to cancel or approve the process.

# Out of scope
- A hierarchical parent object, that ties all the duplicated notifications together. For now, the user must search/filter for all individual notifications if he wants to interact with all of them.
In the future, the idea is to bundle all duplicated notifications in a parent object that can be searched/filtered for.
- A duplication process for the user. In case the user wants to retroactively add BPNs to an existing notification or send a notification to a BPN that was not selected during the creation of this notification,
he must create a new notification and copy the data. In the future, there might be the possibility to add a "Duplicate notification" or "Copy notification" process to Trace-X, but that is out of scope for this concept.

# Concept
## Backend

#### Alert creation
When an alert is created, multiple BPNs can be selected. For each of the recipients the notification will be duplicated.
The data will be identical except for the "createdFor" and "createdForName" fields.
If the data must be changed, the user can do it afterward in the "edit notification" view.

#### Investigation creation
When an investigation is created, multiple parts can be selected. Whenever a part from a different BPN is selected, the notification will be duplicated.
The data will be identical except for the "createdFor", "createdForName" and "assetIds" fields. Each notification will only contain parts from the same BPN.
If the data must be changed, the user can do it afterward in the "edit notification" view.

#### Edit notifications
When editing notifications, only parts with the identical BPN as the "createdFor" BPN will be shown and the user may not add BPNs manually.
There is no possibility to create a new notification from the "Edit notifications" process.
If the user wants to send an existing notification to a different BPN, he must copy the data and use the "Create notification" process.

### Data model
The data model must be changed to reflect the needed changes. The suggestion is to remove the "message" object (which reflected individual notifications).
The previous "reason" object can be renamed to "messages" to reflect its content better and the needed information from the individual messages is moved to the parent object (sentDate, acknowledgedDate, ...).
Additionally, the error messages should have more relevant information and should not be tied to individual messages. Instead of overwriting error messages, a new error message is added, whenever there was an error.

```diff
{
    "id": 1,
    "title": "Notification title",
    "status": "ACKNOWLEDGED",
    "description": "Notification description",
    "createdBy": "BPNL00000003CNKC",
    "createdByName": "TEST_BPN_IRS_1",
-   "sendTo": "BPNL00000003CML1",
-   "sendToName": "TEST_BPN_DFT_1",
+   "createdFor": "BPNL00000003CML1",
+   "createdForName": "TEST_BPN_DFT_1",
-   "createdDate": "2024-04-09T00:00:00.000000Z",
    "assetIds": [
        "urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02"
    ],
    "channel": "SENDER",
-   "reason": {
-       "close": null,
-       "accept": null,
-       "decline": null
-   },
    "severity": "MINOR",
    "type": "ALERT",
    "targetDate": null,
+   "error": [
+       {
+          "status": "ACKNOWLEDGED",
+          "message": "Failed to establish connection to EDC.",
+          "date": "2024-04-12T00:00:00.000000Z"
+       },
+       {
+           "status": "CREATED",
+           "message": "Failed to negotiate contract agreement.",
+           "date": "2024-04-10T00:00:00.000000Z"
+       }
+   ]
+   "messageHistory": [
+       {
+           "createdDate": "2024-04-09T00:00:00.000000Z"
+       },
+       {
+           "sentDate": "2024-04-10T01:00:00.000000Z",
+           "receiver": "BPNL00000003CML1"
+       },
+       {
+           "acknowledgedDate": "2024-04-12T01:00:00.000000Z",
+           "receiver": "BPNL00000003CNKC"
+       },
+       {
+           "acceptedDate": null,
+           "acceptedMessage": null,
+           "receiver": "BPNL00000003CNKC"
+       },
+       {
+           "declinedDate": null,
+           "declinedMessage": null,
+           "receiver": "BPNL00000003CNKC"
+       },
+       {
+           "closedDate": null,
+           "closedMessage": null,
+           "receiver": "BPNL00000003CML1"
+       }
+   ]
-   "messages": []
}
```

## Frontend
![multiple-bpns-modals.png](multiple-bpns-modals.png)
![notification-creation-bpn-selection.png](notification-creation-bpn-selection.png)

See https://miro.com/app/board/uXjVO5JVoho=/?moveToWidget=3458764586657591852&cot=10

# Additional Details
Given the dynamic nature of ongoing development, there might be variations between the conceptualization and the current implementation. For the latest status, refer to the documentation.
