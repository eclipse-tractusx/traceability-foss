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
import { ActivatedRoute } from '@angular/router';
import { RoleService } from '@core/user/role.service';
import { LayoutFacade } from '@shared/abstraction/layout-facade';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-error',
  templateUrl: './error-page.component.html',
  styleUrls: [ './error-page.component.scss' ],
})
export class ErrorPageComponent {
  public title: string = 'errorPage.title';
  public message: string = 'errorPage.message';
  public actionUrl: string = '#';
  public actionLabel: string = 'actions.homepage';
  public showSignOutButton: boolean = false;

  constructor(
    private readonly roleService: RoleService,
    private readonly activatedRoute: ActivatedRoute,
    private readonly layoutFacade: LayoutFacade,
  ) {
    const errorPage$ = activatedRoute.data.pipe(map(d => d.errorPage));
    errorPage$.subscribe({
      next: (errorPage: Record<string, string>) => {
        if (!errorPage?.type) return;
        this.title = errorPage.type + '.title';
        this.message = errorPage.type + '.message';
      },
    });

    // if user has no sufficient permissions to use this app
    if (!roleService.isUser() && !roleService.isAdmin()) {
      this.actionUrl = '';
      this.actionLabel = '';
      this.showSignOutButton = true;
    }
  }

  public logOut(): void {
    this.layoutFacade.logOut();
  }
}
