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
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from '@core/auth/auth.service';
import { Realm } from '@core/model/realm.model';
import { UserService } from '@core/user/user.service';
import { LayoutState } from '../service/layout.state';

@Injectable({
  providedIn: 'root',
})
export class LayoutFacade {
  constructor(private layoutState: LayoutState, private userService: UserService, private authService: AuthService) {}

  // ToDo: Improve getter and setter (remove get/ set from name)
  get getUserInformation(): { name: string; email: string; role: string } {
    return {
      name: `${this.userService.getFirstname()} ${this.userService.getSurname()}`,
      email: `${this.userService.getEmail()}`,
      role: `${this.userService.getRoles().join(', ')}`,
    };
  }

  get getOrgPreferences(): Realm {
    return this.userService.getOrgPreferences();
  }

  get realmName(): string {
    return this.userService.getFirstname();
  }

  get breadcrumbLabel(): string {
    return this.layoutState.getBreadCrumbLabel;
  }

  get tabIndex$(): Observable<number> {
    return this.layoutState.getTabIndex$;
  }

  get receivedQualityAlerts$(): Observable<number> {
    return this.layoutState.getReceivedQualityAlertsCounter$;
  }

  get receivedQualityInvestigations$(): Observable<number> {
    return this.layoutState.getQualityInvestigationBadge$;
  }

  public logOut(): void {
    this.authService.logOut();
  }

  public setTabIndex(index: number): void {
    this.layoutState.setTabIndex(index);
  }

  public setIsSideBarExpanded(isExpanded: boolean): void {
    this.layoutState.setIsSideBarExpanded(isExpanded);
  }
}
