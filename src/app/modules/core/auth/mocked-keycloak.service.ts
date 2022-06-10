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

import { Inject, Injectable, Optional } from '@angular/core';
import { Role } from '@core/user/role';
import { KeycloakService } from 'keycloak-angular';
import Keycloak, { KeycloakLoginOptions } from 'keycloak-js';

@Injectable()
export class MockedKeycloakService extends KeycloakService {
  constructor(@Optional() @Inject('mockedRoles') private mockedRoles: Role[]) {
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
        auth_time: '99999999',
        realm_access: { roles: this.mockedRoles ?? ['admin'] },
      },
    } as any;
  }
}
