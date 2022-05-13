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
