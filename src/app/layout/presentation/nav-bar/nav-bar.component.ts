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

import { Component, HostListener } from '@angular/core';
import { version } from '../../../../../package.json';
import { realm } from 'src/app/core/api/api.service.properties';
import { Router } from '@angular/router';
import { LayoutFacade } from 'src/app/shared/abstraction/layout-facade';
import { Mspid } from 'src/app/shared/model/mspid.model';

/**
 *
 *
 * @export
 * @class NavBarComponent
 * @implements {OnInit}
 */
@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss'],
})
export class NavBarComponent {
  /**
   * Is menu expanded
   *
   * @type {boolean}
   * @memberof NavBarComponent
   */
  public isExpanded = false;

  /**
   * User initials
   *
   * @type {string}
   * @memberof NavBarComponent
   */
  public initials = '';

  /**
   * UI version
   *
   * @type {string}
   * @memberof NavBarComponent
   */
  public version = '';

  /**
   * User details
   *
   * @type {{ name: string, email: string, role: string }}
   * @memberof NavBarComponent
   */
  public user = { name: '', email: '', role: '' };

  /**
   * Logged realm
   *
   * @type {Mspid}
   * @memberof NavBarComponent
   */
  public realm: Mspid;

  /**
   * Icon loading flag
   *
   * @type {boolean}
   * @memberof NavBarComponent
   */
  public iconLoading = false;

  /**
   * @constructor NavBarComponent
   * @param {LayoutFacade} layoutFacade
   * @param {Router} router
   * @memberof NavBarComponent
   */
  constructor(private layoutFacade: LayoutFacade, private router: Router) {
    this.version = version;
    this.initials = this.layoutFacade.realmName;
    this.user = this.layoutFacade.getUserInformation;
    this.layoutFacade.mspids.subscribe((realms: Mspid[]) => {
      this.realm = realms.find(mspid => mspid.name === this.layoutFacade.mspid);
      if (this.realm) {
        this.realm.name =
          this.layoutFacade.mspid === this.layoutFacade.realmName
            ? this.layoutFacade.mspid
            : this.layoutFacade.realmName;
        this.iconLoading = false;
      }
    });
  }

  /**
   * Angular lifecycle method - Ng On Init
   *
   * @return {void}
   * @memberof NavBarComponent
   */
  ngOnInit(): void {
    this.iconLoading = true;
    this.user = this.layoutFacade.getUserInformation;
    this.layoutFacade.setMspids();
  }

  /**
   * Menu expand event
   *
   * @param {Event} event
   * @return {void}
   * @memberof NavBarComponent
   */
  public expand(event: Event): void {
    if (event) {
      event.stopPropagation();
      this.isExpanded = !this.isExpanded;
    }
  }

  /**
   * Logout event
   *
   * @return {void}
   * @memberof NavBarComponent
   */
  public logOut(): void {
    this.layoutFacade.logOut();
  }

  /**
   * Redirect to home page
   *
   * @return {void}
   * @memberof NavBarComponent
   */
  public navigateToHome(): void {
    this.router.navigate([`/${realm[1]}`]).then();
  }

  public getCompanyLogo(): string {
    const logo = {
      bmw: '/assets/images/BMW_2.png',
      'taas-zf': '/assets/images/zf.png',
      'taas-gris': '/assets/images/zf.png',
      'taas-henkel': '/assets/images/henkel-logo-0.png',
      'taas-basf': '/assets/images/basf.png',
    };
    return logo[realm[1]];
  }

  /**
   * On click listener
   *
   * @private
   * @return {void}
   * @memberof NavBarComponent
   */
  @HostListener('window:click', [])
  private onClick(): void {
    this.isExpanded = false;
  }
}
