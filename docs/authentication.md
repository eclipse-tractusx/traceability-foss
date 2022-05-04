## Authentication

The user authentication is manage by keycloak and each organization has its own realm.

The app supports multi tenancy authentication, so a realm must be specified on the url.

```bash
http://localhost:4200/${REALM}
```

In the app module there is a function which is executed during the app bootstrap. 
That function retrieves the realm provided on the url, which is used to set up the Keycloak configurations.

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

If multi tenancy is disabled, the default realm is considered. 
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

When a user navigates to that url, the realm is mapped with the urls company name.

## API interceptor

The app consumes two different apis and for those apis 
there are two different namespaces with specific request urls.

Which means, the correct endpoints must be provided to the logged user. 

In order to achieve that, angular provides a http interceptor, and we can map those urls (stored as environment variables) with the realm retrieved from the token.

### Parsed mspid from token

````typescript
public getUrl(): { laapi: string; aems: string } {
    const { mspid } = this.getUserData() --> MSPID FROM THE PARSED TOKEN;
    const KeycloakRealm = mspid ? mspid : realm[1];
    return KeycloakRealm.toLocaleLowerCase() === '${NAMESPACE1}'
      ? { laapi: '@${NAMESPACE1}LAAPI', aems: '@${NAMESPACE1}AEMS' }
      : { laapi: '@${NAMESPACE2}LAAPI', aems: '@${NAMESPACE2}AEMS' };
  }
````

### Interceptor

````typescript
export class ApiInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<string>, next: HttpHandler): Observable<HttpEvent<string>> {
    const env = isDevMode() ? dev : stage;
    let requestUrl = req.url;

    if (requestUrl.indexOf('@${NAMESPACE2}LAAPI') !== -1) {
      requestUrl = requestUrl.replace('@${NAMESPACE2}LAAPI', env.${NAMESPACE2}Laapi);
    }

    if (requestUrl.indexOf('@${NAMESPACE2}AEMS') !== -1) {
      requestUrl = requestUrl.replace('@${NAMESPACE2}AEMS', env.${NAMESPACE2}Aems);
    }

    if (requestUrl.indexOf('@${NAMESPACE1}AEMS') !== -1) {
      requestUrl = requestUrl.replace('@${NAMESPACE1}AEMS', env.aems);
    }

    if (requestUrl.indexOf('@${NAMESPACE1}LAAPI') !== -1) {
      requestUrl = requestUrl.replace('${NAMESPACE1}LAAPI', env.laapi);
    }

    req = req.clone({ url: requestUrl });

    return next.handle(req);
  }
}
````
