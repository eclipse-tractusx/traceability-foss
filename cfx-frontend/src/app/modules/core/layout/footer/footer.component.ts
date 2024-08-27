import { Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { NavigableUrls } from '@core/known-route';
import { Role } from '@core/user/role.model';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  public activeMenu = '';
  protected readonly Role = Role;

  constructor(router: Router) {
    router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(({ urlAfterRedirects, url }: NavigationEnd) => {
        const currentUrl = urlAfterRedirects ?? url;
        this.activeMenu = NavigableUrls.find(menuKey => currentUrl.includes(menuKey));
      });
  }
  public openDocumentation(): void {
    window.open('https://eclipse-tractusx.github.io/traceability-foss/docs/', '_blank', 'noopener');
  }
}
