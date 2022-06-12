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
import { Observable, Subject } from 'rxjs';
import { State } from '../model/state';

@Injectable({
  providedIn: 'root',
})
export class LayoutState {
  private readonly qualityInvestigationBadge$: State<number> = new State<number>(0);
  private readonly canDeactivate$: Subject<boolean> = new Subject<boolean>();
  private readonly tabIndex$: State<number> = new State<number>(-1);
  private readonly queuedQualityAlerts$: State<number> = new State<number>(0);
  private readonly receivedQualityAlertsCounter$: State<number> = new State<number>(0);
  private readonly queuedQualityInvestigationsCounter$: State<number> = new State<number>(0);
  private readonly isSideBarExpanded$: State<boolean> = new State<boolean>(false);
  private readonly isFooterDisplayed$: State<boolean> = new State<boolean>(false);
  private readonly organizations$: State<string[]> = new State<string[]>([]);

  private breadcrumbLabel: string;

  get getQualityInvestigationBadge$(): Observable<number> {
    return this.qualityInvestigationBadge$.observable;
  }

  get getCanDeactivate(): Subject<boolean> {
    return this.canDeactivate$;
  }

  get getBreadCrumbLabel(): string {
    return this.breadcrumbLabel;
  }

  get getTabIndex$(): Observable<number> {
    return this.tabIndex$.observable;
  }

  get getTabIndexSnapshot(): number {
    return this.tabIndex$.snapshot;
  }

  get getQueuedQualityAlerts$(): Observable<number> {
    return this.queuedQualityAlerts$.observable;
  }

  get getReceivedQualityAlertsCounter$(): Observable<number> {
    return this.receivedQualityAlertsCounter$.observable;
  }

  get getQueuedQualityInvestigationsCounter$(): Observable<number> {
    return this.queuedQualityInvestigationsCounter$.observable;
  }

  get getIsSideBarExpanded$(): Observable<boolean> {
    return this.isSideBarExpanded$.observable;
  }

  get getIsFooterDisplayed$(): Observable<boolean> {
    return this.isFooterDisplayed$.observable;
  }

  get getOrganizations$(): Observable<string[]> {
    return this.organizations$.observable;
  }

  get organizationsSnapshot(): string[] {
    return this.organizations$.snapshot;
  }

  public setQualityInvestigationBadge(transactionsCounter: number): void {
    this.resetQualityInvestigationBadge();
    this.qualityInvestigationBadge$.update(transactionsCounter);
  }

  public addQualityInvestigationToBadge(transactionsCounter: number): void {
    const currentBadgeNumber: number = this.qualityInvestigationBadge$.snapshot;
    this.qualityInvestigationBadge$.update(currentBadgeNumber + transactionsCounter);
  }

  public resetQualityInvestigationBadge(): void {
    this.qualityInvestigationBadge$.reset();
  }

  public setCanDeactivate(canDeactivate: boolean): void {
    this.canDeactivate$.next(canDeactivate);
  }

  public setBreadCrumbLabel(label: string): void {
    this.breadcrumbLabel = label;
  }

  public setTabIndex(index: number): void {
    this.tabIndex$.update(index);
  }

  public setQueuedQualityAlerts(alerts: number): void {
    this.resetQueuedQualityAlerts();
    this.queuedQualityAlerts$.update(alerts);
  }

  public setQueuedQualityInvestigations(investigations: number): void {
    this.resetQualityInvestigationsCounter();
    this.queuedQualityInvestigationsCounter$.update(investigations);
  }

  public setOrganizations(organizations: string[]): void {
    this.organizations$.update(organizations);
  }

  public addQueuedQualityAlerts(alerts: number): void {
    const currentAlerts: number = this.queuedQualityAlerts$.snapshot;
    this.queuedQualityAlerts$.update(currentAlerts + alerts);
  }

  public addQueuedQualityInvestigations(investigations: number): void {
    const currentInvestigations: number = this.queuedQualityInvestigationsCounter$.snapshot;
    this.queuedQualityInvestigationsCounter$.update(currentInvestigations + investigations);
  }

  public resetQueuedQualityAlerts(): void {
    this.queuedQualityAlerts$.reset();
  }

  public resetQualityInvestigationsCounter(): void {
    this.queuedQualityInvestigationsCounter$.reset();
  }

  public setReceivedQualityAlerts(alerts: number): void {
    this.resetReceivedQualityAlerts();
    this.receivedQualityAlertsCounter$.update(alerts);
  }

  public addReceivedQualityAlerts(alerts: number): void {
    const currentAlerts: number = this.receivedQualityAlertsCounter$.snapshot;
    this.receivedQualityAlertsCounter$.update(currentAlerts + alerts);
  }

  public resetReceivedQualityAlerts(): void {
    this.receivedQualityAlertsCounter$.reset();
  }

  public setIsSideBarExpanded(isSideBarExpanded: boolean): void {
    this.isSideBarExpanded$.update(isSideBarExpanded);
  }

  public setIsFooterDisplayed(isDisplayed: boolean): void {
    this.isFooterDisplayed$.update(isDisplayed);
  }
}
