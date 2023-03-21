/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

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
  bpn: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private readonly keycloakService: KeycloakService) {}

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
      auth_time: key_auth_time,
      bpn = '',
    } = this.keycloakService.getKeycloakInstance().tokenParsed;

    const auth_time = key_auth_time.toString();
    const roles = resource_access[environment.clientId]?.roles ?? [];

    return { username, firstname, surname, email, auth_time, roles, bpn };
  }

  public logOut(): void {
    void this.keycloakService.logout();
  }
}
