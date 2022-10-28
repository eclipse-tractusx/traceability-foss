## Application configuration

After installing Angular CLI, you can run many commands

### New angular app

Run `ng new [app-name]` to create a new workspace and initialise a new angular app.

Command reference [here](https://angular.io/cli/new).

### Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change
any of the source files.

Command reference [here](https://angular.io/cli/serve).

### Code scaffolding

Run `ng generate [artifact] [artifact-name]` to generate and/or modify files based on a schematic.
There are several Angular artefacts which you can generate:

- component
- directive
- pipe
- service
- class
- guard
- interface
- enum
- module
- ...

### Running unit tests

Run `ng test` to execute the unit tests via [karma](https://karma-runner.github.io/latest/index.html) + [jasmine](https://jasmine.github.io/).

### Running updates

Run `ng update` to update your application and its dependencies.
Be careful when updating to different versions.
You should check the angular update guide for more information [here](https://update.angular.io/).

### App compiling

Run `ng build [project-name]` to compile an angular app into an output directory named `dist/` at a given output path.
Must be executed from within a workspace directory.

### Further help

To get more help on the Angular CLI use `ng help` or go check out
the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

## Angular CLI configurations

A file named angular.json at the root level of an Angular workspace provides workspace-wide and project-specific
configuration defaults for build and development tools provided by the Angular CLI.

You can find more information about the content in the official
documentation [here](https://angular.io/guide/workspace-config).

In Trace-FOSS we build apps with different configurations depending on the stage (dev, stage).

These configurations must be declared in the angular JSON file, and you can define them in the `/environments`
directory.

Then, on the file replacements array, you can replace the URL with the corresponding environment file.

### angular.json

```json
{
  "configurations": {
    "dev": {
      "buildOptimizer": false,
      "optimization": false,
      "vendorChunk": true,
      "extractLicenses": false,
      "sourceMap": true,
      "namedChunks": true,
      "aot": true,
      "budgets": [
        {
          "type": "initial",
          "maximumWarning": "5mb",
          "maximumError": "10mb"
        }
      ]
    },
    "auth": {
      "fileReplacements": [
        {
          "replace": "src/environments/environment.ts",
          "with": "src/environments/environment.auth.ts"
        }
      ]
    },
    "authLocal": {
      "fileReplacements": [
        {
          "replace": "src/environments/environment.ts",
          "with": "src/environments/environment.authMock.ts"
        }
      ]
    },
    "authMock": {
      "fileReplacements": [
        {
          "replace": "src/environments/environment.ts",
          "with": "src/environments/environment.authMock.ts"
        }
      ]
    },
    "production": {
      "fileReplacements": [
        {
          "replace": "src/environments/environment.ts",
          "with": "src/environments/environment.production.ts"
        }
      ],
      "optimization": true,
      "outputHashing": "all",
      "sourceMap": false,
      "namedChunks": false,
      "aot": true,
      "extractLicenses": true,
      "vendorChunk": false,
      "buildOptimizer": true,
      "budgets": [
        {
          "type": "initial",
          "maximumWarning": "5mb",
          "maximumError": "10mb"
        }
      ]
    }
  }
}
```

### Environment variables

```typescript
export const environment = {
  production: false,
  mockService: false, // enbales mocked backend
  authDisabled: false, // disable keycloak auth
  multiTenant: false,
  keycloakUrl: 'http://localhost:8080/',
  clientId: 'catenax-portal',
  defaultRealm: 'mock',
  realmLogo: '/assets/images/logo.png',
  apiUrl: '/api', // specify where is placed backend API
  realmRegExp: '^https?://[^/]+/([-a-z-A-Z-0-9]+)',
  baseUrl: '/',
  mapStyles: 'mapbox://styles/mapbox/light-v10',
};
```

#### Custom protocols

There is a possibility to define custom protocols with resolution to actual URLs.

Thanks to this feature, there is possibility to use mapbox styles, if register custom protocols like this:

```typescript
export const environment = {
  ...otherEnvs,
  customProtocols: {
    mapbox: {
      '//fonts/mapbox': {
        pathname: 'https://api.mapbox.com/fonts/v1/mapbox',
        queryParams: {
          access_token: MAPBOX_ACCESS_TOKEN,
        },
      },
      '//mapbox.': {
        pathname: 'https://api.mapbox.com/v4/mapbox.',
        postfix: '.json',
        queryParams: {
          secure: '',
          access_token: MAPBOX_ACCESS_TOKEN,
        },
      },
      '//styles/': {
        pathname: 'https://api.mapbox.com/styles/v1/',
        queryParams: {
          access_token: MAPBOX_ACCESS_TOKEN,
        },
      },
      '//sprites/mapbox/light-v10': {
        pathname: 'https://api.mapbox.com/styles/v1/mapbox/light-v10/sprite',
        queryParams: {
          access_token: MAPBOX_ACCESS_TOKEN,
        },
      },
    },
  },
};
```

Keys in `customProtocols` is protocol which would be present in actual URL, and inside details
about each URL prefix (after protocol) resolution.

## Builder

The architect section of angular.json contains a set of Architect targets.
Many of the targets correspond to the CLI commands that run them.
Some additional predefined targets can be run using the ng run command, and you can define your targets.

Each target object specifies the builder for that target, which is the npm package for the tool that Architect runs.

In addition, each target has an option section that configures default options for the target,
and a configuration section that names and specifies alternative configurations for the target.

You can find more information [here](https://angular.io/guide/cli-builder).

The default builder for an application is `@angular-devkit/build-angular:browser` which uses a webpack package bundler
but since the current angular version doesn't support the CSS library Tailwind, a custom builder was necessary.

### Angular JSON builder

```json
{
  "serve": {
    "builder": "@angular-builders/custom-webpack:dev-server",
    "options": {
      "browserTarget": "trace-x:build:dev",
      "proxyConfig": "./int.proxy.conf.json"
    },
    "configurations": {
      "dev": {
        "browserTarget": "trace-x:build:dev"
      },
      "stage": {
        "browserTarget": "trace-x:build:stage"
      }
    }
  }
}
```

### Webpack configuration

```javascript
module.exports = {
  module: {
    rules: [
      {
        test: /\.scss$/,
        loader: 'postcss-loader',
        options: {
          postcssOptions: {
            ident: 'postcss',
            syntax: 'postcss-scss',
            plugins: [
              require('postcss-import'),
              require('tailwindcss/nesting'),
              require('tailwindcss'),
              require('autoprefixer'),
            ],
          },
        },
      },
    ],
  },
};
```

From version 11.2 onwards Tailwind is already supported.

## Build

The angular app is built into Docker containers and exposed through NGINX

It is important to specify the type of configuration profile, previously defined in the angular JSON file, so it can
build the application correctly.

## Run

When running the build docker image you are able to pass through multiple environment variables to configure the FE.
Support environment variables are:

```javascript
const ENV_VARS_MAPPING = {
  CATENAX_PORTAL_KEYCLOAK_URL: 'keycloakUrl',
  CATENAX_PORTAL_CLIENT_ID: 'clientId',
  CATENAX_PORTAL_DEFAULT_REALM: 'defaultRealm',
  CATENAX_PORTAL_REALM_LOGO: 'realmLogo',
  CATENAX_PORTAL_API_URL: 'apiUrl',
  CATENAX_PORTAL_BASE_URL: 'baseUrl',
};
```

`CATENAX_PORTAL_KEYCLOAK_URL`
This variable is used to set up and use keycloak

`CATENAX_PORTAL_CLIENT_ID`
This variable is used to identify the client on keycloak

`CATENAX_PORTAL_DEFAULT_REALM`
This variable is used the set de default realm of the application

`CATENAX_PORTAL_REALM_LOGO`
This variable is used to replace the logo on the application

`CATENAX_PORTAL_API_URL`
This variable points to the desired api

`CATENAX_PORTAL_BASE_URL`
This variable is used to set the base path of the application. (Should be set if application runs as a subtopic)

## Deployment

The app is deployed on hotel budapest.

For each stage, there are different pipelines. The configuration for these pipelines can be found here: `/charts`.

## Package Manager

This app uses [yarn](https://yarnpkg.com/) to manage dependencies.
These are the commands you must certainly use:

#### Install all dependencies

```bash
yarn
yarn install
```

#### Add new dependencies

```bash
yarn add [package]
yarn add [package]@[version]
yarn add [package]@[tag]
```

#### Adding a dependency to different categories of dependencies

```bash
yarn add [package] --dev  # dev dependencies
yarn add [package] --peer # peer dependencies
```

#### Upgrading a dependency

```bash
yarn up [package]
yarn up [package]@[version]
yarn up [package]@[tag]
```

#### Removing a dependency

```bash
yarn remove [package]
```
