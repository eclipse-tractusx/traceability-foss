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
import { LayoutComponent } from '@layout/layout/layout.component';
import { AuthGuard } from './auth/auth.guard';
import { ErrorPageType } from '@page/error-page/model/error-page.model';

export /** @type {*} */
const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    canActivate: [AuthGuard],
    data: { breadcrumb: 'home' },
    loadChildren: () => import('./layout/layout.module').then(m => m.LayoutModule),
  },
  {
    // if page not found we use ErrorPageModule + LayoutComponent to easy navigation
    path: '**',
    component: LayoutComponent,
    data: {
      errorPage: {
        type: ErrorPageType.pageNotFound,
      },
    },
    loadChildren: () => import('../page/error-page/error-page.module').then(m => m.ErrorPageModule),
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' })],
  exports: [RouterModule],
  providers: [AuthGuard],
})
export class CoreRoutingModule {}
