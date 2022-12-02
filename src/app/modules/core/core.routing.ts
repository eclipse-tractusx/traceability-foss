/********************************************************************************
 * Copyright (c) 2022,2023
 *        2022: Bayerische Motoren Werke Aktiengesellschaft (BMW AG)
 *        2022: ZF Friedrichshafen AG
 * Copyright (c) 2022,2023 Contributors to the Eclipse Foundation
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
import { PrivateLayoutComponent } from '@layout/private-layout/private-layout.component';
import { PageNotFoundComponent } from '@page/page-not-found/presentation/page-not-found.component';
import { defaultRealm, realm } from './api/api.service.properties';
import { AuthGuard } from './auth/auth.guard';

export /** @type {*} */
const routes: Routes = [
  {
    path: '',
    redirectTo: defaultRealm,
    pathMatch: 'full',
  },
  {
    path: realm || defaultRealm,
    component: PrivateLayoutComponent,
    canActivate: [AuthGuard],
    data: { breadcrumb: 'home' },
    loadChildren: () => import('./layout/layout.module').then(m => m.LayoutModule),
  },
  {
    path: '**',
    component: PageNotFoundComponent,
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', relativeLinkResolution: 'legacy' })],
  exports: [RouterModule],
  providers: [AuthGuard],
})
export class CoreRoutingModule {}
