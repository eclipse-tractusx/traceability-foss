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

import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { LayoutFacade } from 'src/app/shared/abstraction/layout-facade';

/**
 *
 *
 * @export
 * @class PrivateLayoutComponent
 * @implements {OnInit}
 */
@Component({
  selector: 'app-private-layout',
  templateUrl: './private-layout.component.html',
  styleUrls: ['./private-layout.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class PrivateLayoutComponent implements OnInit {
  /**
   * Is sidebar expanded
   *
   * @type {boolean}
   * @memberof PrivateLayoutComponent
   */
  public expanded: boolean;

  /**
   * @constructor PrivateLayoutComponent
   * @memberof PrivateLayoutComponent
   */
  constructor(private layoutFacade: LayoutFacade) {
    this.expanded = false;
  }

  /**
   * Angular lifecycle method - Ng On Init
   *
   * @return {void}
   * @memberof PrivateLayoutComponent
   */
  ngOnInit(): void {
    this.handleResize();
  }

  /**
   * Window resizing
   *
   * @return {void}
   * @memberof PrivateLayoutComponent
   */
  public handleResize(): void {
    const match = window.matchMedia('(min-width: 1024px)');
    match.addEventListener('change', e => {
      this.expanded = e.matches;
    });
  }

  /**
   * Sidebar toggle
   *
   * @return {void}
   * @memberof PrivateLayoutComponent
   */
  public manualToggle(): void {
    this.expanded = !this.expanded;
    this.layoutFacade.setIsSideBarExpanded(this.expanded);
  }
}
