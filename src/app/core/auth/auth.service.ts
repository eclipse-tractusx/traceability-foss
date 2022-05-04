import { Injectable } from '@angular/core';
import { KeycloakService } from 'keycloak-angular';
import { environment } from '../../../environments/environment';
import { realm } from '../api/api.service.properties';
import { Token } from '../model/token.model';

export interface UserData {
  username: string;
  firstname: string;
  surname: string;
  email: string;
  mspid: string;
  realm_access: {
    roles: string[];
  };
  auth_time: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private keycloakService: KeycloakService) {
  }

  public getBearerToken(): string {
    return 'Bearer ' + this.keycloakService.getKeycloakInstance().token;
  }

  public getUserData(): UserData {
    const {
      preferred_username: username = '',
      given_name: firstname = '',
      family_name: surname = '',
      email = '',
      mspid = '',
      realm_access = { roles: [] },
    } = this.keycloakService.getKeycloakInstance().tokenParsed;

    const { auth_time: key_auth_time } = this.keycloakService.getKeycloakInstance().tokenParsed;
    const auth_time = key_auth_time.toString();

    return { username, firstname, surname, email, mspid, auth_time, realm_access };
  }

  public logOut(): void {
    this.keycloakService.logout().then();
  }

  public getMspid(): string {
    const { mspid } = this.getUserData();
    return mspid.toLocaleUpperCase();
  }
}
