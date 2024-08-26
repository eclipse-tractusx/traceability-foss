/********************************************************************************
 * Copyright (c) 2022, 2023 Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 * Copyright (c) 2022, 2023 ZF Friedrichshafen AG
 * Copyright (c) 2022, 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/

import { Injectable } from '@angular/core';
import { DashboardStats } from '@page/dashboard/model/dashboard.model';

import { Notifications } from '@shared/model/notification.model';
import { State } from '@shared/model/state';
import { Observable } from 'rxjs';
import { View } from 'src/app/modules/shared/model/view.model';

@Injectable()
export class DashboardState {

  private readonly _dashboardStats$: State<View<DashboardStats>> = new State<View<DashboardStats>>({ loader: true });

  // recent notifications
  private readonly _recentReceivedInvestigations$: State<View<Notifications>> = new State<View<Notifications>>({ loader: true });
  private readonly _recentCreatedInvestigations$: State<View<Notifications>> = new State<View<Notifications>>({ loader: true });

  private readonly _recentReceivedAlerts$: State<View<Notifications>> = new State<View<Notifications>>({ loader: true });
  private readonly _recentCreatedAlerts$: State<View<Notifications>> = new State<View<Notifications>>({ loader: true });

  public setDashboardStats(dashboardStatus: View<DashboardStats>): void {
    this._dashboardStats$.update(dashboardStatus);
  }

  public get dashboardStats$(): Observable<View<DashboardStats>> {
    return this._dashboardStats$.observable;
  }

  /**
   * recent notifications getter/setter
   */
  public get recentReceivedInvestigations$(): Observable<View<Notifications>> {
    return this._recentReceivedInvestigations$.observable;
  }

  public setRecentReceivedInvestigations(investigations: View<Notifications>): void {
    this._recentReceivedInvestigations$.update(investigations);
  }

  public get recentCreatedInvestigations$(): Observable<View<Notifications>> {
    return this._recentCreatedInvestigations$.observable;
  }

  public setRecentCreatedInvestigations(investigations: View<Notifications>): void {
    this._recentCreatedInvestigations$.update(investigations);
  }

  public get recentReceivedAlerts$(): Observable<View<Notifications>> {
    return this._recentReceivedAlerts$.observable;
  }

  public setRecentReceivedAlerts(alerts: View<Notifications>): void {
    this._recentReceivedAlerts$.update(alerts);
  }

  public get recentCreatedAlerts$(): Observable<View<Notifications>> {
    return this._recentCreatedAlerts$.observable;
  }

  public setRecentCreatedAlerts(alerts: View<Notifications>): void {
    this._recentCreatedAlerts$.update(alerts);
  }

}
