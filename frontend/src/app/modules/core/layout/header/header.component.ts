/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

import { Component } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { KnownUrl, NavigableUrls } from '@core/known-route';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: [ './header.component.scss' ],
})
export class HeaderComponent {
  public activeMenu = '';

  public readonly iconMapping: Record<KnownUrl, string> = {
    dashboard: 'dashboard',
    about: 'info',
    parts: 'build',
    otherParts: 'commute',
    investigations: 'inbox',
    alerts: 'notification_important',
    admin: 'apps',
  };

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
