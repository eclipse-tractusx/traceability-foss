import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AuthService } from 'src/app/modules/core/auth/auth.service';
import { Realm } from 'src/app/modules/core/model/realm.model';
import { UserService } from 'src/app/modules/core/user/user.service';
import { LayoutState } from '../service/layout.state';
import { SharedService } from '../service/shared.service';
import { Mspid } from '../model/mspid.model';

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

  get mspid(): string {
    return this.userService.getMspid();
  }

  get mspids$(): Observable<Mspid[]> {
    return this.layoutState.getMspids$;
  }

  get mspidsSnapshot(): Mspid[] {
    return this.layoutState.mspIdsSnapshot;
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

  public setMspids(): void {
    this.sharedService.getMspids().subscribe((mspidsValues: string[]) => {
      const mspids: Mspid[] = [];
      const colorPalette = ['#e83e8c', '#03a9f4', '#6610f2', '#fe6702', '#20c997'];

      mspidsValues.forEach((value, index) => mspids.push({ name: value, color: colorPalette[index] }));
      this.layoutState.setMspids(mspids);
    });
  }

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
