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

import { Component, HostListener } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { NavigableUrls } from '@core/known-route';
import { environment } from '@env';
import { LayoutFacade } from '@shared/abstraction/layout-facade';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-user-navigation',
  templateUrl: './user-menu.component.html',
  styleUrls: [ './user-menu.component.scss' ],
})
export class UserMenuComponent {
  public isExpanded = false;
  public userInitials = '';
  public userDetails = { name: '', email: '', role: '' };
  public activeItem: string = '';
  public portalUrl = environment.portalUrl;
  public isAuthorized: boolean;

  constructor(private readonly layoutFacade: LayoutFacade, private readonly router: Router) {
    this.userInitials = this.layoutFacade.realName;
    this.userDetails = this.layoutFacade.userInformation;
    this.isAuthorized = this.userDetails.role === 'Admin';

    this.router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(({ urlAfterRedirects, url }: NavigationEnd) => {
        const currentUrl = urlAfterRedirects ?? url;
        this.activeItem = NavigableUrls.find(menuKey => currentUrl.includes(menuKey));
      });
  }

  public expand(event: Event): void {
    if (event) {
      event.stopPropagation();
      this.isExpanded = !this.isExpanded;
    }
  }

  public logOut(): void {
    this.layoutFacade.logOut();
  }

  public navigateToHome(): void {
    this.router.navigate([ '' ]).then();
  }

  @HostListener('window:click', [])
  private onClick(): void {
    this.isExpanded = false;
  }

  public openPortalPage(): void {
    window.open(environment.portalUrl, '_blank', 'noopener');
  }
}
