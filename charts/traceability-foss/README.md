# Eclipse-TractusX-Traceability-FOSS Umbrella Helm Chart

This Helm chart provides a way to deploy the Umbrella component of Eclipse-TractusX-Traceability-FOSS on Kubernetes.
The chart includes the necessary configuration files, resources, and dependencies to deploy the Umbrella component in a kubernetes environment.

## Prerequisites

To use this Helm chart, you must have the following:

- Kubernetes cluster version 1.16+
- Helm version 3+

## TL;DR

To install the Helm chart, use the following command:
`helm install traceability-foss .`

This will deploy the Umbrella component to your Kubernetes cluster.

## Configuration

The Helm chart provides the following configuration options:

- `frontend.*`: The prefix for the frontend subchart configuration. Defaults to the subchart values.yaml configuration`.
- `backend.*`: The prefix for the backend subchart configuration. Defaults to the subchart values.yaml configuration.
- `postgresql.*`: The prefix for the postgresql of the backend subchart configuration. Defaults to the subchart (backend) values.yaml configuration
- `irs-edc-consumer`: The prefix for the irs-ed-consumer of the backend subchart configuration. Defaults to the subchart (backend) values.yaml configuration
- `pgadmin4.*`: The prefix for the pgadmin4 of the backend subchart configuration. Defaults to the subchart (backend) values.yaml configuration
- `irs-helm.*`: The prefix for the irs-helm of the backend subchart configuration. Defaults to the subchart (backend) values.yaml configuration

You can customize the configuration options by creating a `values.yaml` file and specifying your desired values.

## License
This project is licensed under the Apache License, Version 2.0. See the `LICENSE` file for more information.
