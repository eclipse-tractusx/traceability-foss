## Application configuration

After installing Angular CLI, you can run many commands

### New angular app

Run `ng new [app-name]` to create a new workspace and initialise a new angular app.

Command reference [here](https://angular.io/cli/new).

### Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

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

Run `ng test` to execute the unit tests via [jest](https://github.com/facebook/jest).

### Running updates

Run `ng update` to update your application and its dependencies.
Be careful when updating to different versions.
You should check the angular update guide for more information [here](https://update.angular.io/).

### App compiling

Run `ng build [project-name]` to compile an angular app into an output directory named `dist/` at a given output path.
Must be executed from within a workspace directory.

### Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

## Angular CLI configurations

A file named angular.json at the root level of an Angular workspace provides workspace-wide and project-specific configuration defaults for build and development tools provided by the Angular CLI.

You can find more information about the content in the official documentation [here](https://angular.io/guide/workspace-config).

In Trace-FOSS we build apps with different configurations depending on the stage (dev, stage).

These configurations must be declared in the angular JSON file, and you can define them in the `/environments` directory.

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
    "stage": {
      "fileReplacements": [
        {
          "replace": "src/environments/environment.ts",
          "with": "src/environments/environment.stage.ts"
        }
      ],
      "optimization": true,
      "outputHashing": "all",
      "sourceMap": false,
      "extractCss": true,
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
  mockService: false, // enbales mocked backend
  production: true,
  authDisabled: false, // disable keycloack auth
  keycloakUrl: 'https://auth.domain.tld/auth',
  multiTenant: true,
  defaultRealm: 'XYZRealm',
  realmLogo: '/assets/images/logo.png',
  baseUrl: '/',
  realmRegExp: '^https?://[^/]+/([-a-z-A-Z-0-9]+)',
  apiUrl: '/api', // specify where is placed backend API
};
```

## Builder

The architect section of angular.json contains a set of Architect targets.
Many of the targets correspond to the CLI commands that run them.
Some additional predefined targets can be run using the ng run command, and you can define your targets.

Each target object specifies the builder for that target, which is the npm package for the tool that Architect runs.

In addition, each target has an option section that configures default options for the target,
and a configuration section that names and specifies alternative configurations for the target.

You can find more information [here](https://angular.io/guide/cli-builder).

The default builder for an application is `@angular-devkit/build-angular:browser` which uses a webpack package bundler but since the current angular version doesn't support the CSS library Tailwind, a custom builder was necessary.

### Angular JSON builder

```json
{
  "serve": {
    "builder": "@angular-builders/custom-webpack:dev-server",
    "options": {
      "browserTarget": "webapp:build:dev",
      "proxyConfig": "./int.proxy.conf.json"
    },
    "configurations": {
      "dev": {
        "browserTarget": "webapp:build:dev"
      },
      "stage": {
        "browserTarget": "webapp:build:stage"
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

It is important to specify the type of configuration profile, previously defined in the angular JSON file, so it can pull the correct environment variables.

#### Build config

```bash
    echo "Building image for: ${TARGET_IMAGE_NAME}:${ENVIRONMENT}"

    docker login --password ${DOCKER_SECRET} --username partchain
    docker build -t ${TARGET_IMAGE_NAME}:${ENVIRONMENT} -t ${TARGET_IMAGE_NAME}:${SHORT_COMMIT_ID} -f ./build/Dockerfile . --build-arg PROFILE=${PROFILE}

    aws --region eu-west-1 ecr get-login-password \
        | docker login \
            --password-stdin \
            --username AWS \
            "${TARGET_IMAGE_NAME}"

    docker push ${TARGET_IMAGE_NAME}:${ENVIRONMENT}

    docker push ${TARGET_IMAGE_NAME}:${SHORT_COMMIT_ID}
```

## Deployment

The app is deployed on an AWS EKS instance.

For each stage, there are different account configurations available on the `/config` directory.

### Account configuration

```bash
export ACCOUNT_ID="${ACCOUNT_ID}"
export ECR_REPO_NAME="${REPO NAME}"

if [[ "${ENVIRONMENT}" ]]; then
    echo 'Setting variables for stage' $ENVIRONMENT
    readonly TARGET_IMAGE_NAME="${ACCOUNT_ID}.dkr.ecr.eu-west-1.amazonaws.com/${ECR_REPO_NAME}" #Here use dev registry
    export TARGET_IMAGE_NAME
    echo 'TARGET_IMAGE_NAME:' $TARGET_IMAGE_NAME

    readonly KUBERNETES_SECRET="${ENVIRONMENT}/${PROJECT_NAME}/kubernetesconfig"
    export KUBERNETES_SECRET
    echo 'KUBERNETES_SECRET:' $KUBERNETES_SECRET

    readonly PROFILE=dev
    export PROFILE
    echo 'ANGULAR PROFILE:' $PROFILE

    # In case of a critical security issue on the docker image delete it
    readonly DELETE_ON_FAILURE="No"
    export DELETE_ON_FAILURE


    readonly CROSS_ACC_ROLE=cx-dev-eks-crossaccount-deployment
    export CROSS_ACC_ROLE

else
    echo 'No environment variable found'
    exit 1
fi
```

You can manage the cluster contexts and image settings on the `/deploy` directory.

```bash
if [[ "$ENVIRONMENT" == "dev" ]]; then

    kubectl config use-context ${CONTEXT}
    kubectl set image deployment/ui ui=${TARGET_IMAGE_NAME}:${SHORT_COMMIT_ID} -n ${NAMESPACE} --record
```

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
