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

export interface DeeplinkNotificationFilter {
  receivedFilter: DeeplinkAssetNotificationIds,
  sentFilter: DeeplinkAssetNotificationIds
}

export interface DeeplinkAssetNotificationIds {
  notificationIds: string[];
}

export function createDeeplinkNotificationFilter(params: any): DeeplinkNotificationFilter {
  let receivedFilter = null;
  let sentFilter = null;
  if (params.deeplink) {
    if (params.received === 'true' && params?.notificationIds?.length > 0) {
      receivedFilter = { notificationIds: params.notificationIds };
    }
    if (params.received === 'false' && params?.notificationIds?.length > 0) {
      sentFilter = { notificationIds: params.notificationIds };
    }
    return { receivedFilter, sentFilter };
  }
}
