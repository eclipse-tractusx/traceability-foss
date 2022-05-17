![Veracode Workflow](https://github.com/catenax-ng/product-traceability-foss-frontend/actions/workflows/veracode.yml/badge.svg)
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

It's written in Typescript based on Angular framework.

Source files are exposed statically through the NGINX web server.

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

## License

[Apache License 2.0](./LICENSE)
