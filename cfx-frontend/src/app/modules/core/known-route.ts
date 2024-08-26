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

import { NotificationStatusGroup } from '@shared/model/notification.model';
import { PageRoute } from '@shared/model/page-route.model';

export const PARTS_BASE_ROUTE = 'parts';
export const DASHBOARD_BASE_ROUTE = 'dashboard';
export const ADMIN_BASE_ROUTE = 'admin';
export const ABOUT_BASE_ROUTE = 'about';
export const NOTIFICATION_BASE_ROUTE = 'inbox';
export const NO_PERMISSION_BASE_ROUTE = 'no-permissions';

export const NavigableUrls = [
  PARTS_BASE_ROUTE,
  DASHBOARD_BASE_ROUTE,
  ADMIN_BASE_ROUTE,
  ABOUT_BASE_ROUTE,
  NOTIFICATION_BASE_ROUTE,
] as const;

export type KnownUrl = (typeof NavigableUrls)[number];

export const getRoute = (urlType: KnownUrl, ...args): PageRoute => {
  if (urlType === NOTIFICATION_BASE_ROUTE) {
    return getNotificationInboxRoute(urlType, ...args);
  }

  return { link: urlType };
};

const getNotificationInboxRoute = (
  urlType: KnownUrl,
  notificationStatusGroup?: NotificationStatusGroup,
): PageRoute => ({
  link: urlType,
  queryParams: notificationStatusGroup
    ? {
      tabIndex: notificationStatusGroup === NotificationStatusGroup.RECEIVED ? '1' : '0',
    }
    : undefined,
});
