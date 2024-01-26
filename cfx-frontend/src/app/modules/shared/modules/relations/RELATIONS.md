<div style="display: flex; justify-items: center;">

![Alt text](https://github.com/eclipse-tractusx/traceability-foss/blob/main/frontend/src/assets/images/logo.svg?raw=true)

<h1 style="margin: 10px 0 0 10px">Trace-FOSS Visualisation of relations</h1>

</div>

<h2>This Module is responsible for the visualisation of relations between assets.</h2>
<h4>"A high level of transparency across the supplier network" -> This module tackles the channels to provide an
intuitive and clear visualisation for exactly that.
The visualisation is done in a tree-like structure. By visualising the relations between assets like this,
it is easy to understand and follow.

## Structure

The module can be found in this location: `src/app/modules/page/parts/relations`.
Relations is a submodule of parts because it is only available within parts and has no separate access point.

"Relations" is set up like every other module.

- `core` => place for services, states, helpers
- `model` => all relevant interfaces, types or enums can be found here
- `presentation` => the component to visualise the module can be found here

## Architecture

The diagram is built with [d3.js](https://github.com/d3/d3). It uses SVGs to render any shape or form.
The implementation of `d3` and the custom implementation for displaying the tree can be found in `d3.tree.ts`.
The class `RelationTree` expects a config. This config should provide all necessary event handlers, properties and the
main element.
The main element should be a `figure` element. The result of the tree is displayed there as an SVG.

There are two important state variables you should keep track of. These are `openElements` and `loadedElements`.

### openElements

Open elements is a JSON object. The key contains an asset id. The value contains a list of children's asset ids.
With this one-dimensional approach, it is easy to detect and handle changes.

```JSON
{ [key: string]: string[] }
```

### loadedElements

Loaded elements has a similar structure to open Elements. But with one exception,
the value is `not a list of ids`, it contains `detailed information about an `already downloaded` asset.

With this approach, we can reduce the load on the network.
In addition to that, we are preloading the children of an opened asset. By doing that we provide nearly a seamless
navigation.

```JSON
{ [key: string]: TreeElement }
```

##### TreeElement

```JSON
{
  id: string;
  title: string;
  text?: string;

  state?: 'done' | 'loading' | 'risk';
  children?: string[];
}
```

### TreeStructure

By combining these two variables, `openElements` and `loadedElements` it is quite easy to create the structure needed to
display the tree.

As the name suggests, the structure of `TreeElement` is not that far off from the that is needed to display the tree.

```JSON
{
  id: string;
  title: string;
  text?: string;

  state?: 'done' | 'loading' | 'risk';
  children?: TreeElement[];
}
```

## How to use the module

### There are two possible ways how to use this module.

#### The first approach would be as a standalone version.

1. You first need to create a route to this component.
2. This route should accept a `partId` as a parameter
3. By providing a `partId` within the parameters of the router, the module will pull its corresponding asset.

#### The second approach would be integrated within a detailed view for assets.

1. Add this code snippet in your components HTML
   1. `<app-part-relation [treeHeight]="" [treeWidth]=""></app-part-relation>`
   2. `treeHeight` and `treeWidth` are optional, but they define the size of the rendered SVG.
2. Next you need to set a `selectedPart`
3. By setting the `selectedPart` property of the `parts.state`, the render of the diagramm is triggered.
   1. This tree will display the selected part with all its children.
