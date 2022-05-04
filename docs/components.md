## Angular Material

To support the development of components we use the angular material package.

Regarding angular materials styling, be careful with the ng-deep hack. It will affect globally all the components with the classes or selectors referred;

Either use a selector or id before the ng-deep to ensure it affects solely the desired element; 

Alternative use the Encapsulation method. Try to avoid angular materials for your sake and mental health!

## Shared Components
There are some customized components develop to facilitate the common building blocks of our application.

###  Master Detail Table

There's a shared component to be used when a table implementation is needed.

```html
    <app-table
      [dataSet]="assetData"
      [tableConfiguration]="assetTable"
      [removeSelection]="this.removeSelectedRows"
      [clickedRow]="this.selectedAsset">
    </app-table>
```

This component accepts the following parameters has an input:

| Input / Output        | Description                                                                                                                             |
| --------------------- |-----------------------------------------------------------------------------------------------------------------------------------------|
| dataSet               | This is the data which you want to display                                                                                              |
| tableConfiguration    | The column definitions and the table configurations. For example setting the table as editable to enable multiple selection             |
| removeSelection       | This boolean clears the table selected rows                                                                                             |
| clickedRow            | This may be handy for getting details of a specific row                                                                                 |
| multiColorRow         | Styles the table with multi color rows depending on the even and odd indexes                                                            |
| clickableRows         | States if the rows are clickable                                                                                                        |
| isChildClickable      | States if the child table rows are clickable                                                                                            |
| stickyHeader          | Enable the table header as sticky                                                                                                       |
| pageSizeOptions       | Provide this for a table with client side pagination (The set of provided page size options to display to the user)                     |
| pageSize              | Number of items to display on a page. By default set to 50. (Mat Paginator input)                                                       |
| clickedRowEmitter()   | Emits the clicked row                                                                                                                   |
| clickEvent()          | If the table has any actions (View, delete), this method will provide the row where the action has an impact and the name of the action |
| linkEvent()           | Event for a table cell with links                                                                                                       |
| tableSelection()      | Emits all the checked rows if selection is enable                                                                                       |


Here's an example on how you could set the table configurations:

```typescript
const detailColumns: Array<ColumnConfig> = [
  {fieldName: 'manufacturer', label: 'Manufacturer', hide: false, width: 3},
  {fieldName: 'partNameManufacturer', label: 'Part Name', hide: false, width: 4},
  {fieldName: 'partNumberManufacturer', label: 'Part Number', hide: false, width: 2},
  {fieldName: 'serialNumberManufacturer', label: 'Serial Number', hide: false, width: 5},
  {fieldName: 'qualityStatus', label: 'Quality Status', hide: false, width: 2},
  {fieldName: 'productionDateGmt', label: 'Production Date', hide: false, type: ColumnType.DATE, width: 2},
];

// build the table that contains the details
const detailTable: Table = TableFactory.buildTable(
  detailColumns, new TableConfig(false, {emptyStateReason: 'No components available'}));

const columnsConfig: Array<ColumnConfig> = [
  {fieldName: 'manufacturer', label: 'Manufacturer', hide: false, width: 2},
  {fieldName: 'partNameManufacturer', label: 'Part Name', hide: false},
  {fieldName: 'partNumberManufacturer', label: 'Part Number', hide: false},
  {fieldName: 'serialNumberManufacturer', label: 'Serial Number', hide: false, width: 6},
  {fieldName: 'qualityStatus', label: 'Quality Status', hide: false, width: 1},
  {fieldName: 'productionDateGmt', label: 'Production Date', hide: false, type: ColumnType.DATE},
  {fieldName: 'childComponents', label: 'Components', hide: false, type: ColumnType.TABLE, width: 1, detailTable},
];
return TableFactory
  .buildTable(columnsConfig, new TableConfig(true, {emptyStateReason: 'No components available'}));
```

###  Button

This component accepts the following parameters:

| Input/Output | Description                                                  |
| ------------ | ------------------------------------------------------------ |
| button       | This string may contain primary, secondary, small keywords. It controls the button styling |
| icon         | Displays an icon on that button. You should use the available icons on the /svg folder by specifying the name. |
| type         | Controls type of button ['button', 'submit', 'reset']        |
| disable      | When true, button is set to disabled state.                  |
| clickEvent() | Emit event with whole button component as argument           |



```html
<app-button
  [type]="'submit'"
  [button]="'primary'"
  [icon]="'gen-filter'"
  (clickEvent)="this.openFilter()"
>
  Filter
</app-button>
```

###  Menu Button

This component accepts the following parameters:

| Input/Output | Description                                                  |
| ------------ | ------------------------------------------------------------ |
| button       | This string may contain primary, secondary, small keywords. It controls the button styling |
| disable      | When true, button is set to disabled state                   |
| Id           | Menu id. If empty one is automatically provided.             |
| menuFor      | Template reference for the menu component                    |

E.g:

```html
  <app-menu-item
    class="mr-2"
    [menuFor]="status"
    [button]="'secondary'"
    [disable]="(this.selectedRows$ | async).length === 0"
    ><span>Change Status</span></app-menu-item
  >
```

```html
  <ng-template #status>
    <app-menu [ngStyle]="{ width: '144px' }">
      <app-menu-item *ngIf="this.queryType === 'own'">
        <section class="menu-items" (click)="this.changeStatus('OK')">
          <svg height="13" width="13">
            <circle cx="6.5" cy="6.5" r="6.5" [ngStyle]="{ fill: 'rgb(61,176,20)' }" />
          </svg>
          <label class="pl-3">OK</label>
        </section>
      </app-menu-item>
      <app-menu-item>
        <section class="menu-items" (click)="this.changeStatus('FLAG')">
          <svg height="13" width="13">
            <circle cx="6.5" cy="6.5" r="6.5" [ngStyle]="{ fill: 'rgb(255,173,31)' }" />
          </svg>
          <label class="pl-3">FLAG</label>
        </section>
      </app-menu-item>
      <app-menu-item *ngIf="this.queryType === 'own'">
        <section class="menu-items" (click)="this.changeStatus('NOK')">
          <svg height="13" width="13">
            <circle cx="6.5" cy="6.5" r="6.5" [ngStyle]="{ fill: 'rgb(210,0,0)' }" />
          </svg>
          <label class="pl-3">NOK</label>
        </section>
      </app-menu-item>
    </app-menu>
  </ng-template>
```

Between the component tags you can add any elements like we do on this example, where we have the icon and the label.

You can have as many nested menu as you want, but be careful with the amount of menus.

### Tabs

This component is build with three template references:

 - TabBodyComponent which expects a template reference
 - TabBodyLabel which expects a template reference
 - TabItemComponent which expects the following inputs:

   | Input          | Description                          |
   | -------------- | ------------------------------------ |
   | label          | Each item label                      |
   | isActive       | Checks the active item               |
   | bodyComponent  | Content child for the body template  |
   | labelComponent | Content child for the label template |


The tabs are composed with multiple tab items and these are the necessary parameters:

| Input/Output | Description                       |
| ------------ | --------------------------------- |
| tabs         | List of tab item components       |
| active()     | Emits the label of the active tab |


Here you have an example:

````html
<app-tabs>
  <app-tab-item>
    <app-tab-label label="received">
      <p class="large-text">Received</p>
    </app-tab-label>
    <app-tab-body>
      <app-received
        [receivedInvestigations]="this.receivedInvestigations"
        (triggerNavigation)="this.viewDetails($event)"
        (triggerDelete)="this.emitDelete($event)"
      ></app-received>
    </app-tab-body>
  </app-tab-item>
  <app-tab-item>
    <app-tab-label label="queued">
      <p class="large-text">Queued</p>
    </app-tab-label>
    <app-tab-body>
      <app-queued
        [queuedInvestigations]="this.queuedInvestigations"
        (triggerNavigation)="this.viewDetails($event)"
        (triggerCommit)="this.emitCommit($event)"
        (triggerDelete)="this.emitDelete($event)"
      ></app-queued>
    </app-tab-body>
  </app-tab-item>
  <app-tab-item>
    <app-tab-label label="requested">
      <p class="large-text">Requested</p>
    </app-tab-label>
    <app-tab-body>
      <app-requested
        [requestedInvestigations]="this.requestedInvestigations"
        (triggerNavigation)="this.viewDetails($event)"
      ></app-requested>
    </app-tab-body>
  </app-tab-item>
</app-tabs>
````

### Avatar

This component accepts the following parameters:

| Input           | Description                                                  |
| --------------- | ------------------------------------------------------------ |
| size            | The avatar size                                              |
| initialsTopSize | If you intend to have a avatar with the user name initials, this helps you set the top distance from the parent element |
| name            | The user name to be chopped into initials                    |
| color           | Avatar color                                                 |
| imageUrl        | Avatar image link                                            |


For example:

````html
<app-avatar
    [color]="'#fff'"
    [size]="'2rem'"
    [initialsTopSize]="'0.5rem'"
    [imageUrl]="this.getCompanyLogo()"
    >
</app-avatar>
````

### Wizard

This component accepts the following parameters:

| Input/Output     | Description                                                  |
| ---------------- | ------------------------------------------------------------ |
| steps            | List of steps available (check the steps model to see the properties) |
| bodyComponent    | Component that holds the each step content                   |
| actionsComponent | Component that holds each action steps                       |


````html
<app-wizard>
  <app-step-body>
    <!--ADD ANY CONTENT-->
  </app-step-body>
  <app-set-actions>
    <!--ADD ANY CONTENT-->
  </app-set-actions>
</app-wizard>
````

#### *This component is not used at the moment, so fine tunning might be needed.


###  Notifications

To display a notification, you need to inject the `NotificationService` on the desirable component and call the `notify` method:

```typescript
this.notificationService.success(NotificationText.EmailSent);
```

In case of error, you could use the error method to display the proper api error:

```typescript
this.notificationService.error(this.notificationService.errorServiceResponse(error));
```

This method accepts three parameters: `NotificationText | string` , `NotificationStatus` and a `timeout`:

```typescript
export enum NotificationText {
  StatusChanged = 'The status was successfully changed',
  SuccessFullyCommitted = 'Successfully committed.',
  SuccessFullyDeleted = 'Successfully deleted.',
  PreparingToDownload = 'Preparing data to download...',
  Downloading = 'Downloading...',
  SomethingWentWrong = 'Something went wrong',
  EmailSent = 'Email sent',
  LoginFailed = 'Login failed',
  LogoutError = 'Log out failed '
}
```

```typescript
export const enum NotificationStatus {
  Success = 1,
  Warning = 2,
  Error = 3,
  Informative = 4
}
```

Please add more fields to the enumerators as you see fit.

###  Loading Spinner

You could activate a loading spinner overlay for the loading states of the application. 

An HTTP call for instance.

To do that, simply inject the `SpinnerOverlayService` on the desirable component.

Now you have the available method to display the loading state.

```typescript
this.spinnerOverlayService.show();
```

Don't forget to hide it when it's no longer needed.

```typescript
this.spinnerOverlayService.hide();
```

#### Note: In some cases, it'd be better to have a skeleton loading instead of an overlay.
