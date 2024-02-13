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
import { AuthService } from '../auth/auth.service';
import { Role } from './role.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private readonly _username: string;
  private readonly _firstname: string;
  private readonly _surname: string;
  private readonly _email: string;

  private readonly _roles: Role[] = [];

  constructor(authService: AuthService) {
    const { username, firstname, surname, email, roles } = authService.getUserData();
    this._username = username;
    this._firstname = firstname;
    this._surname = surname;
    this._email = email;

    this._roles = roles as Role[];
  }

  public get firstname(): string {
    return this._firstname;
  }

  public get surname(): string {
    return this._surname;
  }

  public get email(): string {
    return this._email;
  }

  public get roles(): Role[] {
    return this._roles;
  }
}
