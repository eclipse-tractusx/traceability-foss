# Installation Instructions

## Installation Instructions Frontend

- [Installation Instructions Frontend](frontend/INSTALL.md)

## Installation Instructions Backend

- [Installation Instructions Backend](tx-backend/INSTALL.md)

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

To use the frontend locally, follow the [installation instructions](frontend/INSTALL.md).

To integrate with the umbrella services, run the frontend with `start:auth:localUmbrella`

Afterward, use one of the following users to log into the CX-Operator realm:

| Role       | Username                  | Password                      |
|------------|---------------------------|-------------------------------|
| Supervisor | tracex-supervisor@tx.test | tractusx-umbr3lla!-supervisor |
| Admin      | tracex-admin@tx.test      | tractusx-umbr3lla!-admin      |
| User       | tracex-user@tx.test       | tractusx-umbr3lla!-user       |

### Out of scope

- Sending notifications is currently not supported
- IRS Asset Sync requires a new release of IRS which provides a fix for callback url validation
