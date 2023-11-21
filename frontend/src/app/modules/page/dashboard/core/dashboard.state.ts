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

import {Injectable} from '@angular/core';

import {Notifications} from '@shared/model/notification.model';
import {State} from '@shared/model/state';
import {Observable} from 'rxjs';
import {View} from 'src/app/modules/shared/model/view.model';

@Injectable()
export class DashboardState {

  // part counts
  private readonly _numberOfAsBuiltOwnParts$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfAsPlannedOwnParts$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfAsBuiltSupplierParts$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfAsPlannedSupplierParts$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfAsBuiltCustomerParts$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfAsPlannedCustomerParts$: State<View<number>> = new State<View<number>>({ loader: true });

  // calculated part counts
  private readonly _numberOfTotalMyParts$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfTotalOtherParts$: State<View<number>> = new State<View<number>>({ loader: true });

  // notification counts
  private readonly _numberOfMyPartsWithOpenAlerts$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfMyPartsWithOpenInvestigations$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfOtherPartsWithOpenAlerts$: State<View<number>> = new State<View<number>>({ loader: true });
  private readonly _numberOfOtherPartsWithOpenInvestigations$: State<View<number>> = new State<View<number>>({ loader: true });


  // notifications received
  private readonly _recentInvestigations$: State<View<Notifications>> = new State<View<Notifications>>({ loader: true });
  private readonly _recentAlerts$: State<View<Notifications>> = new State<View<Notifications>>({ loader: true });

  /**
   * part counts getter/setter
   */

  public get numberOfAsBuiltOwnParts$(): Observable<View<number>> {
    return this._numberOfAsBuiltOwnParts$.observable;
  }

  public setNumberOfAsBuiltOwnParts(count: View<number>): void {
    this._numberOfAsBuiltOwnParts$.update(count);
  }

  public get numberOfAsPlannedOwnParts$(): Observable<View<number>> {
    return this._numberOfAsPlannedOwnParts$.observable;
  }

  public setNumberOfAsPlannedOwnParts(count: View<number>): void {
    this._numberOfAsPlannedOwnParts$.update(count);
  }

  public get numberOfAsBuiltSupplierParts$(): Observable<View<number>> {
    return this._numberOfAsBuiltSupplierParts$.observable;
  }

  public setNumberOfAsBuiltSupplierParts(count: View<number>): void {
    this._numberOfAsBuiltSupplierParts$.update(count);
  }

  public get numberOfAsPlannedSupplierParts$(): Observable<View<number>> {
    return this._numberOfAsPlannedSupplierParts$.observable;
  }

  public setNumberOfAsPlannedSupplierParts(count: View<number>): void {
    this._numberOfAsPlannedSupplierParts$.update(count);
  }

  public get numberOfAsBuiltCustomerParts$(): Observable<View<number>> {
    return this._numberOfAsBuiltCustomerParts$.observable;
  }

  public setNumberOfAsBuiltCustomerParts(count: View<number>): void {
    this._numberOfAsBuiltCustomerParts$.update(count);
  }

  public get numberOfAsPlannedCustomerParts$(): Observable<View<number>> {
    return this._numberOfAsPlannedCustomerParts$.observable;
  }

  public setNumberOfAsPlannedCustomerParts(count: View<number>): void {
    this._numberOfAsPlannedCustomerParts$.update(count);
  }

  public get numberOfTotalMyParts$(): Observable<View<number>> {
    return this._numberOfTotalMyParts$.observable;
  }

  public setNumberOfTotalMyParts(count: View<number>): void {
    this._numberOfTotalMyParts$.update(count);
  }

  public get numberOfTotalOtherParts$(): Observable<View<number>> {
    return this._numberOfTotalOtherParts$.observable;
  }

  public setNumberOfTotalOtherParts(count: View<number>): void {
    this._numberOfTotalOtherParts$.update(count);
  }


  /**
   * part notifications getter/setter
   */


  public get numberOfMyPartsWithOpenAlerts$(): Observable<View<number>> {
    return this._numberOfMyPartsWithOpenAlerts$.observable;
  }

  public setNumberOfMyPartsWithOpenAlerts(count: View<number>): void {
    this._numberOfMyPartsWithOpenAlerts$.update(count);
  }

  public get numberOfMyPartsWithOpenInvestigations$(): Observable<View<number>> {
    return this._numberOfMyPartsWithOpenInvestigations$.observable;
  }

  public setNumberOfMyPartsWithOpenInvestigations(count: View<number>): void {
    this._numberOfMyPartsWithOpenInvestigations$.update(count);
  }

  public get numberOfOtherPartsWithOpenAlerts$(): Observable<View<number>> {
    return this._numberOfOtherPartsWithOpenAlerts$.observable;
  }

  public setNumberOfOtherPartsWithOpenAlerts(count: View<number>): void {
    this._numberOfOtherPartsWithOpenAlerts$.update(count);
  }

  public get numberOfOtherPartsWithOpenInvestigations$(): Observable<View<number>> {
    return this._numberOfOtherPartsWithOpenInvestigations$.observable;
  }

  public setNumberOfOtherPartsWithOpenInvestigations(count: View<number>): void {
    this._numberOfOtherPartsWithOpenInvestigations$.update(count);
  }

  /**
   * recent notifications getter/setter
   */


  public get recentInvestigations$(): Observable<View<Notifications>> {
    return this._recentInvestigations$.observable;
  }

  public setInvestigationsReceived(investigations: View<Notifications>): void {
    this._recentInvestigations$.update(investigations);
  }

  public get recentAlerts$(): Observable<View<Notifications>> {
    return this._recentAlerts$.observable;
  }

  public setRecentAlerts(alerts: View<Notifications>): void {
    this._recentAlerts$.update(alerts);
  }
}
