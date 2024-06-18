/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { NotificationEditComponent } from '@page/notifications/detail/edit/notification-edit.component';
import { NotificationDetailComponent } from '@page/notifications/detail/notification-detail.component';
import { NotificationsComponent } from '@page/notifications/presentation/notifications.component';
import { I18NEXT_NAMESPACE_RESOLVER } from 'angular-i18next';

export /** @type {*} */
const NOTIFICATIONS_ROUTING: Routes = [
  {
    path: '',
    pathMatch: 'full',
    component: NotificationsComponent,
    data: { i18nextNamespaces: [ 'page.alert' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
  },
  {
    path: 'create',
    pathMatch: 'full',
    component: NotificationEditComponent,
    data: { i18nextNamespaces: [ 'page.alert' ], roles: [ 'user' ] },
    //canActivate: [ RoleGuard ],
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
  },
  {
    path: ':notificationId',
    pathMatch: 'full',
    component: NotificationDetailComponent,
    data: { i18nextNamespaces: [ 'page.alert' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
  },
  {
    path: ':notificationId/edit',
    //canActivate: [ RoleGuard ],
    pathMatch: 'full',
    component: NotificationEditComponent,
    data: { i18nextNamespaces: [ 'page.alert' ], roles: [ 'user' ] },
    resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
  },
];

@NgModule({
  imports: [ RouterModule.forChild(NOTIFICATIONS_ROUTING) ],
  exports: [ RouterModule ],
})
export class NotificationsRoutingModule {
}

