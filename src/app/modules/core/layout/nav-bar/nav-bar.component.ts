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

import { Component, HostListener } from '@angular/core';
import { Router } from '@angular/router';
import { realm, realmLogo } from 'src/app/modules/core/api/api.service.properties';
import { LayoutFacade } from 'src/app/modules/shared/abstraction/layout-facade';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss'],
})
export class NavBarComponent {
  public isExpanded = false;
  public userInitials = '';
  public userDetails = { name: '', email: '', role: '' };

  constructor(private layoutFacade: LayoutFacade, private router: Router) {
    this.userInitials = this.layoutFacade.realmName;
    this.userDetails = this.layoutFacade.getUserInformation;
  }

  ngOnInit(): void {
    this.userDetails = this.layoutFacade.getUserInformation;
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
    this.router.navigate([`/${realm}`]).then();
  }

  public getCompanyLogo(): string {
    return realmLogo;
  }

  @HostListener('window:click', [])
  private onClick(): void {
    this.isExpanded = false;
  }
}
