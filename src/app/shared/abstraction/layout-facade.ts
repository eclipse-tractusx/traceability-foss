import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from 'src/app/core/auth/auth.service';
import { Realm } from 'src/app/core/model/realm.model';
import { UserService } from 'src/app/core/user/user.service';
import { LayoutState } from '../core/layout.state';
import { SharedService } from '../core/shared.service';
import { Mspid } from '../model/mspid.model';

@Injectable({
  providedIn: 'root',
})
export class LayoutFacade {
  /**
   * @constructor LayoutFacade (DI)
   * @param {LayoutState} layoutState
   * @param {UserService} userService
   * @param {AuthService} authService
   * @param {SharedService} sharedService
   * @memberof LayoutFacade
   */
  constructor(
    private layoutState: LayoutState,
    private userService: UserService,
    private authService: AuthService,
    private sharedService: SharedService,
  ) {}

  /**
   * User information getter
   *
   * @readonly
   * @type {{ name: string; email: string; role: string }}
   * @memberof LayoutFacade
   */
  get getUserInformation(): { name: string; email: string; role: string } {
    return {
      name: `${this.userService.getFirstname()} ${this.userService.getSurname()}`,
      email: `${this.userService.getEmail()}`,
      role: `${this.userService.getRoles().join(', ')}`,
    };
  }

  /**
   * Organization preferences
   *
   * @readonly
   * @type {Realm}
   * @memberof LayoutFacade
   */
  get getOrgPreferences(): Realm {
    return this.userService.getOrgPreferences();
  }

  /**
   * Icon name getter
   *
   * @readonly
   * @type {string}
   * @memberof LayoutFacade
   */
  get realmName(): string {
    return this.userService.getFirstname();
  }

  /**
   * Icon name getter
   *
   * @readonly
   * @type {string}
   * @memberof LayoutFacade
   */
  get mspid(): string {
    return this.userService.getMspid();
  }

  /**
   * Mspids state getter
   *
   * @readonly
   * @type {Observable<Mspid[]>}
   * @memberof LayoutFacade
   */
  get mspids(): Observable<Mspid[]> {
    return this.layoutState.getMspids$;
  }

  /**
   * Mpsids snapshot
   *
   * @readonly
   * @type {Mspid[]}
   * @memberof LayoutFacade
   */
  get mspidsSnapshot(): Mspid[] {
    return this.layoutState.mspIdsSnapshot;
  }

  /**
   * Custom breadcrumb getter
   *
   * @readonly
   * @type {string}
   * @memberof LayoutFacade
   */
  get breadcrumbLabel(): string {
    return this.layoutState.getBreadCrumbLabel;
  }

  /**
   * Tab index state getter
   *
   * @readonly
   * @type {Observable<number>}
   * @memberof LayoutFacade
   */
  get tabIndex$(): Observable<number> {
    return this.layoutState.getTabIndex$;
  }

  /**
   *Tab index state snapshot
   *
   * @readonly
   * @type {number}
   * @memberof LayoutFacade
   */
  get tabIndexSnapshot(): number {
    return this.layoutState.getTabIndexSnapshot;
  }

  /**
   * Queued quality alert count state
   *
   * @readonly
   * @type {Observable<number>}
   * @memberof LayoutFacade
   */
  get queuedQualityAlerts$(): Observable<number> {
    return this.layoutState.getQueuedQualityAlerts$;
  }

  /**
   * Queued quality investigations count state
   *
   * @readonly
   * @type {Observable<number>}
   * @memberof LayoutFacade
   */
  get queuedQualityInvestigations$(): Observable<number> {
    return this.layoutState.getQueuedQualityInvestigationsCounter$;
  }

  /**
   * Received quality alert count state
   *
   * @readonly
   * @type {Observable<number>}
   * @memberof LayoutFacade
   */
  get receivedQualityAlerts$(): Observable<number> {
    return this.layoutState.getReceivedQualityAlertsCounter$;
  }

  /**
   * Received quality investigations badge
   *
   * @readonly
   * @type {Observable<number>}
   * @memberof LayoutFacade
   */
  get receivedQualityInvestigations$(): Observable<number> {
    return this.layoutState.getQualityInvestigationBadge$;
  }

  /**
   * Is sidebar expanded state
   *
   * @readonly
   * @type {Observable<boolean>}
   * @memberof LayoutFacade
   */
  get isSideBarExpanded$(): Observable<boolean> {
    return this.layoutState.getIsSideBarExpanded$;
  }

  /**
   * Is footer displayed flag
   *
   * @readonly
   * @type {Observable<boolean>}
   * @memberof LayoutFacade
   */
  get isFooterDisplayed$(): Observable<boolean> {
    return this.layoutState.getIsFooterDisplayed$;
  }

  /**
   * Organizations state getter
   *
   * @readonly
   * @type {Observable<string[]>}
   * @memberof LayoutFacade
   */
  get organizations$(): Observable<string[]> {
    return this.layoutState.getOrganizations$;
  }

  /**
   * Organizations state snapshot
   *
   * @readonly
   * @type {string[]}
   * @memberof LayoutFacade
   */
  get organizationsSnapshot(): string[] {
    return this.layoutState.organizationsSnapshot;
  }

  /**
   *
   * Logout request
   *
   * @return {void}
   * @memberof LayoutFacade
   */
  public logOut(): void {
    this.authService.logOut();
  }

  /**
   * Is empty helper method
   *
   * @param {unknown} object
   * @return {boolean}
   * @memberof LayoutFacade
   */
  public isEmpty(object: unknown): boolean {
    return this.sharedService.isEmpty(object);
  }

  /**
   * Set mspids request
   *
   * @return {void}
   * @memberof LayoutFacade
   */
  public setMspids(): void {
    this.sharedService.getMspids().subscribe((mspidsValues: string[]) => {
      const mspids: Mspid[] = [];
      const colorPalette = ['#e83e8c', '#03a9f4', '#6610f2', '#fe6702', '#20c997'];
      let index = 0;

      mspidsValues.forEach(value => {
        mspids.push({ name: value, color: colorPalette[index] });
        index++;
      });
      this.layoutState.setMspids(mspids);
    });
  }

  /**
   * Mspid request for the resolver route
   *
   * @return {Observable<Mspid[]>}
   * @memberof LayoutFacade
   */
  public getMspidRequest(): Observable<Mspid[]> {
    return this.sharedService.getMspids().pipe(
      map((mspidsValues: string[]) => {
        const mspids: Mspid[] = [];
        const colorPalette = ['#e83e8c', '#03a9f4', '#6610f2', '#fe6702', '#20c997'];
        let index = 0;

        mspidsValues.forEach(value => {
          mspids.push({ name: value, color: colorPalette[index] });
          index++;
        });
        return mspids;
      }),
    );
  }

  /**
   * Organization state setter
   *
   * @return {void}
   * @memberof LayoutFacade
   */
  public setOrganizations(): void {
    this.sharedService.getAllOrganizations().subscribe((organizations: string[]) => {
      this.layoutState.setOrganizations(organizations);
    });
  }

  /**
   * Tab index state setter
   *
   * @param {number} index
   * @return {void}
   * @memberof LayoutFacade
   */
  public setTabIndex(index: number): void {
    this.layoutState.setTabIndex(index);
  }

  /**
   * Adds quality investigations to counter
   *
   * @param {number} investigations
   * @return {void}
   * @memberof LayoutFacade
   */
  public addQueuedQualityInvestigations(investigations: number): void {
    this.layoutState.addQueuedQualityInvestigations(investigations);
  }

  /**
   * Quality investigations setter
   *
   * @param {number} investigations
   * @return {void}
   * @memberof LayoutFacade
   */
  public setQueuedQualityInvestigations(investigations: number): void {
    this.layoutState.setQueuedQualityInvestigations(investigations);
  }

  /**
   * Reset quality investigations counter state
   *
   * @return {void}
   * @memberof LayoutFacade
   */
  public resetQueuedQualityInvestigations(): void {
    this.layoutState.resetQualityInvestigationsCounter();
  }

  /**
   * Add quality alerts to counter
   *
   * @param {number} alerts
   * @return {void}
   * @memberof LayoutFacade
   */
  public addQueuedQualityAlerts(alerts: number): void {
    this.layoutState.addQueuedQualityAlerts(alerts);
  }

  /**
   * Quality alerts setter
   *
   * @param {number} alerts
   * @return {void}
   * @memberof LayoutFacade
   */
  public setQueuedQualityAlerts(alerts: number): void {
    this.layoutState.setQueuedQualityAlerts(alerts);
  }

  /**
   * Reset quality alert counter state
   *
   * @return {void}
   * @memberof LayoutFacade
   */
  public resetQueuedQualityAlerts(): void {
    this.layoutState.resetQueuedQualityAlerts();
  }

  /**
   * Add quality alerts to counter
   *
   * @param {number} alerts
   * @return {void}
   * @memberof LayoutFacade
   */
  public addReceivedQualityAlerts(alerts: number): void {
    this.layoutState.addReceivedQualityAlerts(alerts);
  }

  /**
   * Quality alerts setter
   *
   * @param {number} alerts
   * @return {void}
   * @memberof LayoutFacade
   */
  public setReceivedQualityAlerts(alerts: number): void {
    this.layoutState.setReceivedQualityAlerts(alerts);
  }

  /**
   * Reset quality alert counter state
   *
   * @return {void}
   * @memberof LayoutFacade
   */
  public resetReceivedQualityAlerts(): void {
    this.layoutState.resetReceivedQualityAlerts();
  }

  /**
   * Add quality alerts to counter
   *
   * @param {number} alerts
   * @return {void}
   * @memberof LayoutFacade
   */
  public addReceivedQualityInvestigations(alerts: number): void {
    this.layoutState.addQualityInvestigationToBadge(alerts);
  }

  /**
   * Quality alerts setter
   *
   * @param {number} alerts
   * @return {void}
   * @memberof LayoutFacade
   */
  public setReceivedQualityInvestigationsCounter(alerts: number): void {
    this.layoutState.setQualityInvestigationBadge(alerts);
  }

  /**
   * Reset quality alert counter state
   *
   * @return {void}
   * @memberof LayoutFacade
   */
  public resetReceivedQualityInvestigations(): void {
    this.layoutState.resetQualityInvestigationBadge();
  }

  /**
   * Is sidebar expanded setter
   *
   * @param {boolean} isExpanded
   * @return {void}
   * @memberof LayoutFacade
   */
  public setIsSideBarExpanded(isExpanded: boolean): void {
    this.layoutState.setIsSideBarExpanded(isExpanded);
  }

  /**
   * Is footer displayed setter
   *
   * @param {boolean} isDisplayed
   * @return {void}
   * @memberof LayoutFacade
   */
  public setIsFooterDisplayed(isDisplayed: boolean): void {
    this.layoutState.setIsFooterDisplayed(isDisplayed);
  }

  /**
   * Can deactivate state setter
   *
   * @param {boolean} canDeactivate
   * @return {void}
   * @memberof LayoutFacade
   */
  public setCanDeactivate(canDeactivate: boolean): void {
    this.layoutState.setCanDeactivate(canDeactivate);
  }
}
