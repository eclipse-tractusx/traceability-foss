/*
 * Copyright 2021 The PartChain Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

export /** @type {*} */
const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    loadChildren: () => import('../page/dashboard/dashboard.module').then(m => m.DashboardModule),
  },

  {
    path: 'dashboard',
    loadChildren: () => import('../page/dashboard/dashboard.module').then(m => m.DashboardModule),
    data: { breadcrumb: 'Home' },
  },

  {
    path: 'about',
    loadChildren: () => import('../page/about/about.module').then(m => m.AboutModule),
    data: { breadcrumb: 'About' },
  },
];

/**
 *
 *
 * @export
 * @class LayoutRoutingModule
 */
@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class LayoutRoutingModule {}
