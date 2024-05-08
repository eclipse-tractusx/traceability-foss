# \[Concept\] \[#831\] Notification data model revision

| Key           | Value                                                                    |
|---------------|--------------------------------------------------------------------------|
| Author        | @ds-crehm                                                                |
| Creation date | 06.05.2024                                                               |
| Ticket Id     | [#831](https://github.com/eclipse-tractusx/traceability-foss/issues/831) |
| State         | WIP                                                                      |


# Table of Contents
1. [Summary](#summary)
2. [Requirements](#requirements)
3. [Out of scope](#out-of-scope)
4. [Concept](#concept)
5. [Additional Details](#additional-details)


# Summary
The current data model was created with the possibility to send messages to multiple BPNs.
Since this concept will be changed with the implementation of [#849](https://github.com/eclipse-tractusx/traceability-foss/issues/849),
changes to the data model are needed. Additionally, some parts of the data model are outdated,
which must be changed as well to make it easier to work with and avoid future problems.

# Requirements
- Data model changed according to the suggestion in the concept.
- Message creation logic adjusted according to the process described in [Creating notifications and messages](#creating-notifications-and-messages)
    - No message creation when notification is created
    - Error messages always create a new message in the status during which the error occurred
    - Messages created whenever there was a status change (status in the message will be the new status)
    - Parent object "updatedDate" updated whenever a message is created (or when the notification was edited by the user)

# Out of scope
- Frontend adjustments to the message history will be made in [#753](https://github.com/eclipse-tractusx/traceability-foss/issues/753)

# Concept
## Data model
Notification data model must be changed as follows:

```diff
{
    "id": 609,
    "title": "Alert title",
    "type": "ALERT",
    "status": "CREATED",
    "description": ":-:Testing without targetDate TRACEFOSS-1546:-:2024-05-06T00:41:02.739457851Z",
    "createdBy": "BPNL00000003CML1",
    "createdByName": "TEST_BPN_DFT_1",
    "createdDate": "2024-05-06T00:41:03.356688Z",
+   "updatedDate": "2024-05-06T00:45:39.322734Z",
    "assetIds": [
        "urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02"
    ],
    "channel": "SENDER",
-   "reason": {
-       "close": null,
-       "accept": null,
-       "decline": null
-   },
    "sendTo": "BPNL00000003CNKC",
    "sendToName": "TEST_BPN_IRS_1",
    "severity": "MINOR",
    "targetDate": null,
    "messages": [
        {
            "id": "9a1c137f-172f-47ee-bfd3-a3dffd370349",
-           "createdBy": "BPNL00000003CML1",
-           "createdByName": "TEST_BPN_DFT_1",
+           "sentBy": "BPNL00000003CML1",
+           "sentByName": "TEST_BPN_DFT_1",
            "sendTo": "BPNL00000003CNKC",
            "sendToName": "TEST_BPN_IRS_1",
            "contractAgreementId": null,
            "notificationReferenceId": null,
-           "targetDate": null,
-           "severity": "MINOR",
            "edcNotificationId": "9a1c137f-172f-47ee-bfd3-a3dffd370349",
-           "created": [
-               2024,
-               5,
-               6,
-               0,
-               41,
-               3,
-               820767000
-           ],
-           "updated": [
-               2024,
-               5,
-               6,
-               0,
-               45,
-               59,
-               322734000
-           ],
+           "messageDate": "2024-05-06T00:41:03.356688Z",
            "messageId": "0deac300-e212-4c37-8d7d-83e18f01336e",
+           "message": null,
            "status": "CREATED",
            "errorMessage": "DiscoveryFinder could not determine result."
        }
    ]
}
```

Major changes:
- "updated" from messages removed and replaced with "updatedDate" in the parent object. This will be updated whenever there was any change to the notification.
- "reason" from parent object removed and replaced with "message" in the messages. For the three status changes where a user can input a message (close, accept, decline) this String will be used. For the other status messages, it will be null.
- "targetDate" removed from messages.
- "severity" removed from messages.

Minor changes:
- "createdBy" and "createdByName" in messages renamed to "sentBy" and "sentByName".
- "created" in messages renamed to "messageDate" and format changed to match the other timestamps.

## Creating notifications and messages
When successfully creating a notification, **no message must be created for the status "CREATED"**. After the notification was created it should look like this (with no messages):
```json
{
    "id": 1,
    "title": "Notification title",
    "type": "ALERT",
    "status": "CREATED",
    "description": "Notification creation - example",
    "createdBy": "BPNL00000003CML1",
    "createdByName": "TEST_BPN_DFT_1",
    "createdDate": "2024-05-06T00:41:03.356688Z",
    "updatedDate": "2024-05-06T00:41:03.356688Z",
    "assetIds": [
        "urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02"
    ],
    "channel": "SENDER",
    "sendTo": "BPNL00000003CNKC",
    "sendToName": "TEST_BPN_IRS_1",
    "severity": "MINOR",
    "targetDate": null,
    "messages": []
}
```

When the notification was successfully approved/acknowledged/accepted/declined/closed/cancelled, a message for that status will be created.
The "status" and the "updatedDate" in the parent object will be updated.
```json
{
    "id": 1,
    "title": "Notification title",
    "type": "ALERT",
    "status": "APPROVED",
    "description": "Notification creation - example",
    "createdBy": "BPNL00000003CML1",
    "createdByName": "TEST_BPN_DFT_1",
    "createdDate": "2024-05-06T00:41:03.356688Z",
    "updatedDate": "2024-05-07T00:42:03.336688Z",
    "assetIds": [
        "urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02"
    ],
    "channel": "SENDER",
    "sendTo": "BPNL00000003CNKC",
    "sendToName": "TEST_BPN_IRS_1",
    "severity": "MINOR",
    "targetDate": null,
    "messages": [
        {
            "id": "9a1c137f-172f-47ee-bfd3-a3dffd370349",
            "sentBy": "BPNL00000003CML1",
            "sentByName": "TEST_BPN_DFT_1",
            "sendTo": "BPNL00000003CNKC",
            "sendToName": "TEST_BPN_IRS_1",
            "contractAgreementId": null,
            "notificationReferenceId": null,
            "edcNotificationId": "9a1c137f-172f-47ee-bfd3-a3dffd370349",
            "messageDate": "2024-05-07T00:42:03.356688Z",
            "messageId": "0deac300-e212-4c37-8d7d-83e18f01336e",
            "message": null,
            "status": "APPROVED",
            "errorMessage": null
        }
    ]
}
```

Whenever an error occurs, a new message will be created containing the status in which the error occurred.
Error messages will not be overwritten.
Here is an example of a closed notification where a couple of errors occurred when trying to send the notification.
After the third try, the process was successful. The notification was closed before the receiver could accept or decline it:
```json
{
    "id": 2,
    "title": "Notification title",
    "type": "ALERT",
    "status": "CLOSED",
    "description": "Complete notification process - example",
    "createdBy": "BPNL00000003CML1",
    "createdByName": "TEST_BPN_DFT_1",
    "createdDate": "2024-05-05T08:00:00.000000Z",
    "updatedDate": "2024-05-10T00:00:00.000000Z",
    "assetIds": [
        "urn:uuid:1be6ec59-40fb-4993-9836-acb0e284fa02"
    ],
    "channel": "SENDER",
    "sendTo": "BPNL00000003CNKC",
    "sendToName": "TEST_BPN_IRS_1",
    "severity": "MINOR",
    "targetDate": "2024-05-30T08:00:00.000000Z",
    "messages": [
        {
            "id": "9a1c137f-172f-47ee-bfd3-a3dffd370349",
            "sentBy": "BPNL00000003CML1",
            "sentByName": "TEST_BPN_DFT_1",
            "sendTo": "BPNL00000003CNKC",
            "sendToName": "TEST_BPN_IRS_1",
            "contractAgreementId": null,
            "notificationReferenceId": null,
            "edcNotificationId": "9a1c137f-172f-47ee-bfd3-a3dffd370349",
            "messageDate": "2024-05-05T08:30:00.000000Z",
            "messageId": "0deac300-e212-4c37-8d7d-83e18f01336e",
            "message": null,
            "status": "CREATED",
            "errorMessage": "DiscoveryFinder could not determine result."
        },
        {
            "id": "136e2dc5-5d2a-48b6-85a9-f9ae9d619217",
            "sentBy": "BPNL00000003CML1",
            "sentByName": "TEST_BPN_DFT_1",
            "sendTo": "BPNL00000003CNKC",
            "sendToName": "TEST_BPN_IRS_1",
            "contractAgreementId": null,
            "notificationReferenceId": null,
            "edcNotificationId": "fb4186e3-5de3-4a6b-9205-3993583267e0",
            "messageDate": "2024-05-05T09:00:00.000000Z",
            "messageId": "1d7f363b-853a-4c7d-b930-4be187fa98ce",
            "message": null,
            "status": "CREATED",
            "errorMessage": "No Catalog Item in catalog found."
        },
        {
            "id": "3befc791-326f-42f2-9301-49cb6e3795d0",
            "sentBy": "BPNL00000003CML1",
            "sentByName": "TEST_BPN_DFT_1",
            "sendTo": "BPNL00000003CNKC",
            "sendToName": "TEST_BPN_IRS_1",
            "contractAgreementId": null,
            "notificationReferenceId": null,
            "edcNotificationId": "53fdd2e3-1f23-4e83-9c36-04a43ab5ffc7",
            "messageDate": "2024-05-06T08:00:00.000000Z",
            "messageId": "97cb3c74-3a43-4ba8-9043-c346cb1d90d2",
            "message": null,
            "status": "APPROVED",
            "errorMessage": null
        },
        {
            "id": "62957c2e-0846-4394-830c-8d73f683a5d2",
            "sentBy": "BPNL00000003CNKC",
            "sentByName": "TEST_BPN_IRS_1",
            "sendTo": "BPNL00000003CML1",
            "sendToName": "TEST_BPN_DFT_1",
            "contractAgreementId": null,
            "notificationReferenceId": null,
            "edcNotificationId": "e5ae361e-da0a-4c2c-85d4-d836a02c598d",
            "messageDate": "2024-05-07T08:00:00.000000Z",
            "messageId": "9520f434-a291-4a9a-a9cd-63f13efac952",
            "message": null,
            "status": "ACKNOWLEDGED",
            "errorMessage": null
        },
        {
            "id": "43989ad5-e253-40e8-9bd0-7815f45c50bb",
            "sentBy": "BPNL00000003CML1",
            "sentByName": "TEST_BPN_DFT_1",
            "sendTo": "BPNL00000003CNKC",
            "sendToName": "TEST_BPN_IRS_1",
            "contractAgreementId": null,
            "notificationReferenceId": null,
            "edcNotificationId": "49ebd64c-0b3c-41f8-b95c-7ac6e9a49d05",
            "messageDate": "2024-05-10T00:00:00.000000Z",
            "messageId": "50aef851-bf21-44b6-98d0-3361ede2c813",
            "message": "We found the problem ourselves. Notification will be closed.",
            "status": "CLOSED",
            "errorMessage": null
        }
    ]
}
```

# Additional Details
Given the dynamic nature of ongoing development, there might be variations between the conceptualization and the current implementation. For the latest status, refer to the documentation.
