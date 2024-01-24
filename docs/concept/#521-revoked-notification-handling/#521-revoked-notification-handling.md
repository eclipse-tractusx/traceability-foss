# \[Concept\] \[#521\] Revoked notification handling

| Key           | Value                                                                 |
|---------------|-----------------------------------------------------------------------|
| Autor         | ds-crehm                                                              |
| Creation date | 24.01.2024                                                            |
| Ticket Id     | #521 https://github.com/eclipse-tractusx/traceability-foss/issues/521 |
| State         | WIP                                                                   |

# Table of Contents
1. [Background](#background)
2. [Scope](#Scope)
3. [Requirements](#requirements)
4. [Non-functional requirements](#non-functional-requirements)
5. [Out of scope](#out-of-scope)

# Background

After a notification is sent, a contract negotiation starts by accessing the EDC.
During the negotiation the validity of the policy and relevant permissions are checked.
When the check is successful, the notification will be sent.
When the check is unsuccessful, the notification will not be sent.
Currently, in both cases the user will be informed that the notification was successfully sent.

# Scope

When the policy check is unsuccessful, the user must be notified of this failure as soon as possible.
It must be possible for him to view detailed information about the reason for this failure. He must be able to resend the notification.

# Requirements

- The policies must be validated
  - Does a matching policy exist at all?
  - Is the policy active? (validUntil date >= currentDateTime)
- If the policy is not valid, the notification must be rejected
  - New notification status. E.g. "REJECTED"/"FAILED"
  - Notification set to this status
  - Detailed status information stored in the message history of the notification
  - Pop-up informing the user (e.g. "The notification was not successful. Reason: XXX")
  - (optional) Clicking on the pop-up sends you to the detail view of the notification
- Detailed information about the reason for the rejection must be shown in the detail view of the notification
- (optional) Detailed information about the relevant policy for that notification must be shown in the detail view of the notification. Policy implementation is in progress and must be considered -> A link to the policy implementation is another possibility
- User must be able to resend the notification
  - In the notification overview
  - In the detail view

# Non-functional requirements

- The time it takes for a notification to be rejected is reasonable. If it takes too long, it will be canceled and the user will be notified.

# Out of scope

- Policies are part of the EDC
- Contract negotiation is part of the EDC
