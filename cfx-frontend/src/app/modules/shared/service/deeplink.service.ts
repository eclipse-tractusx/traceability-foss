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
import { ALERT_BASE_ROUTE, INVESTIGATION_BASE_ROUTE } from '@core/known-route';
import { DeeplinkModel } from '@shared/model/deeplink.model';
import { NotificationColumn } from '@shared/model/notification.model';

@Injectable({
  providedIn: 'root',
})

export class DeeplinkService {
  public getDeeplink(column: NotificationColumn, data: any[]): DeeplinkModel {
    let route;
    let received;
    let tabIndex;
    switch (column) {
      case NotificationColumn.RECEIVED_ALERT: {
        received = true;
        route = ALERT_BASE_ROUTE;
        tabIndex = 0;
        break;
      }
      case NotificationColumn.SENT_ALERT: {
        received = false;
        route = ALERT_BASE_ROUTE;
        tabIndex = 1;
        break;
      }
      case NotificationColumn.RECEIVED_INVESTIGATION: {
        received = true;
        route = INVESTIGATION_BASE_ROUTE;
        tabIndex = 0;
        break;
      }
      case NotificationColumn.SENT_INVESTIGATION: {
        received = false;
        route = INVESTIGATION_BASE_ROUTE;
        tabIndex = 1;
        break;
      }
    }
    return { route, received, tabIndex, data };
  }

}
