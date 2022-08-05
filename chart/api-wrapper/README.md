# API-Wrapper Helm Chart

## Important configurations

In addition to the "normal" Helm/Kubernetes/EDC configurations, you must specify the following values:

| Value                                     | Description                                                                                                          |
|-------------------------------------------|----------------------------------------------------------------------------------------------------------------------|
| `wrapper.consumer.edc.datamanagement.url` | Url to the data management api of the connector (incl. base path).                                                   |
| `wrapper.consumer.edc.apikey.name`        | (Optional) If the connectors data management api is secured with token based auth, this defines the header key name. |
| `wrapper.consumer.edc.apikey.value`       | (Optional) If the connectors data management api is secured with token based auth, this defines the token value.     |

## Authentication

If you want to secure the api of the api-wrapper you can do this via basic auth. To register new credentials you have to
add the following configuration for each new credential pair:

```properties
wrapper.auth.basic.<USERNAME>=<PASSWORD>
```
