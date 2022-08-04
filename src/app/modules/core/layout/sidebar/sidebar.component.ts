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

import { Component, Input } from '@angular/core';
import { NavigationEnd, Router } from '@angular/router';
import { realm } from '@core/api/api.service.properties';
import { filter } from 'rxjs/operators';

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {
  @Input() expanded: boolean;

  public activeMenu = '';

  private readonly menu = {
    dashboard: '',
    about: '',
    parts: '',
    otherParts: '',
    investigations: '',
    admin: '',
  };

  get sidebarWidth(): number {
    return this.expanded ? 240 : 56;
  }

  constructor(private readonly router: Router) {
    this.menu = {
      dashboard: `/${realm}`,
      about: `/${realm}/about`,
      parts: `/${realm}/parts`,
      otherParts: `/${realm}/otherParts`,
      investigations: `/${realm}/investigations`,
      admin: `/${realm}/admin`,
    };

    this.router.events.pipe(filter(e => e instanceof NavigationEnd)).subscribe((r: NavigationEnd) => {
      const keys: string[] = Object.keys(this.menu);
      const parentRoute: string = keys.find(key => this.menu[key] === r.url);

      this.activeMenu = parentRoute || keys.find(menuKey => r.url.includes(menuKey));
    });
  }

  public navigate(item: string): void {
    this.router.navigate([this.menu[item]]).then();
    this.activeMenu = item;
  }
}
