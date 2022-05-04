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

import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { Mspid } from '../model/mspid.model';
import { State } from '../model/state';

/**
 *
 *
 * @export
 * @class LayoutState
 */
@Injectable({
  providedIn: 'root',
})
export class LayoutState {
  /**
   * Transactions badge state
   *
   * @private
   * @readonly
   * @type {State<number>}
   * @memberof LayoutState
   */
  private readonly qualityInvestigationBadge$: State<number> = new State<number>(0);

  /**
   * MspIds state
   *
   * @private
   * @readonly
   * @type {State<Realm[]>}
   * @memberof AssetState
   */
  private readonly mspidState$: State<Mspid[]> = new State<Mspid[]>([]);

  /**
   * Can deactivate state
   *
   * @private
   * @readonly
   * @type {Subject<boolean>}
   * @memberof LayoutState
   */
  private readonly canDeactivate$: Subject<boolean> = new Subject<boolean>();

  /**
   * Breadcrumb custom label
   *
   * @private
   * @readonly
   * @type {string}
   * @memberof LayoutState
   */
  private breadcrumbLabel: string;

  /**
   * Tab index state
   *
   * @private
   * @readonly
   * @type {State<number>}
   * @memberof LayoutState
   */
  private readonly tabIndex$: State<number> = new State<number>(-1);

  /**
   * Queued quality alert counter state
   *
   * @private
   * @readonly
   * @type {State<number>}
   * @memberof LayoutState
   */
  private readonly queuedQualityAlerts$: State<number> = new State<number>(0);

  /**
   * Received quality alert counter state
   *
   * @private
   * @readonly
   * @type {State<number>}
   * @memberof LayoutState
   */
  private readonly receivedQualityAlertsCounter$: State<number> = new State<number>(0);

  /**
   * Queued quality investigations counter state
   *
   * @private
   * @type {State<number>}
   * @memberof LayoutState
   */
  private readonly queuedQualityInvestigationsCounter$: State<number> = new State<number>(0);

  /**
   * Is sidebar expanded state
   *
   * @private
   * @readonly
   * @type {State<boolean>}
   * @memberof LayoutState
   */
  private readonly isSideBarExpanded$: State<boolean> = new State<boolean>(false);

  /**
   * Is footer displayed
   *
   * @private
   * @readonly
   * @type {State<boolean>}
   * @memberof LayoutState
   */
  private readonly isFooterDisplayed$: State<boolean> = new State<boolean>(false);

  /**
   * Organizations state
   *
   * @private
   * @readonly
   * @type {State<string[]>}
   * @memberof LayoutState
   */
  private readonly organizations$: State<string[]> = new State<string[]>([]);

  /**
   * Transactions badge state getter
   *
   * @readonly
   * @type {Observable<number>}
   * @memberof LayoutState
   */
  get getQualityInvestigationBadge$(): Observable<number> {
    return this.qualityInvestigationBadge$.observable;
  }

  /**
   * Mspids state getter
   *
   * @readonly
   * @type {Observable<Realm[]>}
   * @memberof AssetState
   */
  get getMspids$(): Observable<Mspid[]> {
    return this.mspidState$.observable;
  }

  /**
   * Mspids state snapshot
   *
   * @readonly
   * @type {Mspid[]}
   * @memberof LayoutState
   */
  get mspIdsSnapshot(): Mspid[] {
    return this.mspidState$.snapshot;
  }

  /**
   * Can deactivate state getter
   *
   * @readonly
   * @type {Subject<boolean>}
   * @memberof LayoutState
   */
  get getCanDeactivate(): Subject<boolean> {
    return this.canDeactivate$;
  }

  /**
   * Custom breadcrumb getter
   *
   * @readonly
   * @type {string}
   * @memberof LayoutState
   */
  get getBreadCrumbLabel(): string {
    return this.breadcrumbLabel;
  }

  /**
   * Tab index state getter
   *
   * @readonly
   * @type {Observable<number>}
   * @memberof LayoutState
   */
  get getTabIndex$(): Observable<number> {
    return this.tabIndex$.observable;
  }

  /**
   * Tab index snapshot
   *
   * @readonly
   * @type {number}
   * @memberof LayoutState
   */
  get getTabIndexSnapshot(): number {
    return this.tabIndex$.snapshot;
  }

  /**
   * Queued quality alert counter state getter
   *
   * @readonly
   * @type {Observable<number>}
   * @memberof LayoutState
   */
  get getQueuedQualityAlerts$(): Observable<number> {
    return this.queuedQualityAlerts$.observable;
  }

  /**
   * Queued quality alert counter state getter
   *
   * @readonly
   * @type {Observable<number>}
   * @memberof LayoutState
   */
  get getReceivedQualityAlertsCounter$(): Observable<number> {
    return this.receivedQualityAlertsCounter$.observable;
  }

  /**
   * Queued quality investigations counter state getter
   *
   * @readonly
   * @type {Observable<number>}
   * @memberof LayoutState
   */
  get getQueuedQualityInvestigationsCounter$(): Observable<number> {
    return this.queuedQualityInvestigationsCounter$.observable;
  }

  /**
   * Sidebar expanded state
   *
   * @readonly
   * @type {Observable<boolean>}
   * @memberof LayoutState
   */
  get getIsSideBarExpanded$(): Observable<boolean> {
    return this.isSideBarExpanded$.observable;
  }

  /**
   * Is footer displayed getter
   *
   * @readonly
   * @type {Observable<boolean>}
   * @memberof LayoutState
   */
  get getIsFooterDisplayed$(): Observable<boolean> {
    return this.isFooterDisplayed$.observable;
  }

  /**
   * Organizations getter
   *
   * @readonly
   * @type {Observable<string[]>}
   * @memberof LayoutState
   */
  get getOrganizations$(): Observable<string[]> {
    return this.organizations$.observable;
  }

  /**
   * Organizations snapshot
   *
   * @readonly
   * @type {string[]}
   * @memberof LayoutState
   */
  get organizationsSnapshot(): string[] {
    return this.organizations$.snapshot;
  }

  /**
   * Transactions badge state setter
   *
   * @param {number} transactionsCounter
   * @return {void}
   * @memberof LayoutState
   */
  public setQualityInvestigationBadge(transactionsCounter: number): void {
    this.resetQualityInvestigationBadge();
    this.qualityInvestigationBadge$.update(transactionsCounter);
  }

  /**
   * Mspids state setter
   *
   * @param {Realm[]} mspids
   * @return {void}
   * @memberof AssetState
   */
  public setMspids(mspids: Mspid[]): void {
    this.mspidState$.update(mspids);
  }

  /**
   * Add transactions to state
   *
   * @param {number} transactionsCounter
   * @return {void}
   * @memberof LayoutState
   */
  public addQualityInvestigationToBadge(transactionsCounter: number): void {
    const currentBadgeNumber: number = this.qualityInvestigationBadge$.snapshot;
    this.qualityInvestigationBadge$.update(currentBadgeNumber + transactionsCounter);
  }

  /**
   * Reset transactions badge state
   *
   * @return {void}
   * @memberof LayoutState
   */
  public resetQualityInvestigationBadge(): void {
    this.qualityInvestigationBadge$.reset();
  }

  /**
   * Can deactivate state setter
   *
   * @param {boolean} canDeactivate
   * @return {void}
   * @memberof LayoutState
   */
  public setCanDeactivate(canDeactivate: boolean): void {
    this.canDeactivate$.next(canDeactivate);
  }

  /**
   * Custom breadcrumb setter
   *
   * @param {string} label
   * @return {void}
   * @memberof LayoutState
   */
  public setBreadCrumbLabel(label: string): void {
    this.breadcrumbLabel = label;
  }

  /**
   * Tab index state setter
   *
   * @param {number} index
   * @return {void}
   * @memberof LayoutState
   */
  public setTabIndex(index: number): void {
    this.tabIndex$.update(index);
  }

  /**
   * Counter of queued quality alerts state setter
   *
   * @param {number} alerts
   * @return {void}
   * @memberof LayoutState
   */
  public setQueuedQualityAlerts(alerts: number): void {
    this.resetQueuedQualityAlerts();
    this.queuedQualityAlerts$.update(alerts);
  }

  /**
   * Counter of queued quality investigations state setter
   *
   * @param {number} investigations
   * @return {void}
   * @memberof LayoutState
   */
  public setQueuedQualityInvestigations(investigations: number): void {
    this.resetQualityInvestigationsCounter();
    this.queuedQualityInvestigationsCounter$.update(investigations);
  }

  /**
   * Organizations setter
   *
   * @param {string[]} organizations
   * @return {void}
   * @memberof LayoutState
   */
  public setOrganizations(organizations: string[]): void {
    this.organizations$.update(organizations);
  }

  /**
   * Add queued alerts to counter
   *
   * @param {number} alerts
   * @return {void}
   * @memberof LayoutState
   */
  public addQueuedQualityAlerts(alerts: number): void {
    const currentAlerts: number = this.queuedQualityAlerts$.snapshot;
    this.queuedQualityAlerts$.update(currentAlerts + alerts);
  }

  /**
   * Add queued investigations to counter
   *
   * @param {number} investigations
   * @return {void}
   * @memberof LayoutState
   */
  public addQueuedQualityInvestigations(investigations: number): void {
    const currentInvestigations: number = this.queuedQualityInvestigationsCounter$.snapshot;
    this.queuedQualityInvestigationsCounter$.update(currentInvestigations + investigations);
  }

  /**
   * Reset queued alerts state
   *
   * @return {void}
   * @memberof LayoutState
   */
  public resetQueuedQualityAlerts(): void {
    this.queuedQualityAlerts$.reset();
  }

  /**
   * Reset queued investigations state
   *
   * @return {void}
   * @memberof LayoutState
   */
  public resetQualityInvestigationsCounter(): void {
    this.queuedQualityInvestigationsCounter$.reset();
  }

  /**
   * Counter of queued quality alerts state setter
   *
   * @param {number} alerts
   * @return {void}
   * @memberof LayoutState
   */
  public setReceivedQualityAlerts(alerts: number): void {
    this.resetReceivedQualityAlerts();
    this.receivedQualityAlertsCounter$.update(alerts);
  }

  /**
   * Add queued alerts to counter
   *
   * @param {number} alerts
   * @return {void}
   * @memberof LayoutState
   */
  public addReceivedQualityAlerts(alerts: number): void {
    const currentAlerts: number = this.receivedQualityAlertsCounter$.snapshot;
    this.receivedQualityAlertsCounter$.update(currentAlerts + alerts);
  }

  /**
   * Reset queued alerts state
   *
   * @return {void}
   * @memberof LayoutState
   */
  public resetReceivedQualityAlerts(): void {
    this.receivedQualityAlertsCounter$.reset();
  }

  /**
   * Is sidebar expanded state setter
   *
   * @param {boolean} isSideBarExpanded
   * @return {void}
   * @memberof LayoutState
   */
  public setIsSideBarExpanded(isSideBarExpanded: boolean): void {
    this.isSideBarExpanded$.update(isSideBarExpanded);
  }

  /**
   * Is footer displayed setter
   *
   * @param {boolean} isDisplayed
   * @return {void}
   * @memberof LayoutState
   */
  public setIsFooterDisplayed(isDisplayed: boolean): void {
    this.isFooterDisplayed$.update(isDisplayed);
  }
}
