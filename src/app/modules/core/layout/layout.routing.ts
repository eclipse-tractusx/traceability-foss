/********************************************************************************
 * Copyright (c) 2021,2022 Contributors to the CatenaX (ng) GitHub Organisation
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
import { RoleGuard } from '@core/user/role.guard';
import { ABOUT_BASE_ROUTE } from '@page/about/about-route';
import { ADMIN_BASE_ROUTE } from '@page/admin/admin-route';
import { DASHBOARD_BASE_ROUTE } from '@page/dashboard/dashboard-route';
import { INVESTIGATION_BASE_ROUTE } from '@page/investigations/investigations-external-route';
import { OTHER_PARTS_BASE_ROUTE } from '@page/other-parts/other-parts-route';
import { PARTS_BASE_ROUTE } from '@page/parts/parts-route';

export /** @type {*} */
const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'dashboard',
  },
  {
    path: DASHBOARD_BASE_ROUTE,
    loadChildren: () => import('../../page/dashboard/dashboard.module').then(m => m.DashboardModule),
    data: {
      breadcrumb: 'home',
    },
  },
  {
    path: PARTS_BASE_ROUTE,
    loadChildren: () => import('../../page/parts/parts.module').then(m => m.PartsModule),
    data: {
      breadcrumb: 'parts',
    },
  },
  {
    path: OTHER_PARTS_BASE_ROUTE,
    loadChildren: () => import('@page/other-parts/other-parts.module').then(m => m.OtherPartsModule),
    data: {
      breadcrumb: 'otherParts',
    },
    canActivate: [RoleGuard],
  },
  {
    path: INVESTIGATION_BASE_ROUTE,
    loadChildren: () => import('../../page/investigations/investigations.module').then(m => m.InvestigationsModule),
    data: {
      breadcrumb: 'investigations',
    },
    canActivate: [RoleGuard],
  },
  {
    path: ABOUT_BASE_ROUTE,
    loadChildren: () => import('../../page/about/about.module').then(m => m.AboutModule),
    data: {
      breadcrumb: 'about',
    },
  },
  {
    path: ADMIN_BASE_ROUTE,
    loadChildren: () => import('../../page/admin/admin.module').then(m => m.AdminModule),
    data: {
      breadcrumb: 'admin',
      roles: ['admin'],
    },
    canActivate: [RoleGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LayoutRoutingModule {}
