# Installation Instructions

## Installation Instructions Helm

### Install Helm chart from the Helm chart repository

```
# 1. Add the helm chart repository:
helm repo add tractusx-dev https://eclipse-tractusx.github.io/
```

```
# 2. Install traceability-foss chart into your cluster:
helm install traceability-foss tractusx-dev/traceability-foss
```
### Use Helm chart as dependency on your own Helm chart

```
    dependencies:
      - name: traceability-foss
        repository: https://eclipse-tractusx.github.io/traceability-foss
        version: 1.x.x
```

### Configure product-example Helm chart on your needs

Please have a look into our [Helm chart documentation](charts/traceability-foss/README.md) for more information about the configuration options.

## Installation Instructions Frontend

### Prerequisites

* [Node.js 18](https://nodejs.org/en)
* [Angular CLI](https://angular.io/cli)
* [YARN](https://yarnpkg.com/)

### Getting started

Clone the source locally:

```bash
$ git clone git@github.com:eclipse-tractusx/traceability-foss.git
$ cd traceability-foss/frontend
```

#### Install prerequisites:

install [node.js](https://nodejs.org/en/download/package-manager)
```bash
$ npm install --global yarn
$ npm install -g @angular/cli
$ yarn install
```

Start application with ``yarn start`` and navigate to ``http://localhost:4200``.

To run the frontend locally with an already existing backend, use the run configs in [frontend/package.json](frontend/package.json).

Add a run config by creating a new entry in [package.json](frontend/package.json) similar to `"start:auth": "ng serve --configuration=dev,auth"` and adding a entry in [angular.json](frontend/angular.json) at "projects.trace-x.architect.build.configuratns" and "projects.trace-x.architect.serve.configurations".

Add an environment by creating new file in [frontend/src/environments](frontend/src/environments) and reference it in the [package.json](frontend/package.json) build configuration.

## Installation Instructions Local Umbrella

### Install Umbrella

Using the [eclipse-tractusx/tractus-x-umbrella](https://github.com/eclipse-tractusx/tractus-x-umbrella) helm chart, all traceability-foss dependencies can be installed locally.

For detailed instructions on how to set up the umbrella chart, see the chapters "Cluster setup" and "Network setup" in the umbrella [README.md](https://github.com/eclipse-tractusx/tractus-x-umbrella/blob/main/charts/umbrella/README.md).

Clone the [Umbrella repo](https://github.com/eclipse-tractusx/tractus-x-umbrella) (only required once):

```
git clone https://github.com/eclipse-tractusx/tractus-x-umbrella.git
```

Check out the [Trace-X umbrella integration branch](https://github.com/eclipse-tractusx/tractus-x-umbrella/tree/chore/trace-x-integration):

```
cd tractus-x-umbrella/
git fetch origin
git checkout -b chore/trace-x-integration origin/chore/trace-x-integration
```

Build the required images for Idp and IATP mock.

#### Powershell
```powershell
& minikube -p minikube docker-env --shell powershell | Invoke-Expression
docker build init-container/ -t init-container:testing
docker build iatp-mock/ -t tractusx/iatp-mock:testing --platform linux/amd64
```

#### Bash
```bash
eval $(minikube docker-env)
docker build init-container/ -t init-container:testing
docker build iatp-mock/ -t tractusx/iatp-mock:testing --platform linux/amd64
```

Install the umbrella chart using the [values-adopter-trace-x.yaml](https://github.com/eclipse-tractusx/tractus-x-umbrella/blob/chore/trace-x-integration/charts/umbrella/values-adopter-trace-x.yaml)

```
helm dependency update charts/tx-data-provider/
helm dependency update charts/umbrella/
helm install umbrella charts/umbrella/ -n umbrella --create-namespace -f charts/umbrella/values-adopter-trace-x.yaml
```

### Local Frontend with Umbrella Trace-X backend

To integrate with the umbrella services, run the frontend with `start:auth:localUmbrella`

Afterward, use one of the following users to log into the CX-Operator realm:

| Role       | Username                  | Password                      |
|------------|---------------------------|-------------------------------|
| Supervisor | tracex-supervisor@tx.test | tractusx-umbr3lla!-supervisor |
| Admin      | tracex-admin@tx.test      | tractusx-umbr3lla!-admin      |
| User       | tracex-user@tx.test       | tractusx-umbr3lla!-user       |

### Out of scope

- Sending/receiving of quality notifications is not supported, since it would require another tracex instance.
- IRS Asset Sync does not yet work with IRS 5.4.0. It requires a new release of IRS which provides a fix for callback url validation.
