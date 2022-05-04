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
        preferred_username: '',
        given_name: '',
        family_name: '',
        email: '',
        mspid: '',
        auth_time: '',
        realm_access: { roles: [] },
      },
    } as any;
  }
}
