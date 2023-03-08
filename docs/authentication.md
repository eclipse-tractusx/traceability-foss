## Authentication

The user authentication is managed by the central keycloak instance managed by catena.
You can configure your IDP there.

[Here is a link on how to do that.](https://github.com/catenax-ng/tx-portal-assets/blob/main/docs/02.%20Technical%20Integration/02.%20Identity%20Provider%20Management/02.%20Configure%20Company%20IdP.md)

```bash
http://localhost:4200/
```

In the app module, there is a function which is executed during the app bootstrap.
That function retrieves the realm provided in the environment variables,
which is used to set up the Keycloak configurations.

### App module provider

```typescript
{
  provide: APP_INITIALIZER,
  useFactory: keycloakInit,
  multi: true,
  deps: [KeycloakService],
}
```

### Keycloak init configurations (folder: /utils)

```typescript
keycloak.init({
  config: {
    url: environment.keycloakUrl,
    realm: environment.defaultRealm,
    clientId: environment.clientId,
  },
  initOptions: {
    onLoad: 'login-required',
    checkLoginIframe: false,
  },
});
```
