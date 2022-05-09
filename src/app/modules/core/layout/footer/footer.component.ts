import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { realm } from 'src/app/modules/core/api/api.service.properties';
import { LayoutFacade } from 'src/app/modules/shared/abstraction/layout-facade';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  public queuedQualityAlerts$: Observable<number>;

  public queuedQualityInvestigations$: Observable<number>;

  public isExpanded$: Observable<boolean>;

  public marginLeft: number;

  public width: number;

  public isOpen$: Observable<boolean>;

  constructor(private layoutFacade: LayoutFacade, private router: Router) {
    this.queuedQualityAlerts$ = this.layoutFacade.queuedQualityAlerts$;
    this.queuedQualityInvestigations$ = this.layoutFacade.queuedQualityInvestigations$;
    this.isExpanded$ = this.layoutFacade.isSideBarExpanded$;
    this.isOpen$ = this.layoutFacade.isFooterDisplayed$;
  }

  public closeNotification(): void {
    this.layoutFacade.setIsFooterDisplayed(false);
  }

  // ToDo: Change this routing
  public navigateToQueuedQualityAlerts(): void {
    void this.router.navigate([`${realm}/quality-alert`]);
    this.layoutFacade.setTabIndex(1);
  }

  public navigateToQueuedQualityInvestigations(): void {
    void this.router.navigate([`${realm}/investigations`]);
    this.layoutFacade.setTabIndex(1);
  }
}
