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

import { Component, HostBinding } from '@angular/core';
import { MenuItemComponent } from './menu-item/menu-item.component';

/**
 * https://indepth.dev/posts/1297/building-a-reusable-menu-component
 *
 * @export
 * @class MenuComponent
 */
@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss'],
})
export class MenuComponent {
  /**
   * Display style
   *
   * @type {string}
   * @memberof MenuComponent
   */
  @HostBinding('style.display') public display = 'inline-block';

  /**
   * Position style
   *
   * @type {string}
   * @memberof MenuComponent
   */
  @HostBinding('style.position') public position = 'absolute';

  /**
   * Active menu
   *
   * @private
   * @type {MenuItemComponent}
   * @memberof MenuComponent
   */
  private activeMenuItem: MenuItemComponent;

  /**
   * Register open menu
   *
   * @param {MenuItemComponent} menuItem
   * @return {void}
   * @memberof MenuComponent
   */
  public registerOpenedMenu(menuItem: MenuItemComponent): void {
    this.activeMenuItem = menuItem;
  }

  /**
   * Close menu if exists
   *
   * @return {void}
   * @memberof MenuComponent
   */
  public closeOpenedMenuIfExists(): void {
    if (this.activeMenuItem) {
      this.activeMenuItem.clearContainer();
    }
  }
}
