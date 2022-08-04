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
