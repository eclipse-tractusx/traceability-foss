import { KeycloakService } from 'keycloak-angular';
import { environment } from '../../environments/environment';

export function keycloakInit(keycloak: KeycloakService): () => Promise<boolean | void> {
  // Set default realm
  let realm = environment.defaultRealm;

  // Check multi-tenant
  if (environment.multiTenant) {
    const matches: RegExpExecArray = new RegExp(environment.realmRegExp).exec(window.location.href);
    if (matches) {
      realm = matches[1];

      // Update the <base> href attribute
      document
        .getElementsByTagName('base')
        .item(0)
        .attributes.getNamedItem('href').value = environment.baseUrl + realm + '/';
    } else {
      // Redirect user to the default realm page.
      window.location.href = document
        .getElementsByTagName('base')
        .item(0)
        .attributes.getNamedItem('href').value = environment.baseUrl + environment.defaultRealm + '/';

      return (): Promise<void> => Promise.resolve();
    }
  }
  return (): Promise<boolean> =>
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
}
