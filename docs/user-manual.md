# User manual

### Notice

This work is licensed under the [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0).

* SPDX-License-Identifier: Apache-2.0
* Licence Path: <https://creativecommons.org/licenses/by/4.0/legalcode>
* Copyright (c) 2021,2022,2023 Contributors to the Eclipse Foundation
* Copyright (c) 2022, 2023 ZF Friedrichshafen AG
* Copyright (c) 2022 ISTOS GmbH
* Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
* Copyright (c) 2022,2023 BOSCH AG
* Source URL: <https://github.com/eclipse-tractusx/traceability-foss>

### General information

Accessing the application is done by the URL provided by the hosting company.
A list of user or technical roles can be found below:

#### Trace-X app roles

Available roles: <https://portal.int.demo.catena-x.net/appusermanagement/3bbc88ae-5f0d-45cb-ab3e-8c7602ff58b4>

![app_roles](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/general-information/app_roles.png)

Assignment of roles: <https://portal.int.demo.catena-x.net/documentation/?path=docs%2F03.+User+Management%2F02.+Modify+User+Account%2F03.+User+Permissions.md>

To enable users to access an application, minimum one application app need to get assigned to the respective user(s).

1. Click on the "Add Role" button.
2. Select respective app role(s) & users to which those roles should get assigned.
3. Confirm your selection via the "Confirm" button.
4. The user role got successfully assigned and the respective users are visible in the App Access user list.

#### Catena-X portal roles

Role details: <https://portal.int.demo.catena-x.net/role-details>

![cx_portal_roles](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/user-manual/general-information/cx_portal_roles.png)

User accounts: <https://portal.int.demo.catena-x.net/documentation/?path=docs%2F03.+User+Management%2F01.+User+Account>

#### Technical users

Documentation portal: <https://portal.int.demo.catena-x.net/documentation/?path=docs%2F03.+User+Management%2F01.+User+Account>

To login use the credentials provided by the hosting company.

    The application is configured with a technical user, which holds the required IRS roles:
* Creating a policy in IRS -> admin_irs
* Requesting jobs in IRS -> view_irs

      For usage of an EDC it is necessary to provide another technical user. Required roles:
* role:  "Identity Wallet Management"

### Navigation

Navigation is done based on the top menu.

![navigation-overview](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/navigation/navigation-overview.png)

#### Dashboard

Provides a fast overview about the amount of manufactured and supplied / customer (other) parts and batches, as well as the amount of open investigations and alerts.
Lists the five newest quality investigations and alerts to get an overview of the current state.

#### Parts

Navigates to the parts and batches list view.

#### Quality topics

Navigates to the inbox of (sent/received) quality notifications.

#### About

Navigates to an overview with helpful information and links about the Catena-X Open-Source Traceability application.

#### Help

Navigates to the Trace-X documentation.

#### Go to CX Portal

Navigates to the Catena-X portal.

#### Administration

Only applicable for the admin user role.
Possibility to check the network status based on logfiles and will provide access to configuration possibilities for the application.

##### Contracts view and export

In the Contracts view an admin user can view contract agreements and sort them by the contract ID.

Also, it’s possible to select contracts and export/download them as a .csv file.

![admin_contract_view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/navigation/admin_contract_view.png)

By clicking on the burger menu of a data row you can get to the detailed view of a contract.

##### Contract detailed view

The contract detailed view is divided into two sections.

On the left, you’ll find a
general overview of the contract data.

On the right, policy details are
displayed in JSON format. Use the view selector to switch between
JSON view and JSON tree view. Expand the policy details card on the right
upper side for full-width display.

![admin_contract_detailed_view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/navigation/admin_contract_detailed_view.png)

##### Data import functionality

With the admin user role, you have the ability to import data into the system.

Click on your profile button located in the top right corner and select "Administration" from the dropdown menu.

As you can see in the picture below, you can select a file to import and click on the appearing upload button.

Find the example file at the following link:
<https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/tx-backend/testdata/import-test-data-CML1_v0.0.12.json>

![admin_upload_file](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/navigation/admin_upload_file.png)

The system will validate the file content. Upon successful validation, assets will be saved as either "AssetAsPlanned" or "AssetAsBuilt", with the import state set to "transient."

#### Sign out

Sign out the current user and return to the Catena-X portal.

#### Language

![language-icon](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/navigation/language-icon.png) Change language.\
Supported languages:

* English
* German

### Parts

List view of the own manufactured (asBuilt) or planned (asPlanned) parts and batches as well as supplier/customer parts.
You can adjust the view of tables by clicking on the fullscreen icon to maximize or minimize the view to the half of the full width.

![parts-list-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/parts/parts-list-view.png)

#### Parts table

A table view split into the different BOM lifecycle phases (AsBuilt / AsPlanned) of a part. By default, both tables are displayed.
Adjustment of the view of tables can be done by activating/deactivating the fullscreen width of the tables.

Between the views, there is a slider to adjust the view to make either the left or the right table more visible.

Both tables can be sorted, filtered, and searched.

Pre-filtration buttons can be toggled to only show own/supplier/customer parts in the table.
The global search bar at the top returns part results from both tables.

Choosing the filter input field for any column and typing in any character will show filter suggestions.

![parts-autosuggestion-filtering](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/parts/parts-autosuggestion-filtering.png)

#### AsBuilt lifecycle parts

List view of own parts with AsBuilt lifecycle.
Gives detailed information on the assets registered in the Digital Twin Registry of Catena-X for the company.
This includes data based on the aspect models of use case traceability: AsBuilt, SerialPart, Batch.
Parts that have a quality alert are highlighted yellow.

#### AsPlanned lifecycle parts

List view of own parts with AsPlanned lifecycle.
Gives detailed information on the assets registered in the Digital Twin Registry of Catena-X for the company.
This includes data based on the aspect models of use case traceability: AsPlanned, SerialPart, Batch.
Parts that have a quality alert are highlighted yellow.

#### Create quality notification from parts

Select one or multiple child components/parts/batches that are built into your part.
Selection will enable you to create a quality notification to customers (alert) or to suppliers (investigation) . For this action, click on the corresponding icon on the top left of a parts table.
The quality notification will be added to a queue (quality notifications) and not directly sent to the customer/supplier.
It is also possible to create a quality notification without the selection of parts.

Once the quality alert is created you will get a pop-up and can directly navigate to the inbox for further action.

Parts which exist in a quality notification will be highlighted as a yellow colored row in the parts table.

#### Parts selection -> Publish assets

Select one or multiple parts that are in the AsBuilt lifecycle. A button will appear on the right of the lifecycle view selection:

![publish_assets_button](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/parts/publish_assets_button.png)

Selection will enable you to publish assets with the goal to persist them (import state "persistent").
With a click on the button a window will be opened, where the selected assets are displayed and a required policy must be selected:

![publish_assets_view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/parts/publish_assets_view.png)

The following table explains the different import state an asset can have:

|     |     |
| --- | --- |
| transient | Asset is uploaded but not synchronized with the Item Relationship Service (IRS). |
| in_synchronization | Asset is ready to be published. |
| published_to_core_services | Asset is published, EDC assets, DTR shell, Submodel are created |
| persistent | Asset is successfully synchronized with the IRS. |
| unset | The import state of the asset was not set |
| error | An error occurred along the import state transition. |

#### Table column settings

On the right upper site of a table there is a settings icon in which you can set the table columns to a desired view.
With a click on it a dialog opens where you can change the settings of the corresponding table:

![other-parts-table-settings-dialog](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/parts/other-parts-table-settings-dialog.png)

Hide/show table columns by clicking on the checkbox or the column name.
It is possible to hide/show all columns by clicking on the "All" - checkbox.

The reset icon resets the table columns to its default view.

Reorder the table columns by selecting a list item (click on the right of the column name).
By selecting the column, you can reorder it with the up and down arrow icons to move it in the front or back of other columns.

Apply your changes by clicking on the "Save" - button.
If you want to discard your changes, press the "ESC" - button, click anywhere else except in the dialog or close it explicitly with the close icon on the upper right of the dialog.

The settings will be stored in the local storage of the browser and will be persisted until they get deleted.

#### Own Part details

To open the detail view, click on the three dots icon of the desired item and select "View details".
More detailed information on the asset is listed as well as a part tree that visually shows the parts relations.

![parts-list-detailed-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/parts/parts-list-detailed-view.png)

##### Overview

General production information.
Information on the quality status of the part/batch.

##### Relations

Part tree based on SingleLevelBomAsBuilt aspect model.
Dependent on the semantic data model of the part the borders are in a different color.
A green border indicates that the part is a SerialPart.
A yellow border indicates that the part is a piece of a batch.

It is possible to adjust the view of the relationships by dragging the mouse to the desired view.
Zooming in/out can be done with the corresponding control buttons.

![open-new-tab](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/parts/open-new-tab.png) Open part tree in new tab to zoom, scroll and focus in a larger view.
A minimap on the bottom right provides an overview of the current position on the part tree.

##### Asset state

Information about the import process and state of the part.

##### Manufacturer data

Detailed information on the IDs for the manufactured part/batch.

##### Customer data

Information about the identifiers at the customer for the respective part/batch.

##### Traction battery code data

If the asset has the "traction battery code" aspect model, an additional section underneath will be displayed.
In this section there is detailed information about the traction battery and a table with its subcomponents

##### Creation of a quality incident from detailed view

By clicking on the "announcement" icon you can create a quality incident from the detailed view, containing the part information in the currently opened detailed view. If this
functionality is disabled, a tooltip will provide information explaining the reason. You can trigger the tooltip by hovering above the button.

##### Publish asset from detailed view

By clicking on the "publish" icon, you can publish the currently opened part from the detailed view. If the icon is disabled,
a tooltip will provide information explaining the reason. You can trigger the tooltip by hovering above the button

#### Supplier part details

To open the detail view, click on the three dots icon of the desired item from the parts table and select "View details".
More detailed information on the asset is listed.

![supplier-parts-list-detailed-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/parts/supplier-parts-list-detailed-view.png)

##### Overview

General production information.
Information on the quality status of the supplier part/batch.

##### Manufacturer data

Detailed information on the IDs for the supplier part/batch.

##### Customer data

Information about the identifiers at the customer (in this case own company) for the respective part/batch.

#### Customer parts

List view of customer parts and batches.
Customer Parts that are in a quality alert are highlighted yellow.

#### Customer part details

To open the detail view, click on the three dots icon of the desired item and select "View details".
More detailed information on the asset is listed.

![customer-parts-list-detailed-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/parts/customer-parts-list-detailed-view.png)

##### Overview

General production information.
Information on the quality status of the customer part/batch.

##### Manufacturer data

Detailed information on the IDs for the customer part/batch.

##### Customer data

Information about the identifiers at the customer for the respective part/batch.

### Quality notifications

Inbox for received/sent quality notifications.

![investigations-list-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/quality-notifications/investigations-list-view.png)

The tables can be sorted, filtered and searched.

Choosing the filter input field for any column and typing in any character will show filter suggestions.

![investigations-autosuggestion-filtering](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/quality-notifications/investigations-autosuggestion-filtering.png)

![notification-drafts](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/quality-notifications/notification-drafts.png) Received quality notifications.

Quality notifications received by a customer.
Those notifications specify a defect or request to investigate on a specific part / batch on your side and give feedback to the customer.

![notification-send](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/quality-notifications/notification-send.png) Sent quality notifications.

Notifications in the context of quality investigations that are in queued/draft status or already requested/sent to the supplier.
Those notifications specify a defect or request to investigate on a specific part / batch on your suppliers side and give feedback back to you.

* Queued status: Quality investigation is created but not yet released.
* Requested status: Quality investigation is sent to the supplier.

#### Create a new quality notification

By clicking the announcement icon in the upper left corner of the table, you will be navigated to the notification creation view, in where you can start a quality notification from blank.

#### Table Actions

Similar to the parts table, the inbox provide a variety on actions you can apply to the listed notifications.
Some actions are related to a single notification, while other can be executed on multiple notifications at a time.

##### Actions on a single notification

Through a click on the three dots on the right of a notification list row you open the menu actions, which you can choose from.
Generally, there are the actions to edit the notification (if it’s not sent yet) or view details about it.
Additionally, there are the options to apply action alongside the lifecycle of a quality notification.

##### Actions on multiple notifications

Notifications can be selected with the checkboxes on the left of the table.
With the selection, there is a context menu for actions on mulitple (selected) notifications.
The "more" menu is opened by clicking on the horizontally aligned three dots icon.

![inbox-multiselect-actions](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/quality-notifications/inbox-multiselect-actions.png)

#### Quality notification create/edit view

A quality notification can be started by the following options:
- Part detail view if a part has child elements an quality notification with type INVESTIGATION can be created.
- Part table if parts are selected an quality notification with type ALERT can be created.
- Other parts table if parts are selected an quality notification with type INVESTIGATION can be created.

A quality notification can be edited by clicking on the context menu on an item within the inbox.

![investigation-create-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/quality-notifications/investigation-create-view.png)

#### Quality notifications context action

Select the three dots icon on the right side of an quality notification entry to open the context menu.
From there it is possible to open the quality notification detailed view or change the status of it.
Only the possible status transition will show up.

![notification-context-action](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/quality-notifications/notification-context-action.png)

Changing the status of a quality notification will open a modal in which the details to the status change can be provided and completed.

![investigation-context-action-modal](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/quality-notifications/investigation-context-action-modal.png)

A pop-up will notify you if the status transition was successful.

#### Quality notification detail view

The quality notification detail view can be opened by selecting the corresponding option in the context menu.

![investigation-detail-view](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/quality-notifications/investigation-detail-view.png)

##### Overview

General information about the quality notification.

##### Affected Parts

Listed parts that are assigned to the selected alert.

##### Supplier parts (If type is investigation)

Detailed information for child parts assigned to a quality notification.

##### Own parts (If type is alert)

Detailed information for parent parts assigned to a quality notification.

##### Message History

Displays all state transitions including the reason/description of the transition that were done on the notification to get an overview of the correspondence between sender and receiver.

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

The Sender can change the status to closed from any status.
The receiver can never change the status to closed.

The legend in the below state diagram describes who can set the status.
One exception to this rule: the transition from status SENT to status RECEIVED is done automatically once the sender receives the Http status code 201.

![Notification state model](https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/src/images/arc42/user-manual/quality-notifications/notificationstatemodel.png)
