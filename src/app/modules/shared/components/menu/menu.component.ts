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

import { Component, HostBinding } from '@angular/core';
import { MenuItemComponent } from './menu-item/menu-item.component';

// https://indepth.dev/posts/1297/building-a-reusable-menu-component
@Component({
  selector: 'app-menu',
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.scss'],
})
export class MenuComponent {
  @HostBinding('style.display') public display = 'inline-block';
  @HostBinding('style.position') public position = 'absolute';

  private activeMenuItem: MenuItemComponent;

  public registerOpenedMenu(menuItem: MenuItemComponent): void {
    this.activeMenuItem = menuItem;
  }

  public closeOpenedMenuIfExists(): void {
    if (this.activeMenuItem) {
      this.activeMenuItem.clearContainer();
    }
  }
}
