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
import { SharedService } from '../service/shared.service';

@Injectable({
  providedIn: 'root',
})
export class LayoutFacade {
  constructor(
    private layoutState: LayoutState,
    private userService: UserService,
    private authService: AuthService,
    private sharedService: SharedService,
  ) {}

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

  get tabIndexSnapshot(): number {
    return this.layoutState.getTabIndexSnapshot;
  }

  get queuedQualityAlerts$(): Observable<number> {
    return this.layoutState.getQueuedQualityAlerts$;
  }

  get queuedQualityInvestigations$(): Observable<number> {
    return this.layoutState.getQueuedQualityInvestigationsCounter$;
  }

  get receivedQualityAlerts$(): Observable<number> {
    return this.layoutState.getReceivedQualityAlertsCounter$;
  }

  get receivedQualityInvestigations$(): Observable<number> {
    return this.layoutState.getQualityInvestigationBadge$;
  }

  get isSideBarExpanded$(): Observable<boolean> {
    return this.layoutState.getIsSideBarExpanded$;
  }

  get isFooterDisplayed$(): Observable<boolean> {
    return this.layoutState.getIsFooterDisplayed$;
  }

  get organizations$(): Observable<string[]> {
    return this.layoutState.getOrganizations$;
  }

  get organizationsSnapshot(): string[] {
    return this.layoutState.organizationsSnapshot;
  }

  public logOut(): void {
    this.authService.logOut();
  }

  public isEmpty(object: unknown): boolean {
    return this.sharedService.isEmpty(object);
  }

  public setOrganizations(): void {
    this.sharedService.getAllOrganizations().subscribe((organizations: string[]) => {
      this.layoutState.setOrganizations(organizations);
    });
  }

  public setTabIndex(index: number): void {
    this.layoutState.setTabIndex(index);
  }

  public addQueuedQualityInvestigations(investigations: number): void {
    this.layoutState.addQueuedQualityInvestigations(investigations);
  }

  public setQueuedQualityInvestigations(investigations: number): void {
    this.layoutState.setQueuedQualityInvestigations(investigations);
  }

  public resetQueuedQualityInvestigations(): void {
    this.layoutState.resetQualityInvestigationsCounter();
  }

  public addQueuedQualityAlerts(alerts: number): void {
    this.layoutState.addQueuedQualityAlerts(alerts);
  }

  public setQueuedQualityAlerts(alerts: number): void {
    this.layoutState.setQueuedQualityAlerts(alerts);
  }

  public resetQueuedQualityAlerts(): void {
    this.layoutState.resetQueuedQualityAlerts();
  }

  public addReceivedQualityAlerts(alerts: number): void {
    this.layoutState.addReceivedQualityAlerts(alerts);
  }

  public setReceivedQualityAlerts(alerts: number): void {
    this.layoutState.setReceivedQualityAlerts(alerts);
  }

  public resetReceivedQualityAlerts(): void {
    this.layoutState.resetReceivedQualityAlerts();
  }

  public addReceivedQualityInvestigations(alerts: number): void {
    this.layoutState.addQualityInvestigationToBadge(alerts);
  }

  public setReceivedQualityInvestigationsCounter(alerts: number): void {
    this.layoutState.setQualityInvestigationBadge(alerts);
  }

  public resetReceivedQualityInvestigations(): void {
    this.layoutState.resetQualityInvestigationBadge();
  }

  public setIsSideBarExpanded(isExpanded: boolean): void {
    this.layoutState.setIsSideBarExpanded(isExpanded);
  }

  public setIsFooterDisplayed(isDisplayed: boolean): void {
    this.layoutState.setIsFooterDisplayed(isDisplayed);
  }

  public setCanDeactivate(canDeactivate: boolean): void {
    this.layoutState.setCanDeactivate(canDeactivate);
  }
}
