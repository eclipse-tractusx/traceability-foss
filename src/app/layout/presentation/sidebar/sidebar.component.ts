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

import { Component, Input } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { filter } from 'rxjs/operators';
import { realm } from 'src/app/core/api/api.service.properties';
import { LayoutFacade } from 'src/app/shared/abstraction/layout-facade';

/**
 *
 *
 * @export
 * @class SidebarComponent
 */
@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {
  /**
   * is expanded
   *
   * @type {boolean}
   * @memberof SidebarComponent
   */
  @Input() expanded: boolean;

  /**
   * Realm name
   *
   * @type {string}
   * @memberof SidebarComponent
   */
  public realm = '';

  /**
   * Active menu
   *
   * @type {string}
   * @memberof SidebarComponent
   */
  public activeMenu = '';

  /**
   * Own label
   *
   * @type {string}
   * @memberof SidebarComponent
   */
  public ownLabel = '';

  /**
   * Other label
   *
   * @type {string}
   * @memberof SidebarComponent
   */
  public otherLabel = '';

  /**
   * Acls badge state
   *
   * @type {Observable<number>}
   * @memberof NavBarComponent
   */
  public qualityAlertsBadge$: Observable<number>;

  /**
   * Quality investigations badge
   *
   * @type {Observable<number>}
   * @memberof SidebarComponent
   */
  public qualityInvestigationsBadge$: Observable<number>;

  /**
   * Menu links
   *
   * @private
   * @readonly
   * @type {*} {
        dashboard: ``,
        search: ``,
        own: ``,
        other: ``,
        transactions: ``,
        about: ``,
        support: ``,
        qualityAlert: ``,
    }
   * @memberof SidebarComponent
   */
  private readonly menu = {
    dashboard: ``,
    'my-parts': ``,
    'supplier-parts': ``,
    about: ``,
    'quality-alert': ``,
  };

  /**
   * Sidebar width
   *
   * @readonly
   * @type {number}
   * @memberof SidebarComponent
   */
  get sidebarWidth(): number {
    return this.expanded ? 240 : 56;
  }

  /**
   * @constructor SidebarComponent
   * @param {Router} router
   * @param {LayoutFacade} layoutFacade
   * @memberof SidebarComponent
   */
  constructor(private router: Router, private layoutFacade: LayoutFacade) {
    this.realm = realm[1];
    this.menu = {
      dashboard: `/${this.realm}`,
      'my-parts': `/${this.realm}/my-parts`,
      'supplier-parts': `/${this.realm}/supplier-parts`,
      about: `/${this.realm}/about`,
      'quality-alert': `/${this.realm}/quality-alert`,
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

  /**
   * Redirect page
   *
   * @param {string} item
   * @return {void}
   * @memberof SidebarComponent
   */
  public navigate(item: string): void {
    this.router.navigate([this.menu[item]]).then();
    this.activeMenu = item;
    this.layoutFacade.setTabIndex(0);
  }

  /**
   * Menu labels for own and other pages
   *
   * @return {void}
   * @memberof SidebarComponent
   */
  public getSidebarLabels(): void {
    this.ownLabel = this.layoutFacade.getOrgPreferences.assetsTile;
    this.otherLabel = this.layoutFacade.getOrgPreferences.componentsTile;
  }
}
