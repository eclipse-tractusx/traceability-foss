# Eclipse-TractusX-Traceability-FOSS Backend Helm Chart

This Helm chart provides a way to deploy the backend component of Eclipse-TractusX-Traceability-FOSS on Kubernetes.
The chart includes the necessary configuration files, resources, and dependencies to deploy the backend component in an kubernetes environment.

## Requirements

To use this Helm chart, you must have the following:

- Kubernetes cluster version 1.16+
- Helm version 3+

## Installation

To install the Helm chart, use the following command:
`helm install backend .`

This will deploy the backend component to your Kubernetes cluster.

## Configuration

The Helm chart provides the following configuration options:

- `replicaCount`: The number of replicas of the backend pod to deploy. The default value is `1`.
- `image.repository`: The Docker image repository to use for the backend. The default value is `eclipse-tractusx-traceability-foss-backend`.
- `image.tag`: The Docker image tag to use for the backend. The default value is `latest`.
- `imagePullSecrets`: The name of the secret to use for pulling Docker images. The default value is `nil`.
- `service.type`: The type of Kubernetes service to create. The default value is `ClusterIP`.
- `service.port`: The port number to use for the Kubernetes service. The default value is `80`.
- `ingress.enabled`: Whether to create a Kubernetes ingress for the backend. The default value is `false`.
- `ingress.annotations`: Annotations to use for the Kubernetes ingress. The default value is an empty map.
- `ingress.hosts`: A list of hosts to use for the Kubernetes ingress. The default value is an empty list.

You can customize the configuration options by creating a `values.yaml` file and specifying your desired values.

## License
This project is licensed under the Apache License, Version 2.0. See the `LICENSE` file for more information.
