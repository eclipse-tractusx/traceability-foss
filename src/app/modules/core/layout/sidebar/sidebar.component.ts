import { Component, Input } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { realm } from 'src/app/modules/core/api/api.service.properties';
import { LayoutFacade } from 'src/app/modules/shared/abstraction/layout-facade';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {
  @Input() expanded: boolean;

  public realm = '';

  public activeMenu = '';

  public ownLabel = '';

  public otherLabel = '';

  public qualityAlertsBadge$: Observable<number>;

  public qualityInvestigationsBadge$: Observable<number>;

  private readonly menu = {
    dashboard: '',
    about: '',
  };

  get sidebarWidth(): number {
    return this.expanded ? 240 : 56;
  }

  constructor(private router: Router, private layoutFacade: LayoutFacade) {
    this.realm = realm;
    this.menu = {
      dashboard: `/${this.realm}`,
      about: `/${this.realm}/about`,
    };

    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe((r: NavigationEnd) => {
      const keys: string[] = Object.keys(this.menu);
      const parentRoute: string = keys.find(key => this.menu[key] === r.url);

      this.activeMenu = parentRoute || keys.find(menuKey => r.url.includes(menuKey));
    });

    this.getSidebarLabels();
    this.qualityAlertsBadge$ = this.layoutFacade.receivedQualityAlerts$;
    this.qualityInvestigationsBadge$ = this.layoutFacade.receivedQualityInvestigations$;
  }

  public navigate(item: string): void {
    this.router.navigate([this.menu[item]]).then();
    this.activeMenu = item;
    this.layoutFacade.setTabIndex(0);
  }

  public getSidebarLabels(): void {
    this.ownLabel = this.layoutFacade.getOrgPreferences.assetsTile;
    this.otherLabel = this.layoutFacade.getOrgPreferences.componentsTile;
  }
}
