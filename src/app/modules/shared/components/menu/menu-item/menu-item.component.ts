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

import { DOCUMENT } from '@angular/common';
import { Component, Inject, Input, OnDestroy, Optional, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { EventManager } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { MenuStateService } from 'src/app/modules/shared/components/menu/menu-state.service';
import { SharedService } from 'src/app/modules/shared/service/shared.service';
import { MenuComponent } from '../menu.component';

// https://indepth.dev/posts/1297/building-a-reusable-menu-component
@Component({
  selector: 'app-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.component.scss'],
})
export class MenuItemComponent implements OnDestroy {
  @Input() public menuFor: TemplateRef<MenuComponent>;
  @Input() button: string;
  @Input() disable: boolean;
  @Input() id: string;

  @ViewChild('viewContainerRef', { read: ViewContainerRef }) public viewContainerRef: ViewContainerRef;

  public arrow: string;
  public isExpanded = false;

  private menuStateSubscription: Subscription;
  private removeGlobalEventListener = new Function();

  constructor(
    @Optional() private parent: MenuComponent,
    @Inject(DOCUMENT) private documentRef: Document,
    private eventManager: EventManager,
    private menuStateService: MenuStateService,
    private sharedService: SharedService,
  ) {}

  ngOnDestroy(): void {
    this.removeClickOutsideListener();
    this.unsubscribe();
  }

  public onClick(): void {
    if (!this.disable) {
      if (!this.id) {
        this.id = this.sharedService.generateRandomID();
      }
      if (!this.isRoot() && this.menuStateService.menuId.getValue() !== this.id) {
        this.broadcastMenuClear();
      }
      this.isExpanded = true;
      if (this.isLeaf() && this.isExpanded) {
        this.broadcastMenuClear();
      } else if (this.containerIsEmpty()) {
        this.addHandlersForRootElement();
        this.closeAlreadyOpenedMenuInTheSameSubtree();
        this.registerOpenedMenu();
        this.addTemplateToContainer(this.menuFor);
        this.menuStateService.menuId.next(this.id);
      } else {
        this.removeClickOutsideListener();
        this.clearContainer();
        this.isExpanded = false;
      }
    }
  }

  public clearContainer(): void {
    this.viewContainerRef.clear();
  }

  public getExpandedIcon(): string {
    return this.isExpanded ? 'keyboard_arrow_up' : 'keyboard_arrow_down';
  }

  private isLeaf(): boolean {
    return this.parent && !this.hasNestedSubMenu();
  }

  private hasNestedSubMenu(): boolean {
    return !!this.menuFor;
  }

  private closeAlreadyOpenedMenuInTheSameSubtree(): void {
    if (this.parent) {
      this.parent.closeOpenedMenuIfExists();
    }
  }

  private registerOpenedMenu(): void {
    if (this.parent) {
      this.parent.registerOpenedMenu(this);
    }
  }

  private containerIsEmpty(): boolean {
    return this.viewContainerRef.length === 0;
  }

  private addTemplateToContainer(template: TemplateRef<MenuComponent>): void {
    if (template) {
      this.viewContainerRef.createEmbeddedView(template);
    }
  }

  public isRoot(): boolean {
    return !!this.parent;
  }

  private addHandlersForRootElement(): void {
    if (!this.parent) {
      this.subscribeToClearMenuMessages();
      this.addClickOutsideListener();
    }
  }

  private addClickOutsideListener(): void {
    this.removeGlobalEventListener = this.eventManager.addGlobalEventListener(
      'window',
      'click',
      this.closeMenuOnOutsideClick.bind(this),
    );
  }

  private removeClickOutsideListener(): void {
    if (this.removeGlobalEventListener) {
      this.removeGlobalEventListener();
    }
  }

  private closeMenuOnOutsideClick({ target }): void {
    const appMenuItem = this.documentRef.querySelector('app-menu-item > app-menu');
    if (appMenuItem && !appMenuItem.parentElement.contains(target)) {
      this.removeClickOutsideListener();
      this.broadcastMenuClear();
    }
  }

  private broadcastMenuClear(): void {
    this.menuStateService.clearMenu();
  }

  private subscribeToClearMenuMessages(): void {
    this.menuStateSubscription = this.menuStateService.state$.subscribe(() => {
      this.clearContainer();
      this.isExpanded = false;
    });
  }

  private unsubscribe(): void {
    if (this.menuStateSubscription) {
      this.menuStateSubscription.unsubscribe();
    }
  }
}
