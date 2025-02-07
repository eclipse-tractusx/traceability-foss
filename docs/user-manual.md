# User manual

### Notice

This work is licensed under the [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0).

* SPDX-License-Identifier: Apache-2.0
* Licence Path: <https://creativecommons.org/licenses/by/4.0/legalcode>
* Copyright (c) 2021, 2022, 2023, 2024 Contributors to the Eclipse Foundation
* Copyright (c) 2022, 2023 ZF Friedrichshafen AG
* Copyright (c) 2022 ISTOS GmbH
* Copyright (c) 2022, 2023, 2024 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
* Copyright (c) 2022, 2023 BOSCH AG
* Source URL: <https://github.com/eclipse-tractusx/traceability-foss>

### General information

Accessing the application is done by the URL provided by the hosting company.
A list of user or technical roles can be found below:

#### Trace-X app roles

Available roles: Admin. User , Supervisor

![app-roles](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/general-information/app-roles.svg)

Assignment of roles: <https://github.com/eclipse-tractusx/portal-assets/blob/d922bf4cbbccccab1b30ff3be5184e52c9a94c5e/docs/user/03.%20User%20Management/02.%20Modify%20User%20Account/03.%20User%20Permissions.md>

To enable users to access an application, minimum one application app need to get assigned to the respective user(s).

1. Click on the "Add Role" button.
2. Select respective app role(s) & users to which those roles should get assigned.
3. Confirm your selection via the "Confirm" button.
4. The user role got successfully assigned and the respective users are visible in the App Access user list.

#### Catena-X portal roles

Role details: <https://portal.int.demo.catena-x.net/role-details>

![cx-portal-roles](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/general-information/cx-portal-roles.svg)

User accounts: <https://github.com/eclipse-tractusx/portal-assets/blob/d922bf4cbbccccab1b30ff3be5184e52c9a94c5e/docs/user/03.%20User%20Management/01.%20User%20Account/02.%20User%20Account.md>

#### Technical users

Documentation of portal: <https://portal.int.demo.catena-x.net/documentation/?path=docs%2F03.+User+Management%2F01.+User+Account>

To login use the credentials provided by the hosting company.

The application is configured with a technical user, which holds the required IRS roles:

* Creating a policy in IRS -> admin_irs
* Requesting orders in IRS -> view_irs

For usage of an EDC it is necessary to provide another technical user. Required roles:

* Role:  "Identity Wallet Management"

### Navigation

Navigation is done based on the top menu.

![navigation-overview](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/navigation/navigation-overview.svg)

#### Dashboard

Provides a fast overview about the amount of manufactured (own) and supplied / customer (other) parts and batches, as well as the amount of open investigations and alerts.
Lists the five newest quality investigations and alerts to get an overview of the current state.

#### Parts

Navigates to the parts and batches list view.

#### Quality notifications

Navigates to the inbox of (sent / received) quality notifications.

#### About

Navigates to an overview with helpful information and links about Trace-X.

#### Help

Navigates to the Trace-X documentation.

#### Go to CX Portal

Navigates to the Catena-X portal.

#### Administration

Only applicable for the admin user role.
Possibility to view and change specific configuration settings of the application. Assets can be imported here and policies can be created.

##### BPN-EDC configuration

In the BPN-EDC configuration screen an admin user can add URLs for BPNs. Existing configurations can be edited or removed.

![bpn-edc-configuration](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/navigation/bpn-edc-configuration.svg)

Any configured BPNs will have their URLs merged in addition to the URLs found by the DiscoveryService.
The resulting list of URLs will be used whenever a notification is sent to those BPNs. In case a URL returns an error response, the remaining URLs will be used as backup.

![user-manual_000](https://eclipse-tractusx.github.io/traceability-foss/docs/assets/user-manual/user-manual_000.png)

| ID | Description |
| --- | --- |
| 01 | Any URLs configured for the selected BPN in the BPN-EDC configuration are requested. |
| 02 | Any URLs configured for the selected BPN in the DiscoveryFinder service are requested. |
| 03 | All received URLs are merged into one list with identical URLs discarded. |

##### Contracts - view and export

In the Contracts view an admin user can view contract agreements and sort them only by the contract ID.

With the quick filter buttons it is possible to filter the contracts based on types.
Either show contracts related to notifications, or only ones related to parts.

By clicking on the burger menu of a data row you can get to the detailed view of a contract.
Depending on the contract type, you can directly navigate to the item under this contract.

Also, it’s possible to select contracts and export / download them as a .csv file.

![admin-contract-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/navigation/admin-contract-view.svg)

###### Contract detailed view

The contract detailed view is divided into two sections.

On the left, you’ll find a general overview of the contract data.

On the right, policy details are displayed in JSON format. Use the view selector to switch between JSON view and JSON tree view. Expand the policy details card on the right upper side for full-width display.

![admin-contract-detailed-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/navigation/admin-contract-detailed-view.svg)

##### Data provisioning

With the admin user role you have the ability to import data into the system.
Click on your profile button located in the top right corner and select "Administration" from the dropdown menu.

As you can see in the picture below, you can select a file to import and click on the appearing upload button.

Find the example file at the following link:
<https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/tx-backend/testdata/import-test-data-CML1_v0.0.14.json>

![admin-upload-file](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/navigation/admin-upload-file.svg)

The system will validate the file content. Upon successful validation assets will be saved as either "AssetAsPlanned" or "AssetAsBuilt" with the import state set to "transient."

#### Policy management

The policy management feature allows administrators to create, edit, view, and delete policies within the system.
This section provides an overview of how to use these features effectively.

##### Policies list view

The policies list view displays all the policies in a tabular format.
You can perform various actions such as view, edit, and delete policies from this view.

![policies-list-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/policy-management/policies-list-view.svg)

To access the policies list view, navigate to the "Policies" section from the top menu.

By clicking on the settings symbol in the top right corner of the table, you are able to customize the visibility and order of the table columns.

In the top left corner you can initiate the creation or deletion of policies.

###### Deleting policies

To delete policies follow these steps:

Select the policies you wish to delete by checking the boxes next to them.
Click on the delete icon to open the deletion dialog.
Confirm the deletion in the dialog.
The system will then remove the selected policies and update the list view.

![delete-policies-dialog](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/policy-management/delete-policies-dialog.svg)

##### Policy editor / detailed view

The policy editor allows you to create, edit, and view detailed information about a policy.

![policy-editor](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/policy-management/policy-editor.svg)

Note: For existing policies, it is currently only possible to edit the valid until date and the BPN.

###### Creating policies

To create a policy:

1. Navigate to the "Create Policy" section from the policies list view by clicking the plus icon in the top left corner of the table.
2. Fill in the policy details including policy name, validity date, BPN(s), access type, and constraints.
3. Save the policy using the save button.
4. The system will validate the inputs and update the policy accordingly.

![policy-create](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/policy-management/policy-create.svg)

##### Constraints

Constraints define the conditions under which the policy is applicable.
You can add, edit, and remove constraints in the policy editor.

To add a constraint click the add button in the constraints section. Then fill the left operand, operator, and right operand.

To remove a constraint, click the delete button next to the constraint.

To move constraints up or down in the list, use the up and down arrow buttons.

![policy-constraints](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/policy-management/policy-constraints.svg)

#### Sign out

Sign out the current user and return to the Catena-X portal.

#### Language

![language-icon](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/navigation/language-icon.svg) Change language.\
Supported languages:

* English
* German

### Parts

List view of the own manufactured (asBuilt) or planned (asPlanned) parts and batches as well as supplier/customer parts.
You can adjust the view of tables by clicking on the fullscreen icon to maximize or minimize the view to the half of the full width.

![parts-list-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/parts/parts-list-view.svg)

#### Parts table

A table view split into the different BOM lifecycle phases (AsBuilt / AsPlanned) of a part. By default, both tables are displayed.
Adjustment of the view of tables can be done by activating/deactivating the fullscreen width of the tables.

Between the views there is a slider to adjust the view to make either the left or the right table more visible.

Both tables can be sorted, filtered, and searched.

Pre-filtration buttons can be toggled to only show own / supplier / customer parts in the table.
The global search bar at the top returns part results from both tables.

Choosing the filter input field for any column and typing in any character will show filter suggestions.

![parts-autosuggestion-filtering](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/parts/parts-autosuggestion-filtering.svg)

#### AsBuilt lifecycle parts

List view of own parts with AsBuilt lifecycle.
Gives detailed information on the assets registered in the Digital Twin Registry of Catena-X for the company.
This includes data based on the aspect models of use case traceability: AsBuilt, SerialPart, Batch.
Parts that have an active quality notification are highlighted yellow.

#### AsPlanned lifecycle parts

List view of own parts with AsPlanned lifecycle.
Gives detailed information on the assets registered in the Digital Twin Registry of Catena-X for the company.
This includes data based on the aspect models of use case traceability: AsPlanned, SerialPart, Batch.
Parts that have an active quality notification are highlighted yellow.

#### Create quality notification from parts

Select one or multiple child components / parts / batches that are built into your part.
A selection will enable you to create a quality notification for own parts (alert) or supplier parts (investigation). For this action click on the icon on the top left of a parts table.
The quality notification will be added to a queue (quality notifications) and not directly sent to the customer / supplier.
It is also possible to create a quality notification without selecting any parts.

Once the quality alert is created you will get a pop-up and can directly navigate to the quality notification inbox for further action.

Parts which exist in an active quality notification will be highlighted as a yellow colored row in the parts table.

#### Parts selection -> Publish assets

Only an administrator can publish parts.

Select one or multiple parts that are in the AsBuilt lifecycle. The publish button will turn black on the right of the lifecycle view selection:

![publish-assets-button](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/parts/publish-assets-button.svg)

With a click on the button a window will be opened, where the selected assets are displayed and a required policy must be selected:

![publish-assets-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/parts/publish-assets-view.svg)

Then the selected parts will be published.

The following table explains the different import states an asset can have:

|     |     |
| --- | --- |
| transient | Asset is uploaded but not synchronized with the Item Relationship Service (IRS). |
| in_synchronization | Asset is ready to be published. |
| published_to_core_services | Asset is published, EDC assets, DTR shell, Submodel are created. |
| persistent | Asset is successfully synchronized with the IRS. |
| unset | The import state of the asset is not set. |
| error | An error occurred along the import state transition. |

#### Table column settings

On the right upper site of a table there is a settings icon in which you can set the table columns to a desired view.
With a click on it a dialog opens where you can change the settings of the corresponding table:

![other-parts-table-settings-dialog](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/parts/other-parts-table-settings-dialog.svg)

Hide / show table columns by clicking on the checkbox or the column name.
It is possible to hide / show all columns by clicking on the "All"-checkbox.

The reset icon resets the table columns to its default view.

Reorder the table columns by selecting a list item by clicking on the name. You can reorder it with the up and down arrow icons.

Apply your changes by clicking on the "Save"-button.
If you want to discard your changes, press the "ESC"-button, click anywhere else except in the dialog or close it explicitly with the close icon on the upper right of the dialog.

The settings will be stored in the local storage of the browser and will be persisted until they get deleted.

#### Own part details

To open the detail view, click on the three dots icon of the desired item and select "View details".
More detailed information on the asset is listed as well as a part tree that visually shows the parts relations.

![parts-list-detailed-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/parts/parts-list-detailed-view.svg)

##### Overview

General production information.
Information on the quality status of the part / batch.

##### Relations

Part tree based on SingleLevelBomAsBuilt aspect model.
Dependent on the semantic data model of the part the borders are in a different color.
A green border indicates that the part is a SerialPart.
A yellow border indicates that the part is a piece of a batch.
A black border indicates that the part is a JustInSequence part.

It is possible to adjust the view of the relationships by dragging the mouse to the desired view.
Zooming in / out can be done with the corresponding control buttons.

![open-new-tab](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/parts/open-new-tab.svg) Open part tree in new tab to zoom, scroll and focus in a larger view.
A minimap on the bottom right provides an overview of the current position on the part tree.

##### Asset state

Information about the import process and state of the part.

##### Manufacturer data

Detailed information on the IDs for the manufactured part / batch.

##### Customer data

Information about the identifiers at the customer for the respective part / batch.

##### Traction battery code data

If the asset has the "traction battery code" aspect model, an additional section underneath will be displayed.
In this section there is detailed information about the traction battery and a table with its subcomponents.

##### Creation of a quality notification from detailed view

By clicking on the "announcement" icon you can create a quality notification from the detailed view, containing the part information in the currently opened detailed view.
If this functionality is disabled, a tooltip will provide information explaining the reason. The tooltip is visible when hovering the mouse in front of the button.

##### Publish asset from detailed view

By clicking on the "publish" icon you can publish the currently opened part from the detailed view. If the icon is disabled, a tooltip will provide information explaining the reason. The tooltip is visible when hovering the mouse in front of the button.

#### Supplier part details

To open the detail view click on the three dots icon of the desired item from the parts table and select "View details".
More detailed information on the asset is listed.

![supplier-parts-list-detailed-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/parts/supplier-parts-list-detailed-view.svg)

##### Overview

General production information.
Information on the quality status of the supplier part / batch.

##### Manufacturer data

Detailed information on the IDs for the supplier part / batch.

##### Customer data

Information about the identifiers at the customer (in this case own company) for the respective part / batch.

#### Customer parts

List view of customer parts and batches.
Customer parts that are in an active quality notification are highlighted yellow.

#### Customer part details

To open the detail view click on the three dots icon of the desired item and select "View details".
More detailed information on the asset is listed.

![customer-parts-list-detailed-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/parts/customer-parts-list-detailed-view.svg)

##### Overview

General production information.
Information on the quality status of the customer part / batch.

##### Manufacturer data

Detailed information on the IDs for the customer part / batch.

##### Customer data

Information about the identifiers at the customer for the respective part / batch.

### Quality notifications

Inbox for received / sent quality notifications.

![investigations-list-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/quality-notifications/investigations-list-view.svg)

The tables can be sorted, filtered and searched.

Choosing the filter input field for any column and typing in any character will show filter suggestions.

![investigations-autosuggestion-filtering](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/quality-notifications/investigations-autosuggestion-filtering.svg)

![notification-drafts](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/quality-notifications/notification-drafts.svg) Received quality notifications

Quality notifications received by a customer.
Those notifications specify a defect or request to investigate on a specific part / batch on your side and give feedback to the customer.

![notification-send](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/quality-notifications/notification-send.svg) Sent quality notifications

Notifications in the context of quality investigations that are in queued status or already sent to the supplier.
Those notifications specify a defect or request to investigate on a specific part / batch on your suppliers side and give feedback back to you.

* Queued status: Quality investigation is created but not yet released.
* Requested status: Quality investigation is sent to the supplier.

#### Create a new quality notification

By clicking the announcement icon in the upper left corner of the table, you will be navigated to the notification creation view, where you can start a quality notification from scratch.

#### Table Actions

Similar to the parts table the inbox provides a variety of actions you can apply to the listed notifications.
Some actions are related to a single notification, while others can be executed on multiple notifications at a time.

##### Actions on a single notification

With a click on the three dots on the right of a notification list row you open the menu actions.
You can view details of the notification or edit it.
Additionally, there are options to choose from alongside the lifecycle of a quality notification (cancel, approve, acknowledge, accept, decline, close).

##### Actions on multiple notifications

Notifications can be selected with the checkboxes on the left of the table.
With the selection there is a context menu for actions on multiple (selected) notifications.
The "more" menu is opened by clicking on the horizontally aligned three dots icon.

![inbox-multiselect-actions](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/quality-notifications/inbox-multiselect-actions.svg)

#### Quality notification create / edit view

A quality notification can be started by the following options:

* Part detail view, if a part has child elements: A quality notification with type INVESTIGATION can be created.
* Part table, if own parts are selected: A quality notification with type ALERT can be created.
* Part table, if supplier parts are selected: A quality notification with type INVESTIGATION can be created.
* Part table, if no parts are selected: A blank quality notification can be created.
* Quality notification table: A blank quality notification can be created.

A quality notification can be edited by clicking on the context menu on an item within the inbox.

![investigation-create-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/quality-notifications/investigation-create-view.svg)

#### Quality notifications context action

Select the three dots icon on the right side of a quality notification entry to open the context menu.
From there it is possible to open the quality notification detailed view or change the status of it.
Only possible status transitions will show up.

![notification-context-action](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/quality-notifications/notification-context-action.svg)

Changing the status of a quality notification will open a modal in which the details to the status change can be provided and completed.

![investigation-context-action-modal](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/quality-notifications/investigation-context-action-modal.svg)

A pop-up will notify you if the status transition was successful.

#### Quality notification detail view

The quality notification detail view can be opened by selecting the corresponding option in the context menu.

![investigation-detail-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/quality-notifications/investigation-detail-view.svg)

##### Overview

General information about the quality notification.

##### Affected parts

Listed parts that are assigned to the selected quality notification.

##### Message history

Displays all state transitions including the reason / description of the transition that were done on the notification to get an overview of the correspondence between sender and receiver.

##### Quality notification action

All possible state transitions are displayed in form of buttons (upper right corner).
There the desired action can be selected to open a modal in which the details to the status change can be provided and completed.

#### Quality notification  status

Following status for a quality notification are possible:

| Status | Description |
| --- | --- |
| Queued | A quality notification that was created by a user but not yet sent to the receiver. |
| Requested | Created quality notification that is already sent to the receiver. |
| Cancelled | Created quality notification that is not yet sent to the receiver and got cancelled on sender side before doing so. It is no longer valid / necessary. |
| Received | Received notification from a sender which needs to be investigated. |
| Acknowledged | The receiver acknowledged to work on the received inquiry. |
| Accepted | The receiver accepted the inquiry. Issue on part/batch detected. |
| Declined | The receiver declined the inquiry. No issue on part/batch detected. |
| Closed | The sender closed the quality notification and no further handling with it is possible. |

#### Quality notification status flow

Quality notifications always have a status.
The transition from one status to a subsequent status is described in the below state model.

The sender can change the status to closed from any status.
The receiver can never change the status to closed.

The legend in the below state diagram describes who can set the status.
One exception to this rule: The transition from status SENT to status RECEIVED is done automatically once the sender receives the Http status code 201.

![Notification state model](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/quality-notifications/notificationstatemodel.svg)
