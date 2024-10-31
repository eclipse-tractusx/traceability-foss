## Authentication

The user authentication is managed by the central keycloak instance managed by catena.
You can configure your IDP there.

[Here is a link on how to do that.](https://github.com/eclipse-tractusx/portal-assets/blob/main/docs/developer/02.%20Technical%20Integration/02.%20Identity%20Provider%20Management/02.%20Configure%20Company%20IdP.md)

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
    realm: environment.realm,
    clientId: environment.clientId,
  },
  initOptions: {
    onLoad: 'login-required',
    checkLoginIframe: false,
  },
});
```

## Local Keycloak

### Prerequisites

- Docker with docker-compose

### How to run local Keycloak?

Keycloak can be started through:

```
yarn env:mock
```

### Keycloak First Configuration

On the first Keycloak start it should be properly configured.

By default, it would be available at http://localhost:8080

To get to the configuration section please click on Administration Console.

Default user/password is `admin`/`admin`.

Now you can start the configuration.

Create a new Realm `mock` and select one.

In `Realm Settings` (from sidebar) -> `Security Defenses`:
Clear `X-Frame-Options`
Set `Content-Security-Policy` to `frame-src 'self'; object-src 'none’;`

In `Clients` (from sidebar)

1. Create a new client `catenax-portal`
2. Edit `catenax-portal`
   1. Set `Valid Redirect URIs` to `*`
   2. `Web Origins` to `*`

In `Roles` (from sidebar):

1. Add next roles:

- `user`
- `admin`
- `supervisor`

In Users (from sidebar):

1. Create user `default-user` with email, first name and last name, then assign to it `user` role for `catenax-portal` client and set a password (disable temp password option)
2. Create user `default-admin` with email, first name and last name, then assign to it `admin` role for `catenax-portal` client and set a password (disable temp password option)
3. Create user `default-supervisor` with email, first name and last name, then assign to it `supervisor` role for `catenax-portal` client and set a password (disable temp password option)
