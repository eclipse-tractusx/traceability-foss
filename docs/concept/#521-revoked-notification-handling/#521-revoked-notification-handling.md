# \[Concept\] \[#521\] Revoked notification handling

| Key           | Value                                                                 |
|---------------|-----------------------------------------------------------------------|
| Autor         | ds-crehm                                                              |
| Creation date | 24.01.2024                                                            |
| Ticket Id     | #521 https://github.com/eclipse-tractusx/traceability-foss/issues/521 |
| State         | WIP                                                                   |

# Table of Contents
1. [Description](#description)
2. [Requirements](#requirements)
3. [Non-functional requirements](#non-functional-requirements)
4. [Out of scope](#out-of-scope)

# Description

After a notification is sent, the policy must first be checked for the asset and the relevant BPNs.
If the policies are invalid or expired, the notification must be rejected and the user notified of the rejection.

# Requirements

- The relevant policies must be fetched from the EDC
- The policies must be validated
  - Is the policy active?
  - Is the connection to the requested BPN allowed?
  - Is the requested notification to this BPN allowed?
- If the policy is not valid, the notification must be rejected
  - Status set to "REJECTED"
  - Status information stored in the notification (detailed description why it failed)
  - Pop-up informing the user ("The notification was rejected. Reason: XXX")
  - (optional) Clicking on the pop-up sends you to the detail view of the notification
- Detailed information about the reason for the rejection must be shown in the detail view of the notification
- (optional) Detailed information about the relevant policy for that notification must be shown in the detail view of the notification. Policy implementation is in progress and must be considered -> A link to the policy implementation is another possibility
- The user must be able to review the notification (and the policy -> policy implementation) and resend it

# Non-functional requirements

- The time it takes for a notification to be rejected is reasonable. If it takes too long, it will be canceled and the user will be notified.

# Out of scope

- Policies are part of the EDC
