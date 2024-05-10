# Environment variables

Support environment variables are:

```javascript
const ENV_VARS_MAPPING = {
  CATENAX_PORTAL_KEYCLOAK_URL: 'keycloakUrl',
  CATENAX_PORTAL_CLIENT_ID: 'clientId',
  CATENAX_PORTAL_REALM: 'realm',
  CATENAX_PORTAL_API_URL: 'apiUrl',
  CATENAX_PORTAL_BASE_URL: 'baseUrl',
  CATENAX_PORTAL_BACKEND_DOMAIN,
  CATENAX_PORTAL_URL: 'portalUrl',
  BPN: 'bpn'
};
```

`CATENAX_PORTAL_KEYCLOAK_URL`
This variable is used to set up and use keycloak

`CATENAX_PORTAL_CLIENT_ID`
This variable is used to identify the client on keycloak

`CATENAX_PORTAL_REALM`
This variable is used to define the realm for keycloak. Default realm for the portal is "CX-Central".

`CATENAX_PORTAL_API_URL`
This variable points to the desired api

`CATENAX_PORTAL_BASE_URL`
This variable is used to set the base path of the application. (Should be set if application runs as a subtopic)

`CATENAX_PORTAL_BACKEND_DOMAIN`
This variable is needed for security, to be more explicit, for the security headers of a request.
The domain of the corresponding backend should be used here.
An example value could be: `catena-x.net`

`CATENAX_PORTAL_URL`
This variable is optional and points to the CX Portal instance

`BPN`
This variable is the bpn of the application owner

# Helm deployment

## Configuration of values.yaml

To run a helm chart you first need to specify a values file with instructions on how to run your helm file.
Here is an example how you could structure this file for this frontend helm chart.

`your-values.yaml`

```yaml
image:
  tag: $APP_REVISION
  ENVIRONMENT_VAR_1: 'VALUE'
  ENVIRONMENT_VAR_2: 'VALUE'
  ...

ingress:
  enabled: true
  className: "nginx"
  annotations:
    cert-manager.io/cluster-issuer: letsencrypt-prod
  hosts:
    - host: "${FE_HOST_URL}"
    paths:
        - path: /
        pathType: Prefix
  tls:
    - hosts:
        - "${FE_HOST_URL}"
    secretName: "${FE_HOST_URL}-tls"

```

## Helm installation

Add the Trace-X frontend Helm repository:

```sh
$ helm repo add traceability-foss-frontend https://github.com/eclipse-tractusx/traceability-foss
```

Then install the Helm chart into your cluster:

```sh
$ helm install -f your-values.yaml traceability-foss-frontend traceability-foss-frontend/traceability-foss-frontend
```

### Deployment using ArgoCD

Create a new Helm chart and use Trace-X as a dependency.

```yaml
dependencies:
  - name: traceability-foss-frontend
    alias: frontend
    version: x.x.x
    repository: 'https://github.com/eclipse-tractusx/traceability-foss/'
```

Then provide your configuration as the values.yaml of that chart.

Create a new application in ArgoCD and point it to your repository / Helm chart folder.

# Docker deployment

## How to build the application

The angular app is built into Docker containers and exposed through NGINX

It is important to specify the type of configuration profile, previously defined in the angular JSON file, so it can
build the application correctly.

## How to run the docker image

When running the build docker image you are able to pass through multiple environment variables to configure the FE.

### Example command:

```shell
$ docker run -d -p 4200:8080 -e CATENAX_PORTAL_BASE_URL=/example -e CATENAX_PORTAL_BACKEND_DOMAIN=catena-x.net ${dockerImage}
```

#### `Docker run`

With the `docker run [OPTIONS]` an operator can add to or override the image defaults set by a developer. And, additionally, operators can override nearly all the defaults set by the Docker runtime itself. The operator’s ability to override image and Docker runtime defaults is why run has more options than any other `docker` command.

#### `-d`

To start a container in detached mode, you use `-d=true` or just `-d` option. By design, containers started in detached mode exit when the root process used to run the container exits

#### `-p 4200:8080`

To expose a container’s internal port, an operator can start the container with the `-P` or `-p` flag. The exposed port is accessible on the host and the ports are available to any client that can reach the host.

#### `-e ENV_VAR=VAR_VALUE`

The operator can set any environment variable in the container by using one or more `-e` flags, even overriding already defined flags by the developer with a Dockerfile `ENV`.

#### `${dockerImage}`

While not strictly a means of identifying a container, you can specify a version of an image you’d like to run the container with by adding `image[:tag]` to the command. For example, `docker run 002b3c518f5852f7fd5e9e46c0ea1ab4d76b697d33d5022af1a69e1a909645ea`.
Images can be pulled [here](https://github.com/eclipse-tractusx/traceability-foss-frontend/pkgs/container/traceability-foss-frontend).
