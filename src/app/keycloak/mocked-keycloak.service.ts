import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import Keycloak, { KeycloakLoginOptions } from 'keycloak-js';

@Injectable()
export class MockedKeycloakService extends KeycloakService {
  constructor() {
    super();
    this['_instance'] = {};
  }

  public init(): Promise<boolean> {
    return Promise.resolve(true);
  }

  public login(options?: KeycloakLoginOptions): Promise<void> {
    return Promise.resolve();
  }

  public logout(redirectUri?: string): Promise<void> {
    return Promise.resolve();
  }

  public getKeycloakInstance(): Keycloak {
    return {
      token: 'MOCK',
      tokenParsed: {
        preferred_username: 'Mock-User',
        given_name: 'Mock',
        family_name: 'User',
        email: 'mock.user@foss.de',
        mspid: 'MOCK',
        auth_time: '99999999',
        realm_access: { roles: ['admin'] },
      },
    } as any;
  }
}
