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
import { LastLogin } from 'src/app/modules/shared/model/last-login.model';
import { AuthService } from '../auth/auth.service';
import { Realm } from '../model/realm.model';
import { UserServiceProperties } from './user.service.properties';
import { Role } from './role';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private username: string;
  private firstname: string;
  private surname: string;
  private email: string;
  private firstVisit: boolean;

  private roles: Role[] = [];
  private dashboardLoaded: boolean;

  constructor(private authService: AuthService) {
    this.setUserDetails();
  }

  public setUserDetails(): void {
    const userData = this.authService.getUserData();
    this.username = userData.username;
    this.firstname = userData.firstname;
    this.surname = userData.surname;
    this.email = userData.email;
    this.firstVisit = !this.hasBeenHere();
    this.dashboardLoaded = false;

    this.roles = userData.roles as Role[];
    if (userData.auth_time) {
      this.setLastLogin(userData.auth_time);
    }
  }

  public getFirstname(): string {
    return this.firstname;
  }

  public getSurname(): string {
    return this.surname;
  }

  public getEmail(): string {
    return this.email;
  }

  public getRoles(): Role[] {
    return this.roles;
  }

  public getFirstVisit(): boolean {
    return this.firstVisit;
  }

  public getDashboardLoaded(): boolean {
    return this.dashboardLoaded;
  }

  public setDashboardLoaded(): void {
    this.dashboardLoaded = true;
  }

  public getOrgPreferences(): Realm {
    const user: Realm = {} as Realm;
    user.assetsTile = 'My parts';
    user.componentsTile = 'Other parts';
    return user;
  }

  private hasBeenHere(): boolean {
    const hasBeenHere = Boolean(localStorage.getItem(UserServiceProperties.localStorageKey + this.username));

    if (!hasBeenHere) {
      localStorage.setItem(UserServiceProperties.localStorageKey + this.username, 'true');
    }

    return !!hasBeenHere;
  }

  private setLastLogin(authTime: string): void {
    const storedLastLogin = localStorage.getItem('lastLogin');
    if (storedLastLogin) {
      const lastLogin: LastLogin = JSON.parse(storedLastLogin);
      if (lastLogin.currentDate !== authTime) {
        const refreshedLastLogin: LastLogin = {
          previousDate: lastLogin.currentDate,
          currentDate: authTime,
        };
        this.setLocalStorage(refreshedLastLogin);
      }
    } else {
      const login: LastLogin = {
        previousDate: '',
        currentDate: authTime,
      };
      this.setLocalStorage(login);
    }
  }

  private setLocalStorage(lastLogin: LastLogin): void {
    localStorage.setItem('lastLogin', JSON.stringify(lastLogin));
  }
}
