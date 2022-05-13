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

import {
  AfterContentChecked,
  AfterContentInit,
  Component,
  ContentChildren,
  EventEmitter,
  Output,
  QueryList,
} from '@angular/core';
import { Observable } from 'rxjs/internal/Observable';
import { delay, map, startWith } from 'rxjs/operators';
import { LayoutFacade } from '../../abstraction/layout-facade';
import { TabItemComponent } from './tab.item.component';

@Component({
  selector: 'app-tabs',
  templateUrl: './tabs.component.html',
  styleUrls: ['./tabs.component.scss'],
})
export class TabsComponent implements AfterContentInit, AfterContentChecked {
  @ContentChildren(TabItemComponent) tabs: QueryList<TabItemComponent>;

  @Output() active: EventEmitter<string> = new EventEmitter<string>();

  public tabItems$: Observable<TabItemComponent[]>;
  public activeTab: TabItemComponent;
  public tabIndex$: Observable<number>;

  constructor(private layoutFacade: LayoutFacade) {
    this.tabIndex$ = this.layoutFacade.tabIndex$;
  }

  ngAfterContentInit(): void {
    this.tabItems$ = this.tabs.changes
      .pipe(startWith(''))
      .pipe(delay(0))
      .pipe(map(() => this.tabs.toArray()));
  }

  ngAfterContentChecked(): void {
    // choose the default tab
    // we need to wait for a next VM turn,
    // because Tab item content, will not be initialized yet
    if (!this.activeTab) {
      Promise.resolve().then(() => {
        this.tabIndex$.subscribe((index: number) => {
          this.activeTab = index !== -1 ? this.tabs.toArray()[index] : this.tabs.first;
        });
      });
    }
  }

  public selectTab(tabItem: TabItemComponent): void {
    if (this.activeTab === tabItem) {
      return;
    }

    if (this.activeTab) {
      this.activeTab.isActive = false;
    }

    this.activeTab = tabItem;
    this.active.next(this.activeTab.label);

    tabItem.isActive = true;
  }
}
