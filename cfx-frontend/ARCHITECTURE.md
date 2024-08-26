## Modular design

Modular design in Angular is the act of installing a particular separation of code from each other based on the scope and problem domains.

Modular code is essentially the ability to arrange information in a categorical way that makes sense for the size of your application.

This is the basic idea of how our modules are separated.

<img alt="Modular design" src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/images/modular_design.png">

The `Core Module` is designed for the all basic and core functionalities of the application.
The singleton services, the universal components and other features where there’s only one instance per application must be stored in the
`Core Folder`.\
Within the `Core Module` you should have the `Feature Modules`, the `Core Module` and any necessary providers.
Only import components if strictly necessary.

The `Template Module` is used to store library imports, such as material modules.

When it comes to shared functionality, we have the `Shared Module`.
Here we have our reusable components, facades, models, pipes, directives, classes and services.
All the components which are going to be reusable throughout
the application must be stored in the `Shared Folder`.

A `Feature Module` delivers a cohesive set of functionality focused on a specific application need.
Here you have all the components, facades, models, pipes, directives, classes and services that complement which module.
Don't forget to import the `Shared Module` to enable the use of any reusable content.

## Lazy loading

Components routing and rendering should not be controlled through ngIf and flags, only when necessary. Angular routing should be used.

The routes are defined in `routing-modules` and should be distributed into feature modules to enable lazy loading.

The `Layout Module` has all the UI pages which were imported from its routing file.

```typescript
const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadChildren: () => import('./../dashboard/dashboard.module').then(m => m.DashboardModule),
  },
  //...
];
```

## High-level abstraction layers

The system is decomposed into three different layers. The idea is to place proper responsibility into the proper layer of the system:

- Core layer
- Abstraction layer
- Presentation layer

<img alt="Layers" src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/images/architecture.png">

This division of the system also dictates communication rules. For example, the presentation layer can talk to the core layer only through the abstraction layer.

### Presentation layer

The only responsibilities of this layer are to present and delegate.

It knows what to display and what to do, but it does not know how user interactions should be handled.

That logic is dispatched to the core layer.

### Abstraction layer

This layer exposes the streams of state and interfaces for the components in the presentation layer.

The abstraction layer is not a place to implement business logic.

This is a bridge of connection between the presentation, and the core layer.

This way we're abstraction all the API requests and eventual state requests from the components.

This facade should be responsible for receiving data from services, and for storing those newly values in our state.

E.g:

```typescript
public setAcls(): void {
    this.aclState.setAcls({ loader: true });
    this.aclService.getACL().subscribe(
      (acls: AclList) => {
        this.aclState.setOwner(acls.owner);
        this.aclState.setAcls({ data: Object.values(acls.ACL) });
      },
      error => of(this.aclState.setAcls({ error })),
    );
}
```

The facade should also notify the presentation layer of any state updates.

```typescript
get acls$(): Observable<View<Acl[]>> {
    return this.aclState.getAcls$;
}
```

### Core layer

Components are given observables with data to display on the template and don’t care how and where this data comes from.

All data manipulation and outside world communication happen here.

You should consider having these three files:

- \*.assembler.ts
- \*.service.ts
- \*.state.ts

The `services` should be responsible for any outside world communication.

Typically, they have all the API requests. You could also use services to store any helper method which you might need.

This is the layer where we manage our data `states`.

> **Note:** `RXJS` is the solution used for state management on this application.

Make sure you create any state by using the generic class located in the shared folder (/model/state.ts).
Feel free to update that class if new methods are needed

Example of a state:

```typescript
private readonly acls$: State<View<Acl[]>> = new State<View<Acl[]>>({ loader: true });
```

If your states require some extended data manipulation, you should decouple the code on the `assembler` static class.

In this example, we had to "transform" the data received from the API, so we extracted that logic.

#### State

```typescript
  public setAcls(acls: View<Acl[]>, owner?: string): void {
    const updatedOwner: string = owner ? owner : this.owner$.snapshot;
    const aclsView: View<Acl[]> = {
      data: acls.data && AclAssembler.assembleAcls(acls.data, updatedOwner),
      loader: acls.loader,
      error: acls.error,
    };
    this.acls$.update(aclsView);
  }
```

#### Assembler

```typescript
  public static assembleAcls(acls: Acl[], owner: string): Acl[] {
    const transformedAcls: Acl[] = [];
    acls.forEach(acl => {
      acl.targetOrg = acl.entities.find(entity => entity !== owner);
      acl.entities = [acl.entities.toString().replace(',', '')];
      acl.owner = owner;
      acl.status = acl.status.charAt(0).toUpperCase() + acl.status.slice(1);
    });
    transformedAcls.push(...acls);
    return transformedAcls;
  }
```

## View selector pattern

To ease up some common application states, we've implemented the view selector pattern.

The view state selector pattern binds the component state with the corresponding template.

In our case, every component state depends on data received by our state management solution.
It will start with a loading state that results in injecting the loader,
then depending on the resolved state (error or success) it will switch to the main or error view.

### View states

```typescript
export class View<T> implements OptionalViewData<T>, OptionalViewError, OptionalViewLoader {
  data?: T;
  loader?: boolean;
  error?: Error;
}
```

For managing the logic we choose a directive approach, where we inject the template on the view with the corresponding context.

```html
<ng-container *viewContainer="acls$ | async; main: mainTmp; error: errorTmp; loading: loaderTmp"> </ng-container>

<ng-template #mainTmp let-acl="view">
  <app-acl-tabs [acls]="acl.data" (updateAccessEmitter)="this.updateAccess($event)"></app-acl-tabs>
</ng-template>

<ng-template #errorTmp let-acl="view">
  <app-acl-empty-state> </app-acl-empty-state>
</ng-template>

<ng-template #loaderTmp let-acl="view">
  <app-acl-skeleton></app-acl-skeleton>
</ng-template>
```

```typescript
  @Input() set viewContainer(view: View<T>) {
    if (!view) return;

    this.context.view = view;
    this.viewContainerRef.clear();

    if (view.loader) this.viewContainerRef.createEmbeddedView(this.loaderTemplateRef, this.context);

    if (view.error && !view.loader) this.viewContainerRef.createEmbeddedView(this.errorTemplateRef, this.context);

    if (view.data && !view.error) this.viewContainerRef.createEmbeddedView(this.mainTemplateRef, this.context);
  }
```

## Smart and dumb components pattern

Within the presentation layer, we break the page into chunks, which helps us manage your page in a very flexible way by encapsulating
all the styles, markups, and business logic into each Component, and then you merge them to build your app.

There are two types or rather concepts of components.

### Smart components

- Have a facade(s) and other services injected
- Communicates with the core layer
- Pass data to the dumb components
- React to the events from dumb components
- Are top-level routable components (but not always!)

### Dumb components

- Present UI elements
- Delegate interaction up to the smart components via events

With this approach, we can take advantage of angular change detection. In dumb components instead of using a default detection strategy, we could use an on push strategy

The default strategy assumes anything about the app, which means every time something changes, as a result of various user events, times, promises, etc.., a change detection will run on all components.
This means anything from a click event to data received from an ajax call causes the change detection to be triggered.

On the other hand, the on push strategy only depends on "@inputs()" and needs to be checked by the following cases:

- Input reference changes
- Events originated from the component or one of its children
- Running the change detection explicitly

## Unidirectional data flow

<img alt="Unidirectional Data Flow" src="https://raw.githubusercontent.com/eclipse-tractusx/traceability-foss/main/docs/images/data_flow.png">

We intend to impose a similar restriction on the application layer as angular uses on the presentation layer (via input binding).
Whenever there's a change in the model, angular will detect it and propagated it.
Child components can only depend on its parent and never the other way around.
This allows Angular to traverse the components tree only once (as there are no cycles in the tree structure) to achieve a stable table,
which means that every value in the bindings is propagated.

The state can be propagated to multiple components and displayed in multiple places, but never modified locally.
The change may come only "from above" and the components below only reflect the current state of the system.
This gives us the important system property mentioned before - data consistency - and the state object becomes the single source of truth.
We can display the same data in multiple places and not be afraid that the values would differ.
