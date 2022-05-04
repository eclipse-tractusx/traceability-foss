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

import { DOCUMENT } from '@angular/common';
import { Component, Inject, Input, OnDestroy, Optional, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { EventManager } from '@angular/platform-browser';
import { Subscription } from 'rxjs';
import { MenuStateService } from 'src/app/shared/components/menu/menu-state.service';
import { SharedService } from 'src/app/shared/core/shared.service';
import { MenuComponent } from '../menu.component';

/**
 * https://indepth.dev/posts/1297/building-a-reusable-menu-component
 *
 * @export
 * @class MenuItemComponent
 * @implements {OnDestroy}
 */
@Component({
  selector: 'app-menu-item',
  templateUrl: './menu-item.component.html',
  styleUrls: ['./menu-item.component.scss'],
})
export class MenuItemComponent implements OnDestroy {
  /**
   * Menu component
   *
   * @type {TemplateRef<MenuComponent>}
   * @memberof MenuItemComponent
   */
  @Input() public menuFor: TemplateRef<MenuComponent>;

  /**
   * Menu button
   *
   * @type {string}
   * @memberof MenuItemComponent
   */
  @Input() button: string;

  /**
   * Is menu disable
   *
   * @type {boolean}
   * @memberof MenuItemComponent
   */
  @Input() disable: boolean;

  /**
   * Menu id
   *
   * @type {string}
   * @memberof MenuItemComponent
   */
  @Input() id: string;

  /**
   * menu view container
   *
   * @type {ViewContainerRef}
   * @memberof MenuItemComponent
   */
  @ViewChild('viewContainerRef', { read: ViewContainerRef }) public viewContainerRef: ViewContainerRef;

  /**
   * Menu icon
   *
   * @type {string}
   * @memberof MenuItemComponent
   */
  public arrow: string;

  /**
   * Is menu expanded
   *
   * @type {boolean}
   * @memberof MenuItemComponent
   */
  public isExpanded = false;

  /**
   * Menu state subscription
   *
   * @private
   * @type {Subscription}
   * @memberof MenuItemComponent
   */
  private menuStateSubscription: Subscription;

  /**
   * Remove listener
   *
   * @private
   * @type {Function}
   * @memberof MenuItemComponent
   */
  private removeGlobalEventListener = new Function();

  /**
   * @constructor MenuItemComponent
   * @param {MenuComponent} parent
   * @param {Document} documentRef
   * @param {EventManager} eventManager
   * @param {MenuStateService} menuStateService
   * @param {SharedService} sharedService
   * @memberof MenuItemComponent
   */
  constructor(
    @Optional() private parent: MenuComponent,
    @Inject(DOCUMENT) private documentRef: Document,
    private eventManager: EventManager,
    private menuStateService: MenuStateService,
    private sharedService: SharedService,
  ) {}

  /**
   * Angular lifecycle method - Ng On Destroy
   *
   * @return {void}
   * @memberof MenuItemComponent
   */
  ngOnDestroy(): void {
    this.removeClickOutsideListener();
    this.unsubscribe();
  }

  /**
   * Menu click event
   *
   * @return {void}
   * @memberof MenuItemComponent
   */
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

  /**
   * Clear container
   *
   * @return {void}
   * @memberof MenuItemComponent
   */
  public clearContainer(): void {
    this.viewContainerRef.clear();
  }

  /**
   * Menu icon
   *
   * @return {string}
   * @memberof MenuItemComponent
   */
  public getExpandedIcon(): string {
    return this.isExpanded ? 'arrow-up-s-fill' : 'arrow-down-s-fill';
  }

  /**
   * Is leaf
   *
   * @private
   * @return {boolean}
   * @memberof MenuItemComponent
   */
  private isLeaf(): boolean {
    return this.parent && !this.hasNestedSubMenu();
  }

  /**
   * Has nested menu
   *
   * @private
   * @return {boolean}
   * @memberof MenuItemComponent
   */
  private hasNestedSubMenu(): boolean {
    return !!this.menuFor;
  }

  /**
   * Close opened menu
   *
   * @private
   * @return {void}
   * @memberof MenuItemComponent
   */
  private closeAlreadyOpenedMenuInTheSameSubtree(): void {
    if (this.parent) {
      this.parent.closeOpenedMenuIfExists();
    }
  }

  /**
   * Register open menu
   *
   * @private
   * @return {void}
   * @memberof MenuItemComponent
   */
  private registerOpenedMenu(): void {
    if (this.parent) {
      this.parent.registerOpenedMenu(this);
    }
  }

  /**
   * Check if container is empty
   *
   * @private
   * @return {boolean}
   * @memberof MenuItemComponent
   */
  private containerIsEmpty(): boolean {
    return this.viewContainerRef.length === 0;
  }

  /**
   * Add template menu to container
   *
   * @private
   * @param {TemplateRef<MenuComponent>} template
   * @return {void}
   * @memberof MenuItemComponent
   */
  private addTemplateToContainer(template: TemplateRef<MenuComponent>): void {
    if (template) {
      this.viewContainerRef.createEmbeddedView(template);
    }
  }

  /**
   * Is root
   *
   * @return {boolean}
   * @memberof MenuItemComponent
   */
  public isRoot(): boolean {
    return !!this.parent;
  }

  /**
   * Root handlers
   *
   * @private
   * @return {void}
   * @memberof MenuItemComponent
   */
  private addHandlersForRootElement(): void {
    if (!this.parent) {
      this.subscribeToClearMenuMessages();
      this.addClickOutsideListener();
    }
  }

  /**
   * Click outside listener
   *
   * @private
   * @return {void}
   * @memberof MenuItemComponent
   */
  private addClickOutsideListener(): void {
    this.removeGlobalEventListener = this.eventManager.addGlobalEventListener(
      'window',
      'click',
      this.closeMenuOnOutsideClick.bind(this),
    );
  }

  /**
   * Remove click outside listener
   *
   * @private
   * @return {void}
   * @memberof MenuItemComponent
   */
  private removeClickOutsideListener(): void {
    if (this.removeGlobalEventListener) {
      this.removeGlobalEventListener();
    }
  }

  /**
   * Close menu by clicking outside
   *
   * @private
   * @param { target }
   * @return {void}
   * @memberof MenuItemComponent
   */
  private closeMenuOnOutsideClick({ target }): void {
    const appMenuItem = this.documentRef.querySelector('app-menu-item > app-menu');
    if (appMenuItem && !appMenuItem.parentElement.contains(target)) {
      this.removeClickOutsideListener();
      this.broadcastMenuClear();
    }
  }

  /**
   * Clear menu state
   *
   * @private
   * @return {void}
   * @memberof MenuItemComponent
   */
  private broadcastMenuClear(): void {
    this.menuStateService.clearMenu();
  }

  /**
   * Clear subscription
   *
   * @private
   * @return {void}
   * @memberof MenuItemComponent
   */
  private subscribeToClearMenuMessages(): void {
    this.menuStateSubscription = this.menuStateService.state$.subscribe(() => {
      this.clearContainer();
      this.isExpanded = false;
    });
  }

  /**
   * Unsubscribe
   *
   * @private
   * @return {void}
   * @memberof MenuItemComponent
   */
  private unsubscribe(): void {
    if (this.menuStateSubscription) {
      this.menuStateSubscription.unsubscribe();
    }
  }
}
