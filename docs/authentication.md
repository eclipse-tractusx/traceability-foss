## Authentication

The user authentication is managed by keycloak and each organization has its own realm.

The app supports multi tenancy-authentication, so a realm must be specified on the URL.

```bash
http://localhost:4200/${REALM}
```

In the app module, there is a function which is executed during the app bootstrap. 
That function retrieves the realm provided on the URL, which is used to set up the Keycloak configurations.

### App module provider

````typescript
{
  provide: APP_INITIALIZER,
  useFactory: keycloakInit,
  multi: true,
  deps: [KeycloakService],
}
````

### Keycloak init configurations (folder: /utils)

````typescript
 keycloak.init({
      config: {
        url: environment.keycloakUrl,
        realm,
        clientId: 'ui',
      },
      initOptions: {
        onLoad: 'login-required',
        checkLoginIframe: false,
      },
    });
````

If multi-tenancy is disabled, the default realm is considered. 
Those configurations are retrieved from the environment variables.

````typescript
export const environment = {
  multiTenant: true,
  defaultRealm: '${REALM}',
  ...
};
````

> **Note:** For a deployed app those realms are already configured in the platform chart configurations:

````yaml
ui:
  fullnameOverride: ui
  image: ${IMAGE}
  hosts:
    ${REALM}: ui.${COMPANY-X}.test.catenax.partchain.dev
  nodeSelector:
    agentpool: application
````

When a user navigates to that URL, the realm is mapped with the URL's company name.
