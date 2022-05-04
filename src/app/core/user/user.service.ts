import { Injectable } from '@angular/core';
import { UserServiceProperties } from './user.service.properties';
import { AuthService } from '../auth/auth.service';
import { Realm } from '../model/realm.model';
import { realm } from '../api/api.service.properties';
import { LastLogin } from 'src/app/shared/model/last-login.model';
import { SharedService } from 'src/app/shared/core/shared.service';

/**
 *
 *
 * @export
 * @class UserService
 */
@Injectable({
  providedIn: 'root',
})
export class UserService {
  private username: string;
  private firstname: string;
  private surname: string;
  private email: string;
  private firstVisit: boolean;
  private mspid: string;
  private roles: string[] = [];
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
    this.mspid = userData.mspid;
    this.firstVisit = !this.hasBeenHere();
    this.dashboardLoaded = false;

    const defaultRoles = ['offline_access', 'uma_authorization', 'user', `${realm[1]}_user`, 'view_only'];
    const { roles } = userData.realm_access;
    this.roles = roles.filter(role => !defaultRoles.includes(role));
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

  public getMspid(): string {
    return this.mspid;
  }

  public getRoles(): string[] {
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
