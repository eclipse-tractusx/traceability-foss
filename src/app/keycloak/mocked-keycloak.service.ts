import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { Keycloak } from 'keycloak-angular/lib/core/services/keycloak.service';

@Injectable()
export class MockedKeycloakService extends KeycloakService {
  private _instance = {};

  public init(): Promise<boolean> {
    return Promise.resolve(true);
  }

  public login(options?: Keycloak.KeycloakLoginOptions): Promise<void> {
    return Promise.resolve();
  }

  public logout(redirectUri?: string): Promise<void> {
    return Promise.resolve();
  }

  public getKeycloakInstance(): Keycloak.KeycloakInstance {
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
