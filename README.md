![Veracode Workflow](https://github.com/catenax-ng/product-traceability-foss-frontend/actions/workflows/veracode.yaml/badge.svg)
![Tests Workflow](https://github.com/catenax-ng/product-traceability-foss-frontend/actions/workflows/test.yml/badge.svg)

<div style="display: flex; justify-items: center;">

![Alt text](src/assets/images/catena-x.svg?raw=true 'Catena-x')

<h1 style="margin: 10px 0 0 10px">Trace-FOSS User Interface</h1>

</div>

<h2>Trace-FOSS is a system for tracking parts along the supply chain.</h2>
<h4>A high level of transparency across the supplier network enables faster intervention based on a recorded event in the supply chain.
This saves costs by seamlessly tracking parts and creates trust through clearly defined and secure data access by the companies and persons involved in the process.</h4>

## Application

This application serves as a user entry point to the Catena-X network.

It's written in Typescript based on the `Angular` framework.  
We decided on using Angular because of two important aspects.  
Firstly, Angular comes with `strict guidelines`, which makes it harder to start working on for new developers, but for established developers it is `easy to start working with`.  
Secondly, Angular `scales` perfectly in the long run. Because of the restricted possibilities and stricted guidelines it is hard to implement multiple solutions for the same problem. e.g. Storing data or routing.  
With that in mind it made sense it chose Angular for an `open source` project.

Source files are exposed statically through the NGINX web server.

### Challenges and solutions

"Visualisation of traceability" is one of our most important feature, but once was one of our biggest problems.  
We wanted to achieve something that is `visually pleasing`, is `easy to use` and `performs` very good in a `browser application`.  
Our first approach was to use HTML canvas. But it turned out it is hard to perform accurate actions inside a canvas. That is why we decided on using the [D3 library](https://d3js.org/).  
D3.js is a JavaScript library for manipulating documents based on data. D3 helps you bring data to life using HTML, SVG, and CSS.  
Because it uses SVGs, we knew it will perform great. And we are able to have pinpoint accuracy when it comes to user actions.

## Getting started

Clone the source locally:

```sh
$ git clone ${path}
$ cd product-traceability-foss-frontend
```

## Configurations

If you're using angular for the first time, run `npm install -g @angular/cli` to install the angular command-line interface.

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 13.3.6.

Angular CLI is the official tool for initializing and working with Angular projects.
It saves you from the hassle of complex configurations and building tools like TypeScript, Webpack, and so on.

After installing Angular CLI, you'll need to run one command to generate a project and another to serve it using a local development server to play with your application.

Find [here](docs/configuration.md) documentation to support you during the development and configuration of the app.

## Application authentication

Please find [here](docs/authentication.md) some important information about the app authentication.

## Application architecture & patterns

This [architecture](docs/architecture.md) gives you a roadmap and best practices to follow when building an application
so that you end up with a well-structured app.

## Angular security best practices

These are the best [practices](docs/security.md) recommended to avoid vulnerabilities in your application.

## Development guidelines

These guidelines are defined to maintain homogeneous code quality and style. It can be adapted as the need arises.

New and old developers should regularly review this [guide](docs/guidelines.md) to update it as new points emerge and to sync themselves with the latest changes.

## User guide

A detailed [explanation](docs/user-guide.md) of how to use the application.

## Conventions

### Angular Template Attribute Convention

Attributes in Angular template should be properly ordered by groups:

1. `*` - Structural Directives
2. `[]` - Attribute Directives or Input parameters
3. `()` - Event listeners
4. All other attributes

## Bonus: VSCode extensions

Some VSCode extensions that might improve your coding experience :)

- Angular files
- Angular language service
- Angular schematics
- Auto rename tag
- Color info
- Color picker
- CSS peek
- Code spell checker
- Debugger for firefox
- Document this
- ESlint
- HTML CSS support
- Import cost
- Indent-rainbow
- JavaScript code snippets
- Material icon theme
- npm
- Open browser preview
- Path intellisense
- Prettier
- Stylelint
- Tailwind CSS intellisense
- TODO highlight
- TODO tree
- Version lens
- Visual Studio IntelliCode
- Yaml

## Local Keycloak

### Prerequisites

- Docker with docker-compose

### How to run local Keycloak?

Keycloak can be started through:

```
yarn env:mock
```

### Keycloak First Configuration

On the first Keycloak start it should be properly configured.

By default, it would be available at http://localhost:8080

To get to the configuration section please click on Administration Console.

Default user/password is `admin`/`admin`.

Now you can start the configuration.

Create a new Realm `mock` and select one.

In `Realm Settings` (from sidebar) -> `Security Defenses`:
Clear `X-Frame-Options`
Set `Content-Security-Policy` to `frame-src 'self'; object-src 'none’;`

In `Clients` (from sidebar)

1. Create a new client `catenax-portal`
2. Edit `catenax-portal`
   1. Set `Valid Redirect URIs` to `*`
   2. `Web Origins` to `*`

In `Roles` (from sidebar):

1. Add next roles:

- `user`
- `admin`
- `supervisor`

In Users (from sidebar):

1. Create user `default-user` with email, first name and last name, then assign to it `user` role for `catenax-portal` client and set a password (disable temp password option)
2. Create user `default-admin` with email, first name and last name, then assign to it `admin` role for `catenax-portal` client and set a password (disable temp password option)
3. Create user `default-supervisor` with email, first name and last name, then assign to it `supervisor` role for `catenax-portal` client and set a password (disable temp password option)

All done!

## How to contribute

TBD  
For now, we are following the angular guidelines which can be found here: [Angulars how to contribute](https://github.com/angular/angular-cli/blob/main/CONTRIBUTING.md)

## Branching system and release workflow

We are using the [GitHub Flow](https://docs.github.com/en/get-started/quickstart/github-flow) for our branching system.

The general idea behind this approach is that you keep the main code in a constant deployable state.  
You start off with the main branch, then a developer creates a feature branch directly from main.  
After the feature is developed the code is reviewed and tested on the branch.  
Only after the code is stable it can be merged to main.

<img src="docs/images/github-flow-branching-model.jpeg" height="60%" width="60%"/>

## License

[Apache License 2.0](./LICENSE)
