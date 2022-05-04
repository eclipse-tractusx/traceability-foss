/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { HttpClient, HttpHandler } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { KeycloakService } from 'keycloak-angular';
import { AuthService } from '../auth/auth.service';

import { UserService } from '../user/user.service';

describe('UserService', () => {
  const userData = {
    getUserData() {
      return {
        username: 'Lion',
        firstname: 'Lion',
        surname: 'Lion',
        email: 'lion@email.com',
        mspid: 'Lion',
        realm_access: { roles: ['admin'] },
      };
    },

    getUrl() {
      return;
    },

    getMspid() {
      return;
    },
  };
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: AuthService, useValue: userData }, HttpClient, HttpHandler],
    }),
  );

  it('should be created', () => {
    const service: UserService = TestBed.get(UserService);
    expect(service).toBeTruthy();
  });
});
