/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import { Injectable } from '@angular/core';
import { environment } from '@env';
import { KeycloakService } from 'keycloak-angular';

export interface UserData {
  username: string;
  firstname: string;
  surname: string;
  email: string;
  roles: string[];
  auth_time: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private keycloakService: KeycloakService) {}

  public getBearerToken(): string {
    return 'Bearer ' + this.keycloakService.getKeycloakInstance().token;
  }

  public getUserData(): UserData {
    const {
      preferred_username: username = '',
      given_name: firstname = '',
      family_name: surname = '',
      email = '',
      resource_access = {},
    } = this.keycloakService.getKeycloakInstance().tokenParsed;

    const { auth_time: key_auth_time } = this.keycloakService.getKeycloakInstance().tokenParsed;
    const auth_time = key_auth_time.toString();
    const roles = resource_access[environment.clientId]?.roles ?? [];

    return { username, firstname, surname, email, auth_time, roles };
  }

  public logOut(): void {
    this.keycloakService.logout().then();
  }
}
