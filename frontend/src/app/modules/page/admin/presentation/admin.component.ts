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
import { Role } from '@core/user/role.model';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html',
  styleUrls: [ './admin.component.scss' ],
})
export class AdminComponent {
  public activeUrl: string;
  public menuConfig: {
    name: string;
    icon: string;
    link: string;
    role?: Role;
  }[] = [
    {
      name: 'routing.adminBpn',
      icon: 'edit',
      link: '/admin/configure-bpn',
    },
    {
      name: 'routing.adminImport',
      icon: 'upload',
      link: '/admin/configure-import',
    },
    {
      name: 'routing.adminContract',
      icon: 'assignment_ind',
      link: '/admin/contracts',
    },
    {
      name: 'routing.adminPolicies',
      icon: 'description',
      link: '/admin/policies',
    },
    {
      name: 'routing.digitalTwinPart', 
      icon: 'build',
      link: '/admin/digital-twin-part',
    },
  
  ];

  constructor(router: Router) {
    this.activeUrl = router.url;

    router.events
      .pipe(filter(event => event instanceof NavigationEnd))
      .subscribe(({ urlAfterRedirects, url }: NavigationEnd) => (this.activeUrl = urlAfterRedirects ?? url));
  }

  public showFullText = false;
}
