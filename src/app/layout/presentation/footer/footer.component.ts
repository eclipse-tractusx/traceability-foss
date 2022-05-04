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

import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { realm } from 'src/app/core/api/api.service.properties';
import { LayoutFacade } from 'src/app/shared/abstraction/layout-facade';

/**
 *
 *
 * @export
 * @class FooterComponent
 * @implements {OnInit}
 */
@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss'],
})
export class FooterComponent {
  /**
   * Queued quality alerts
   *
   * @type {Observable<number>}
   * @memberof FooterComponent
   */
  public queuedQualityAlerts$: Observable<number>;

  /**
   * Queued investigations
   *
   * @type {Observable<number>}
   * @memberof FooterComponent
   */
  public queuedQualityInvestigations$: Observable<number>;

  /**
   * Is expanded state
   *
   * @type {Observable<boolean>}
   * @memberof FooterComponent
   */
  public isExpanded$: Observable<boolean>;

  /**
   * Css margin
   *
   * @type {number}
   * @memberof FooterComponent
   */
  public marginLeft: number;

  /**
   * Css width
   *
   * @type {number}
   * @memberof FooterComponent
   */
  public width: number;

  /**
   * Is footer displayed
   *
   * @type {boolean}
   * @memberof FooterComponent
   */
  public isOpen$: Observable<boolean>;

  /**
   * @constructor FooterComponent
   * @param {LayoutFacade} layoutFacade
   * @param {Router} router
   * @memberof FooterComponent
   */
  constructor(private layoutFacade: LayoutFacade, private router: Router) {
    this.queuedQualityAlerts$ = this.layoutFacade.queuedQualityAlerts$;
    this.queuedQualityInvestigations$ = this.layoutFacade.queuedQualityInvestigations$;
    this.isExpanded$ = this.layoutFacade.isSideBarExpanded$;
    this.isOpen$ = this.layoutFacade.isFooterDisplayed$;
  }

  /**
   * Close footer
   *
   * @return {void}
   * @memberof FooterComponent
   */
  public closeNotification(): void {
    this.layoutFacade.setIsFooterDisplayed(false);
  }

  /**
   * Route to quality alert page
   *
   * @return {void}
   * @memberof FooterComponent
   */
  public navigateToQueuedQualityAlerts(): void {
    this.router.navigate([`${realm[1]}/quality-alert`]);
    this.layoutFacade.setTabIndex(1);
  }

  /**
   * Route to quality investigations page
   *
   * @return {void}
   * @memberof FooterComponent
   */
  public navigateToQueuedQualityInvestigations(): void {
    this.router.navigate([`${realm[1]}/investigations`]);
    this.layoutFacade.setTabIndex(1);
  }
}
