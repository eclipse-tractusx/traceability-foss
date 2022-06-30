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

import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { State } from '../model/state';

@Injectable({
  providedIn: 'root',
})
export class LayoutState {
  private readonly qualityInvestigationBadge$: State<number> = new State<number>(0);
  private readonly tabIndex$: State<number> = new State<number>(-1);
  private readonly receivedQualityAlertsCounter$: State<number> = new State<number>(0);
  private readonly isSideBarExpanded$: State<boolean> = new State<boolean>(false);

  private breadcrumbLabel: string;

  get getQualityInvestigationBadge$(): Observable<number> {
    return this.qualityInvestigationBadge$.observable;
  }

  get getBreadCrumbLabel(): string {
    return this.breadcrumbLabel;
  }

  get getTabIndex$(): Observable<number> {
    return this.tabIndex$.observable;
  }

  get getReceivedQualityAlertsCounter$(): Observable<number> {
    return this.receivedQualityAlertsCounter$.observable;
  }

  public setTabIndex(index: number): void {
    this.tabIndex$.update(index);
  }

  public setIsSideBarExpanded(isSideBarExpanded: boolean): void {
    this.isSideBarExpanded$.update(isSideBarExpanded);
  }
}
