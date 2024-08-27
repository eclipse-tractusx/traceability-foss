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

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {
  ABOUT_BASE_ROUTE,
  ADMIN_BASE_ROUTE,
  ALERT_BASE_ROUTE,
  DASHBOARD_BASE_ROUTE,
  IMPRINT_BASE_ROUTE,
  INVESTIGATION_BASE_ROUTE,
  NO_PERMISSION_BASE_ROUTE,
  OTHER_PARTS_BASE_ROUTE,
  PARTS_BASE_ROUTE,
} from '@core/known-route';
import { RoleGuard } from '@core/user/role.guard';
import { Role } from '@core/user/role.model';
import { AdminComponent } from '@page/admin/presentation/admin.component';
import { ErrorPageType } from '@page/error-page/model/error-page.model';
import { I18NEXT_NAMESPACE_RESOLVER } from 'angular-i18next';

export /** @type {*} */
  // every page (except error pages) require at least "user" role
  // (to be able to detect unauthorized user and redirect to error page)
  const routes: Routes = [
    {
      path: '',
      pathMatch: 'full',
      redirectTo: 'dashboard',
    },
    {
      path: NO_PERMISSION_BASE_ROUTE,
      loadChildren: () => import('@page/error-page/error-page.module').then(m => m.ErrorPageModule),
      data: {
        errorPage: {
          type: ErrorPageType.noPermissions,
        },
      },
    },
    {
      path: DASHBOARD_BASE_ROUTE,
      loadChildren: () => import('../../page/dashboard/dashboard.module').then(m => m.DashboardModule),
      data: {
        breadcrumb: 'home',
        roles: [Role.USER, Role.ADMIN],
      },
      canActivate: [RoleGuard],
    },
    {
      path: PARTS_BASE_ROUTE,
      loadChildren: () => import('../../page/parts/parts.module').then(m => m.PartsModule),
      data: {
        breadcrumb: 'parts',
        roles: [Role.USER, Role.ADMIN],
      },
      canActivate: [RoleGuard],
    },
    {
      path: OTHER_PARTS_BASE_ROUTE,
      loadChildren: () => import('@page/other-parts/other-parts.module').then(m => m.OtherPartsModule),
      data: {
        breadcrumb: 'otherParts',
        roles: [Role.USER, Role.ADMIN],
      },
      canActivate: [RoleGuard],
    },
    {
      path: INVESTIGATION_BASE_ROUTE,
      loadChildren: () => import('../../page/investigations/investigations.module').then(m => m.InvestigationsModule),
      data: {
        breadcrumb: 'investigations',
        roles: [Role.USER, Role.ADMIN],
      },
      canActivate: [RoleGuard],
    },
    {
      path: ALERT_BASE_ROUTE,
      loadChildren: () => import('../../page/alerts/alerts.module').then(m => m.AlertsModule),
      data: {
        breadcrumb: 'alerts',
        roles: [Role.USER, Role.ADMIN],
      },
      canActivate: [RoleGuard],
    },
    {
      path: ABOUT_BASE_ROUTE,
      loadChildren: () => import('../../page/about/about.module').then(m => m.AboutModule),
      data: {
        breadcrumb: 'about',
        roles: [Role.USER, Role.ADMIN],
      },
      canActivate: [RoleGuard],
    },
    {
      path: IMPRINT_BASE_ROUTE,
      loadChildren: () => import('../../page/imprint/imprint.module').then(m => m.ImprintModule),
      data: {
        breadcrumb: 'imprint',
        roles: [Role.USER, Role.ADMIN],
      },
      canActivate: [RoleGuard],
    },
    {
      path: ADMIN_BASE_ROUTE,
      loadChildren: () => import('../../page/admin/admin.module').then(m => m.AdminModule),
      component: AdminComponent,
      data: {
        i18nextNamespaces: ['page.admin'],
        breadcrumb: 'admin',
        roles: [Role.ADMIN],
      },
      resolve: { i18next: I18NEXT_NAMESPACE_RESOLVER },
      canActivate: [RoleGuard],
    },
  ];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LayoutRoutingModule { }
