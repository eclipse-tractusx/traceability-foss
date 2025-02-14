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
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { KeycloakAuthGuard, KeycloakService } from 'keycloak-angular';

@Injectable()
export class AuthGuard extends KeycloakAuthGuard {
  constructor(protected router: Router, protected keycloakService: KeycloakService) {
    super(router, keycloakService);
    console.log('keycloakService check:', this.keycloakService.isLoggedIn());
    debugger; // This will pause execution in DevTools
  }

  public async isAccessAllowed(route: ActivatedRouteSnapshot): Promise<boolean> {
    console.log('authenticated first check:', this.authenticated);
    debugger; // This will pause execution in DevTools
    if (!this.authenticated) {
      console.log('authenticated second check:', this.authenticated);
      //await this.keycloakService.login().then();
    }

    // Get the roles required from the route.
    const requiredRoles = route.data.roles;
    console.log('required roles', requiredRoles);

    // Allow the user to proceed if no additional roles are required to access the route.
    if (!(requiredRoles instanceof Array) || requiredRoles.length === 0) {
      return true;
    }

    // Allow the user to proceed if all the required roles are present.
    return requiredRoles.every(role => this.roles.includes(role));
  }
}
